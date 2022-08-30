package com.jolydrone.maintenance.services;

import com.jolydrone.maintenance.entity.ChargingStation;
import com.jolydrone.maintenance.entity.Drone;
import com.jolydrone.maintenance.entity.DroneStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.logging.Logger;

@Service
public class BreakdownDispatcher {


    private final RestTemplate restTemplate;
    @Value("${repair.man.url}")
    private String repairManUrl;
    private final Logger logger = Logger.getLogger(BreakdownDispatcher.class.getName());

    @Autowired
    public BreakdownDispatcher(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void askForRepair(String chargingStationName) {
        this.restTemplate.postForEntity(
                repairManUrl + "/j/station/" + chargingStationName + "/terminals", new StationToRepairDTO(chargingStationName, ChargingStation.MaxTerminalNumber), StationToRepairDTO.class); //TODO handle exception
    }

    public void askForRepair(Drone drone) {
        this.restTemplate.postForEntity(
                repairManUrl + "/j/drone", new DroneToRepairDTO(drone.getName(), drone.getStatus()), DroneToRepairDTO.class); //TODO handle exception
    }


    /**
     * TerminalList List<TerminalList> terminals
     **/
    public record TerminalList(List<TerminalList> terminalLists) {
    }

    public record DroneToRepairDTO(String name, DroneStatus status) {
    }

    public record StationToRepairDTO(String name, int terminalcount) {
    }
}
