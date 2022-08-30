package com.jolydrone.flightmonitor.services;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolydrone.flightmonitor.configurations.Config;
import com.jolydrone.flightmonitor.entity.DeliveryStatus;
import com.jolydrone.flightmonitor.entity.Drone;
import com.jolydrone.flightmonitor.entity.SpecialEvent;
import com.rabbitmq.client.Channel;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.logging.Logger;

@Service
public class SpecialEventMonitorWS implements ChannelAwareMessageListener {

    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(SpecialEventMonitorWS.class.getName());
    private final RestTemplate restTemplate;

    @Value("${delivery.planner.url}")
    private String deliveryPlannerUrl;

    @Value("${maintenance.url}")
    private String maintenanceUrl;

    @Autowired
    @Qualifier("dataTemplate")
    private RedisTemplate<String, Object> redisTemplateSpecialEvent;

    @Autowired
    private WriteToCommonDatabase writeToCommonDatabase;

    @Autowired
    public SpecialEventMonitorWS(Jackson2ObjectMapperBuilder builder, RestTemplateBuilder restTemplateBuilder
    ) {
        this.objectMapper = builder.build();
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    @Retry(name="externalService", fallbackMethod = "fallBack")
    public void onMessage(Message message, Channel channel) {

        var eventJson = new String(message.getBody());
        this.logger.info("Receive special events.");
        try {

            var event = this.objectMapper.readValue(eventJson, SpecialEvent.class);

            this.logger.info(String.format("Event of tracking number %s.", event.getTrackingNumber()));

            var delivery = event.getDelivery();

            switch (event.getType()) {
                case "DELIVERY_START":

                    delivery.setDeliveryStatus(DeliveryStatus.ON_GOING);
                    this.redisTemplateSpecialEvent.opsForList().leftPush(Config.PROGRESS_DB_KEY, delivery);

                    this.logger.info("Marked delivery " + delivery.getTrackingNumber() + " as ON_GOING");
                    this.logger.info("Sending drone update to delivery planner service...");

                    var response = this.restTemplate.getForEntity(
                            this.deliveryPlannerUrl + "/drones/{name}/status/{droneStatus}", Drone.class,
                            delivery.getDrone().getName(), "DELIVERING");

                    if (response.getStatusCode() == HttpStatus.OK) {
                        this.logger.info("Drone status updated");
                    } else {
                        this.logger.warning("Request failed " + response.getStatusCode());
                    }
                    break;

                case "DELIVERY_FINISHED":

                    delivery.setDeliveryStatus(DeliveryStatus.DELIVERED);
                    writeToCommonDatabase.saveDeliveryDone(delivery);

                    this.logger.info("Marked delivery " + delivery.getTrackingNumber() + " as DELIVERED");

                    this.logger.info("Sending drone update to delivery planner service...");

                    var responseDroneStatus = this.restTemplate.getForEntity(
                            this.deliveryPlannerUrl + "/drones/{name}/status/{droneStatus}", Drone.class,
                            delivery.getDrone().getName(), "AVAILABLE");

                    if (responseDroneStatus.getStatusCode() == HttpStatus.OK) {
                        this.logger.info("Drone status updated");
                    } else {
                        this.logger.warning("Request failed " + responseDroneStatus.getStatusCode());
                    }

                    break;
                case "DRONE_BROKEN":
                    Drone drone = delivery.getDrone();

                    this.logger.info("Changing drone of delivery " + delivery.getTrackingNumber() + " as BROKEN");

                    var responseBrokenDrone = this.restTemplate.getForEntity(
                            this.deliveryPlannerUrl + "/drones/{name}/status/{droneStatus}", Drone.class,
                            drone.getName(), "BROKEN");

                    if (responseBrokenDrone.getStatusCode() == HttpStatus.OK) {
                        this.logger.info("Drone status changed  " + responseBrokenDrone.getBody());
                        var responseAskForRepair = this.restTemplate.postForEntity(
                                this.maintenanceUrl + "/j/drone",
                                drone, Drone.class);
                        if (responseBrokenDrone.getStatusCode() == HttpStatus.OK) {
                            this.logger.info("Drone status changed  " + responseAskForRepair.getBody());
                        } else {
                            this.logger
                                    .warning("Request failed " + responseAskForRepair.getStatusCode() + " body "
                                            + responseAskForRepair.getBody());
                        }
                    } else {
                        this.logger
                                .warning("Request failed " + responseBrokenDrone.getStatusCode() + " body "
                                        + responseBrokenDrone.getBody());
                    }

                    break;
                case "STATION_BROKEN":
                    break;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void fallBack(Message message, Channel channel, Throwable throwable){
        logger.log(Level.SEVERE, "Could not send request.");
    }
}
