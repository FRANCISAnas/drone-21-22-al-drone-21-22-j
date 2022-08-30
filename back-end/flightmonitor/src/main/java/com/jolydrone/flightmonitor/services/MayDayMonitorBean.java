package com.jolydrone.flightmonitor.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolydrone.flightmonitor.entity.*;
import com.jolydrone.flightmonitor.repositories.DeliveryRepository;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class MayDayMonitorBean implements MessageListener {

    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(MayDayMonitorBean.class.getName());
    private final RestTemplate restTemplate;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Value("${maintenance.url}")
    private String maintenanceUrl;

    @Value("${station.manager.url}")
    private String stationManagerUrl;

    @Value("${delivery.planner.url}")
    private String deliveryPlannerUrl;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public MayDayMonitorBean(Jackson2ObjectMapperBuilder builder, RestTemplateBuilder restTemplateBuilder) {
        this.objectMapper = builder.build();
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public void onMessage(Message message) {

        var eventJson = new String(message.getBody());

        try {
            var mayDayDataReceiver = objectMapper.readValue(eventJson, MayDayDataReceiver.class);
            var drone = mayDayDataReceiver.droneAndFlightPlan().drone();
            var flightPlan = mayDayDataReceiver.droneAndFlightPlan().flightPlan();
            var damagedStationName = mayDayDataReceiver.damagedStationName();
            var damagedStationCoordinates = mayDayDataReceiver.damagedStationCoordinates();

            logger.info(String.format("---------- Received may day event from drone %s at %s. ---------- ", drone.getName(), damagedStationName));

            Optional<Delivery> findCutDeliveryOpt = deliveryRepository.findDeliveryByDroneId(drone.getId());
            if (findCutDeliveryOpt.isPresent()) {

                Delivery curDelivery = findCutDeliveryOpt.get();
                drone = curDelivery.getDrone();

                try {
                    logger.info("Notifying damaged station to station manager ...");
                    var stationChargingResponse = this.restTemplate.getForEntity(
                            this.stationManagerUrl + "/j/stations/" + damagedStationName + "/sentForRepair",
                            ChargingStation.class);

                    // logger.info(Objects.requireNonNull(stationChargingResponse.getBody()).toString());
                    if (stationChargingResponse.getStatusCode() == HttpStatus.OK) {
                        // logger.info(Objects.requireNonNull(stationChargingResponse.getBody()).toString());
                        logger.info("Asking for a new path for all delivery that will pass by this station...");
                        try {
                            // TODO check if damaged station is in flight plan
                            var deliveries = deliveryRepository.findAllByDeliveryStatus(DeliveryStatus.ON_GOING);
                            for (Delivery d : deliveries) {
                                var dFlightPlan = d.getFlightPlan();

                                System.out.println(d.getTrackingNumber() + curDelivery.getTrackingNumber()
                                        + Objects.equals(d.getTrackingNumber(), curDelivery.getTrackingNumber()));

                                logger.info(
                                        "Requesting delivery planner a new flight plan for delivery with tracking number: "
                                                + d.getTrackingNumber());
                                Coordinates currentCoordinates = null;
                                for (int i = 0; i < curDelivery.getFlightPlan().getRefillStops().size() - 1; i++) {
                                    if (curDelivery.getFlightPlan().getRefillStops().get(i + 1)
                                            .getStopoverStatus() == StopoverStatus.PENDING) {
                                        currentCoordinates = curDelivery.getFlightPlan().getRefillStops().get(i)
                                                .getChargingStation().getLocation();
                                        break;
                                    }
                                }

                                var flightPlanResponse = this.restTemplate.postForEntity(
                                        this.deliveryPlannerUrl + "/drones/{droneName}/newFlightPlan/{currentCapity}",
                                        new OriginDestinationDrone(currentCoordinates,
                                                d.getFlightPlan().getDestination()),
                                        FlightPlan.class, d.getDrone().getName(), d.getDrone().getAutonomyMaximum());
                                var newFlightPlan = flightPlanResponse.getBody();
                                if (flightPlanResponse.getStatusCode() == HttpStatus.OK) {
                                    logger.info("New Flight plan gotten " + (newFlightPlan.getRefillStops() != null
                                            ? newFlightPlan.getRefillStops().size()
                                            : 0) + " stops.");

                                    logger.info("Setting the rest of charging stations as 'IGNORED'");

                                    int i = 0;
                                    while (i < dFlightPlan.getRefillStops().size() && !Objects.equals(
                                            dFlightPlan.getRefillStops().get(i).getChargingStation().getStationName(),
                                            damagedStationName))
                                        i++;
                                    while (i < dFlightPlan.getRefillStops().size() && dFlightPlan.getRefillStops()
                                            .get(i).getStopoverStatus() != StopoverStatus.PENDING)
                                        i++;

                                    for (; i < dFlightPlan.getRefillStops().size(); i++) {
                                        dFlightPlan.getRefillStops().get(i).setStopoverStatus(StopoverStatus.IGNORED);
                                    }

                                    logger.info("Adding new stops to the flight plan");
                                    if (newFlightPlan != null) {
                                        logger.info("Updating the flight plan of the delivery.");

                                        FlightPlan duplicateFlightPlan = new FlightPlan(dFlightPlan);
                                        newFlightPlan.getRefillStops().forEach(stopover -> {
                                            // stopover.setId(UUID.randomUUID().getMostSignificantBits() &
                                            // Long.MAX_VALUE);
                                            duplicateFlightPlan.getRefillStops()
                                                    .add(new Stopover(stopover.getChargingStation()));
                                        });

                                        d.setFlightPlan(duplicateFlightPlan);
                                        try {
                                            this.logger.info("---------- Updating flight plan of drone: '. ---------- " + d.getDrone().getName() + "'");
                                            this.rabbitTemplate.convertAndSend("dronecom", String.format("drone.%s.update_flight_plan", d.getDrone().getName()),
                                                    this.objectMapper.writeValueAsString(duplicateFlightPlan));
                                        } catch (JsonProcessingException | AmqpException e) {
                                            this.logger.severe(
                                                    "Oops something went wrong while dealing with the may day event.");
                                            // TODO Auto-generated catch block
                                            // e.printStackTrace();
                                        }
                                        try {
                                            // logger.info(dFlightPlan.toString());
                                            deliveryRepository.save(d);// TODO FIX non duplicate reffills stops
                                        } catch (Exception e) {
                                            // logger.severe("Error while updating the delivery.");
                                            //// e.printStackTrace();
                                        }
                                    } else {
                                        logger.warning("No flight plan found!");
                                    }
                                } else {
                                    logger.warning(String.format(
                                            "Failed to get a new flight plan for delivery with tracking number %s and drone %s",
                                            d.getTrackingNumber(), d.getDrone().getName()));
                                }
                            }

                        } catch (RestClientException e) {
                            logger.info("Post request for getting new flightPlan failed...");
                            // e.printStackTrace();
                        }
                    }
                } catch (RestClientException e) {
                    logger.info("Post request for changing station status failed...");
                    // e.printStackTrace();
                }

                try {
                    logger.info("Sending message for station repairement...");
                    var maintenanceResponse = this.restTemplate.postForEntity(this.maintenanceUrl + "/j/station",
                            damagedStationName, String.class);
                    logger.info("Repair message: " + maintenanceResponse.getBody());
                } catch (RestClientException e) {
                    logger.info("Post request for station repairement failed...");
                    // e.printStackTrace();
                }

            }

        } catch (JsonProcessingException e) {
            // e.printStackTrace();
        }

    }

    record MayDayDataReceiver(UpdatePositionMonitorBean.DroneAndFlightPlan droneAndFlightPlan,
                              String damagedStationName, Coordinates damagedStationCoordinates) {

    }

    public record OriginDestinationDrone(Coordinates origin, Coordinates destination) {

    }

}
