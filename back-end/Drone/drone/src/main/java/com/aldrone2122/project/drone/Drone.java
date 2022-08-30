package com.aldrone2122.project.drone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Drone {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private Long id;
    private String name;
    private int unloadedWeight;
    private int autonomyMaximum;
    private int batteryLevelInPercent;
    private int maximumWeighingCapacity;
    private DroneStatus status;
    private Coordinates position;
    private Channel launchChannel;

    public Drone() {
    }

    public Drone(String name, int unloadedWeight, int autonomyMaximum, int batteryLevelInPercent, DroneStatus status,
                 int maximumWeighingCapacity) {
        this.name = name;
        this.unloadedWeight = unloadedWeight;
        this.autonomyMaximum = autonomyMaximum;
        this.batteryLevelInPercent = batteryLevelInPercent;
        this.status = status;
        this.maximumWeighingCapacity = maximumWeighingCapacity;
    }

    public void start() {
        Thread thread1 = new Thread(() -> {
            try {
                this.setUpLaunchCallBack();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        try {
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setUpLaunchCallBack() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("RABBIT_HOST"));
        factory.setPort(Integer.parseInt(System.getenv("RABBIT_PORT")));
        factory.setRequestedHeartbeat(0);
        Connection connection = factory.newConnection();
        this.launchChannel = connection.createChannel();

        this.launchChannel.exchangeDeclare("dronecom", "topic", true, false, null);
        var result = this.launchChannel.queueDeclare("", true, true, false, null);

        this.launchChannel.queueBind(result.getQueue(), "dronecom", "drone." + this.name + ".launch");
        DeliverCallback deliverCallback = (consumerTag, deliveryBytes) -> {
            String message = new String(deliveryBytes.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received start signal from a FlightMonitor");
            try {

                Delivery delivery = this.mapper.readValue(message, Delivery.class);

                Map<String, Region> regionsMap = this.yamlMapper.readValue(
                    Drone.class.getClassLoader().getResourceAsStream("map.yaml"),
                    new TypeReference<Map<String, Region>>() {
                    });

                regionsMap.forEach((regionName, value) -> value.setName(regionName));
                System.out.println("Executing the delivery flight Plan :");

                delivery.execute(new FlightMonitorCommunicator(new ArrayList<>(regionsMap.values())));

            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        this.launchChannel.basicConsume(result.getQueue(), true, deliverCallback, consumerTag -> {
        });

    }

    @Override
    public String toString() {
        return "Drone [autonomyMaximum=" + autonomyMaximum + ", batteryLevelInPercent=" + batteryLevelInPercent
            + ", id=" + id + ", mapper=" + mapper + ", maximumWeighingCapacity=" + maximumWeighingCapacity
            + ", name=" + name + ", position=" + position + ", status=" + status + ", unloadedWeight="
            + unloadedWeight + "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnloadedWeight() {
        return unloadedWeight;
    }

    public void setUnloadedWeight(int unloadedWeight) {
        this.unloadedWeight = unloadedWeight;
    }

    public int getAutonomyMaximum() {
        return autonomyMaximum;
    }

    public void setAutonomyMaximum(int autonomyMaximum) {
        this.autonomyMaximum = autonomyMaximum;
    }

    public int getBatteryLevelInPercent() {
        return batteryLevelInPercent;
    }

    public void setBatteryLevelInPercent(int batteryLevelInPercent) {
        this.batteryLevelInPercent = batteryLevelInPercent;
    }

    public int getMaximumWeighingCapacity() {
        return maximumWeighingCapacity;
    }

    public void setMaximumWeighingCapacity(int maximumWeighingCapacity) {
        this.maximumWeighingCapacity = maximumWeighingCapacity;
    }

    public DroneStatus getStatus() {
        return status;
    }

    public void setStatus(DroneStatus status) {
        this.status = status;
    }

    public Coordinates getPosition() {
        return position;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public Channel getLaunchChannel() {
        return launchChannel;
    }

    public void setLaunchChannel(Channel launchChannel) {
        this.launchChannel = launchChannel;
    }

}
