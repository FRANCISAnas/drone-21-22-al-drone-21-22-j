package com.aldrone2122.project.drone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RedisTest {

    private Channel updateChannel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().getOrDefault("RABBIT_HOST", "localhost"));
        factory.setPort(Integer.parseInt(System.getenv().getOrDefault("RABBIT_PORT", "5672")));
        factory.setRequestedHeartbeat(0);

        try {
            Connection connection = factory.newConnection();
            this.updateChannel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    void testRedis() throws IOException {
        try {
            Delivery delivery = this.objectMapper.readValue(this.getClass().getClassLoader().getResourceAsStream("delivery-test.json"), Delivery.class);
            for (int i = 0; i < 250; i++) {
                Thread.sleep(500);
                delivery.getDrone().setPosition(new Coordinates(400.0 - i * 80 / 50, -100.0 + i * 50 / 50));
                this.updateChannel.basicPublish("dronecom", "drone.regular.update", null,
                    objectMapper.writeValueAsString(delivery).getBytes());
            }
        } catch (
            IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // delivery.execute();
    }

}
