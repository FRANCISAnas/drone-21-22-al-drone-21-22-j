package com.jolydrone.maintenance.controllers;

import com.jolydrone.maintenance.entity.Drone;
import com.jolydrone.maintenance.services.BreakdownDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class BreakdownDispatcherController {


    @Autowired
    private BreakdownDispatcher breakdownDispatcher;


    private final Logger logger = Logger.getLogger(BreakdownDispatcherController.class.getName());

    @PostMapping("/j/station")
    public String repairStation(@RequestBody String chargingStationName) {
        //breakdownDispatcher.askForRepair(chargingStationName);
        logger.info("Received repair request for charging station " + chargingStationName);
        return "Ok";
    }

    @PostMapping("/j/drone")
    public Drone repairDrone(@RequestBody Drone drone) {
        breakdownDispatcher.askForRepair(drone);
        return drone;
    }

}
