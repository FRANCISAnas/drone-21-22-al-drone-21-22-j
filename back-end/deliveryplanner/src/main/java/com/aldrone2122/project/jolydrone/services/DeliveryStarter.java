package com.aldrone2122.project.jolydrone.services;

import com.aldrone2122.project.jolydrone.adapters.DtoAdapter;
import com.aldrone2122.project.jolydrone.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

@Component
public class DeliveryStarter {

    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(DeliveryStarter.class.getName());
    @Value("${flight.monitor.gateway}")
    private String gateway;

    @Autowired
    private DtoAdapter adapterDTO;

    @Autowired
    public DeliveryStarter(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public void startDelivery(Delivery delivery) {
        this.logger.info(String.format("Sending delivery with trackingNumber %s to flight monitor.", delivery.getTrackingNumber()));

        var dtoDelivery = new DeliveryDTO(this.adapterDTO.transformToDTOWithoutId(delivery.getFlightPlan()),
                delivery.getDeliveryStatus(), delivery.getTrackingNumber(),
                new PackageDTO(delivery.getPaquet().getWeight(), delivery.getPaquet().getDestination(),
                        delivery.getPaquet().getDimensions()),
                new DroneDTO(delivery.getDrone().getName(), delivery.getDrone().getUnloadedWeight(),
                        delivery.getDrone().getAutonomyMaximum(), delivery.getDrone().getBatteryLevelInPercent(),
                        delivery.getDrone().getStatus(), delivery.getDrone().getMaxWeighingCapacity()));

        try {
            long lStartTime = System.currentTimeMillis();

            var response = this.restTemplate.postForEntity(gateway + "/flight-monitor/flight/start", dtoDelivery, String.class);
            long lEndTime = System.currentTimeMillis();
            long elapsed = lEndTime - lStartTime;
            logger.info("Send delivery to flight monitor  " + response.getStatusCode() + " " + response.getBody());
            logger.info(" ===== The gateway spent :  " + elapsed + " milli seconds to forward this delivery to the correct flightmonitor ==== ");

        } catch (RestClientException e) {
            logger.severe("Oops something went wrong while asking flight monitor to start delivery.");
            e.printStackTrace();
        }
    }


    /**
     * DeliveryDTO
     */
    public record DeliveryDTO(FlightPlanDTO flightPlan, DeliveryStatus deliveryStatus, String trackingNumber,
                              PackageDTO paquet, DroneDTO drone) {

        @Override
        public String toString() {
            return "DeliveryDTO{" + "\"flightPlan:\"" + flightPlan + "\", deliveryStatus:\"" + deliveryStatus
                    + "\", trackingNumber:\"'" + trackingNumber + '\"' + ", paquet:\"" + paquet + "\", drone:\"" + drone + "\"}";
        }
    }

    /**
     * DroneDTO
     */
    public record DroneDTO(String name, Integer unloadedWeight, Integer autonomyMaximum, Integer batteryLevelInPercent,
                           DroneStatus status, Integer maxWeighingCapacity) {
    }

    /**
     * PackageDTO
     */
    public record PackageDTO(Double weight, Coordinates destination, Dimensions dimensions) {
    }

    /**
     * FlightPlanDTO
     */
    public record FlightPlanDTO(Coordinates origin, Coordinates destination, List<StopoverDTO> refillStops) {

        @Override
        public String toString() {
            return "FlightPlanDTO{" + "\"origin:\"" + origin + "\", destination:\"" + destination + "\", refillStops:\""
                    + refillStops + '}';
        }
    }

    public record StopoverDTO(StationDTO chargingStation, StopoverStatus stopoverStatus) {
    }

    /**
     * StationDTO
     */
    public record StationDTO(int terminalCount, List<TerminalDTO> terminals, String stationName, Coordinates location) {

    }

    /**
     * TerminalDTO TerminalStatus status
     */
    public record TerminalDTO(TerminalStatus status, int idForStation) {
    }
}
