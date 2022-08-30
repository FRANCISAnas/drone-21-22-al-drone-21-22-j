package com.aldrone2122.project.drone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

public class FlightMonitorCommunicator implements IFlightMonitorCommunicator {

    static final String EXCHANGE_NAME = "dronecom";

    private Channel updateChannel;

    private final List<Region> regions;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentNavigableMap<Long, MessageInfo> outstandingConfirms = new ConcurrentSkipListMap<>();

    private final ConcurrentLinkedQueue<String> messageToRepublished = new ConcurrentLinkedQueue<>();

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public FlightMonitorCommunicator(List<Region> regions) {
        this.regions = regions;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("RABBIT_HOST"));
        factory.setPort(Integer.parseInt(System.getenv("RABBIT_PORT")));
        factory.setRequestedHeartbeat(0);

        try {
            Connection connection = factory.newConnection();
            this.updateChannel = connection.createChannel();
            this.updateChannel.confirmSelect();
            ConfirmCallback cleanOutstandingConfirms = (sequenceNumber, multiple) -> {
                if (!outstandingConfirms.containsKey(sequenceNumber)) {
                    return;
                }
                if (multiple) {
                    ConcurrentNavigableMap<Long, MessageInfo> confirmed = outstandingConfirms.headMap(
                        sequenceNumber, true);
                    confirmed.clear();
                } else {
                    outstandingConfirms.remove(sequenceNumber);
                }
            };

            this.updateChannel.addConfirmListener(cleanOutstandingConfirms, (sequenceNumber, multiple) -> {
                MessageInfo messageInfo = outstandingConfirms.get(sequenceNumber);
                System.err.format(
                    "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                    messageInfo, sequenceNumber, multiple);
                // we try to republish the event
                System.out.println(Thread.currentThread().getName());
                pcs.firePropertyChange("NEW_REPUBLISH", null, messageInfo);
                System.out.println("After firePropertyChange");
                cleanOutstandingConfirms.handle(sequenceNumber, multiple);

            });
        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void updateMonitor(Delivery delivery) {
        delivery.setTimeStamp(LocalDateTime.now());
        delivery.setClock(delivery.getClock() + 1);
        String routingKey = regions.stream().filter(region -> region.isInside(delivery.getFlightPlan().getCurrentPos()))
            .findFirst().get().getUpdateRoute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        try {
            this.updateChannel.basicPublish(EXCHANGE_NAME, routingKey, null,
                objectMapper.writeValueAsString(delivery).getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void finishedDelivery(Delivery delivery) {
        delivery.setTimeStamp(LocalDateTime.now());
        delivery.setClock(delivery.getClock() + 1);
        String routingKey = regions.stream().filter(region -> region.isInside(delivery.getFlightPlan().getCurrentPos()))
            .findFirst().get().getSpecialRoute();

        try {
            String message = objectMapper
                .writeValueAsString(new SpecialEvent("DELIVERY_FINISHED", delivery.getTrackingNumber(), delivery));
            outstandingConfirms.put(this.updateChannel.getNextPublishSeqNo(), new MessageInfo(message, routingKey));
            this.updateChannel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startDelivery(Delivery delivery) {
        delivery.setTimeStamp(LocalDateTime.now());
        delivery.setClock(0);
        String routingKey = regions.stream().filter(region -> region.isInside(delivery.getFlightPlan().getCurrentPos()))
            .findFirst().get().getSpecialRoute();
        try {
            String message = objectMapper
                .writeValueAsString(new SpecialEvent("DELIVERY_START", delivery.getTrackingNumber(), delivery));

            outstandingConfirms.put(this.updateChannel.getNextPublishSeqNo(), new MessageInfo(message, routingKey));

            this.updateChannel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    record MessageInfo(String body, String routingKey) {

    }

}
