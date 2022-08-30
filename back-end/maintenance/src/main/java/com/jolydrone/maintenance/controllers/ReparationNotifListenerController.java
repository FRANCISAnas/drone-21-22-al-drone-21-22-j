package com.jolydrone.maintenance.controllers;

import com.jolydrone.maintenance.entity.ChargingStation;
import com.jolydrone.maintenance.entity.Drone;
import com.jolydrone.maintenance.services.BreakdownDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@RestController
public class ReparationNotifListenerController {


    private final RestTemplate restTemplate;

    private final Logger logger = Logger.getLogger(ReparationNotifListenerController.class.getName());

    @Value("${delivery.planner.url}")
    private String deliveryPlannerUrl;

    @Value("${station.manager.url}")
    private String stationManagerUrl;

    @Autowired
    public ReparationNotifListenerController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostMapping("/j/station/repair")
    public ChargingStation updateReceivedChargingStationStatusFromRepairMan(@RequestBody BreakdownDispatcher.StationToRepairDTO stationToRepairDTO) {
        var response = this.restTemplate.getForEntity(
                this.stationManagerUrl + "/j/stations/{name}/repair", ChargingStation.class,
                stationToRepairDTO.name());

        if (response.getStatusCode() == HttpStatus.OK) {
            this.logger.info("Drone status changed  " + response.getBody());
        } else {
            this.logger
                    .warning("Request failed " + response.getStatusCode() + " body " + response.getBody());
        }
        return response.getBody();
    }

    @PostMapping("/j/drone/repair")
    public Drone updateReceivedStatusFromRepairMan(@RequestBody BreakdownDispatcher.DroneToRepairDTO droneToRepairDTO) {
        var response = this.restTemplate.getForEntity(
                this.deliveryPlannerUrl + "/drones/{name}/status/{droneStatus}", Drone.class,
                droneToRepairDTO.name(), droneToRepairDTO.status());

        if (response.getStatusCode() == HttpStatus.OK) {
            this.logger.info("Drone status changed  " + response.getBody());
        } else {
            this.logger
                    .warning("Request failed " + response.getStatusCode() + " body " + response.getBody());
        }
        return response.getBody();
    }

}
