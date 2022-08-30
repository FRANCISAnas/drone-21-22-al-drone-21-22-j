package com.jolydrone.flightmonitor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolydrone.flightmonitor.configurations.Config;
import com.jolydrone.flightmonitor.controllers.DroneLauncherController.DeliveryDTO;
import com.jolydrone.flightmonitor.entity.Delivery;
import com.jolydrone.flightmonitor.entity.DeliveryRequest;
import com.jolydrone.flightmonitor.repositories.DeliveryRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class DroneLauncherWS {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Value("delivery.planner.url")
    private String deliveryPlanner;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    @Qualifier("dataTemplate")
    private RedisTemplate<String, Object> redisTemplatdroneLauncher;

    private final Logger logger = Logger.getLogger(DroneLauncherWS.class.getName());

    @Autowired
    public DroneLauncherWS(Jackson2ObjectMapperBuilder builder) {
        this.objectMapper = builder.build();
    }

    public void launchDrone(DeliveryDTO delivery) {
        if (Boolean.TRUE.equals(this.redisTemplatdroneLauncher.opsForHash().hasKey(Config.DELIVERY_PROCESSED, delivery.trackingNumber()))) {
            this.logger.info(String.format("Delivery with trackingNumber %s has already been accepted", delivery.trackingNumber()));
            return;
        } else {
            this.logger.info(String.format("Adding Delivery with trackingNumber %s", delivery.trackingNumber()));
            this.redisTemplatdroneLauncher.opsForHash().putIfAbsent(Config.DELIVERY_PROCESSED, delivery.trackingNumber(), new DeliveryRequest(delivery.trackingNumber()));
        }


        this.logger.info(
                String.format("Launching request to start delivery with trackingNumber %s, from %s to %s.",
                        delivery.trackingNumber(), delivery.flightPlan().getOrigin().toString(), delivery.flightPlan().getDestination().toString()));

        var tmp = new Delivery();
        tmp.setDeliveryStatus(delivery.deliveryStatus());
        tmp.setDrone(delivery.drone());
        tmp.setFlightPlan(delivery.flightPlan());
        tmp.setTrackingNumber(delivery.trackingNumber());
        tmp.setPaquet(delivery.paquet());

        this.logger.info("pushing to redis ...");
        this.redisTemplatdroneLauncher.opsForList().leftPush(Config.PROGRESS_DB_KEY, tmp);

        this.logger.info("Delivery saved!");

        try {
            logger.info(String.format("Signal sent to drone %s via the bus.", delivery.drone().getName()));
            this.logger.info(delivery.drone().getName());
            this.rabbitTemplate.convertAndSend("dronecom", String.format("drone.%s.launch", delivery.drone().getName()),
                    this.objectMapper.writeValueAsString(tmp));

        } catch (JsonProcessingException | AmqpException e) {
            this.logger.severe("Error");
            e.printStackTrace();
        }
    }
}
