package com.aldrone2122.project.jolydrone.controllers;

import com.aldrone2122.project.jolydrone.entity.Coordinates;
import com.aldrone2122.project.jolydrone.entity.Drone;
import com.aldrone2122.project.jolydrone.entity.FlightPlan;
import com.aldrone2122.project.jolydrone.entity.Package;
import com.aldrone2122.project.jolydrone.services.DroneAllocatorWS;
import com.aldrone2122.project.jolydrone.services.DroneAllocatorWS.DeliverPackageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class DroneAllocatorController {


    @Autowired
    private DroneAllocatorWS droneAllocatorWS;

    private final Logger logger = Logger.getLogger(DroneAllocatorController.class.getName());

    @PostMapping("/deliver")
    public DeliverPackageResponse deliverPackage(@RequestBody Package deliverPackage) {
        logger.info("---------- Received delivery request ----------");
        return droneAllocatorWS.deliverPackage(deliverPackage);
    }

    @PostMapping("/drones/{droneName}/newFlightPlan/{droneCurrentCapacity}")
    public FlightPlan getNewFlightPlan(@PathVariable(value = "droneName") String droneName, @PathVariable(value = "droneCurrentCapacity") Integer droneCurrentCapacity, @RequestBody OriginDestination originDestination) {
        logger.info("Received new flight plan request for drone " + droneName);
        logger.info("Max distance it can do: " + droneCurrentCapacity);
        return droneAllocatorWS.findPathFor(originDestination, droneName, droneCurrentCapacity);
    }

    @PostMapping("/drones/{droneName}/delivering")
    public Drone updateDroneCurrentPosition(@PathVariable(value = "droneName") String droneName, @RequestBody Coordinates currentPosition) {
        return droneAllocatorWS.updateCurrentPositionFor(droneName, currentPosition);
    }

    public record OriginDestination(Coordinates origin, Coordinates destination) {

    }

}
