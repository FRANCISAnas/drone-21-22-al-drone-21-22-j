package com.jolydrone.flightmonitor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolydrone.flightmonitor.configurations.Config;
import com.jolydrone.flightmonitor.entity.Delivery;
import com.jolydrone.flightmonitor.entity.Drone;
import com.jolydrone.flightmonitor.entity.FlightPlan;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Component
public class UpdatePositionMonitorBean implements MessageListener {

    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(UpdatePositionMonitorBean.class.getName());
    private final RestTemplate restTemplate;
    @Value("${delivery.planner.url}")
    private String deliveryPlannerUrl;

    @Autowired
    @Qualifier("dataTemplate")
    private RedisTemplate<String, Object> redisTemplateUpd;


    @Autowired
    public UpdatePositionMonitorBean(Jackson2ObjectMapperBuilder builder,
                                     RestTemplateBuilder restTemplateBuilder) {
        this.objectMapper = builder.build();
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public void onMessage(Message message) {
        var eventJson = new String(message.getBody());
        try {
            var delivery = objectMapper.readValue(eventJson, Delivery.class);
            this.redisTemplateUpd.opsForList().leftPush(Config.PROGRESS_DB_KEY, delivery);
            logger.info(String.format("Received update for Delivery %s made by drone %s, current position %s", delivery.getTrackingNumber(), delivery.getDrone().getName(), delivery.getDrone().getPosition()));

        } catch (JsonProcessingException e) {
            logger.info("Received wrong format of droneAndFlightPlan.");
        }
    }

    public record DroneAndFlightPlan(Drone drone, FlightPlan flightPlan) {

    }
}
