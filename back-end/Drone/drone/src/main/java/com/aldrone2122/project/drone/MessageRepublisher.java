package com.aldrone2122.project.drone;

import com.aldrone2122.project.drone.FlightMonitorCommunicator.MessageInfo;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageRepublisher implements PropertyChangeListener {

    private Channel republishChannel;

    public MessageRepublisher() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("RABBIT_HOST"));
        factory.setPort(Integer.parseInt(System.getenv("RABBIT_PORT")));
        factory.setRequestedHeartbeat(0);

        try {
            Connection connection = factory.newConnection();
            this.republishChannel = connection.createChannel();
        } catch (IOException | TimeoutException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println(Thread.currentThread().getName());
        MessageInfo messageInfo = (MessageInfo) evt.getNewValue();
        try {
            this.republishChannel.basicPublish(FlightMonitorCommunicator.EXCHANGE_NAME, messageInfo.routingKey(), null, messageInfo.body().getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
