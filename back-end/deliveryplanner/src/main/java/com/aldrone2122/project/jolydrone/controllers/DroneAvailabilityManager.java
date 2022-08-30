package com.aldrone2122.project.jolydrone.controllers;

import com.aldrone2122.project.jolydrone.entity.Drone;
import com.aldrone2122.project.jolydrone.entity.DroneStatus;
import com.aldrone2122.project.jolydrone.entity.Warehouse;
import com.aldrone2122.project.jolydrone.repositories.DroneRepository;
import com.aldrone2122.project.jolydrone.repositories.WarehouseRepository;
import com.aldrone2122.project.jolydrone.services.DroneAvailabilityManagerWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class DroneAvailabilityManager {

    private final Logger logger = Logger.getLogger(DroneAvailabilityManager.class.getName());
    @Autowired
    private DroneAvailabilityManagerWS droneAvailabilityManagerWS;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private DroneRepository droneRepository;

    @PostMapping("/warehouses/{warehouseId}/dronesStationned")
    public Drone stationDrone(@PathVariable(value = "warehouseId") Long warehouseId,
                              @Valid @RequestBody Drone drone) {
        return warehouseRepository.findById(warehouseId).map(warehouse -> {
            drone.parkAt(warehouse);
            drone.setDefaultAutonomyMaximum(drone.getAutonomyMaximum());
            return droneRepository.save(drone);
        }).orElseThrow(() -> new ResourceNotFoundException("WarehouseId " + warehouseId + " not found"));
    }

    @GetMapping("/j/warehouses")
    public List<Warehouse> getWarehouses() {
        return warehouseRepository.findAll();
    }


    @GetMapping("/drones/{name}/status/{droneStatus}")
    public ResponseEntity<Drone> updateDroneStatus(@PathVariable("name") String droneName,
                                                   @PathVariable("droneStatus") DroneStatus droneStatus) {

        this.logger.info("updateDroneStatus drone's name :" + droneName);
        var droneOpt = this.droneRepository.findByName(droneName);
        return droneOpt.map(drone -> {
            if (droneStatus == DroneStatus.AVAILABLE) {
                logger.info("Returning drone at warehouse home");
                drone.returnToInitialHome();
                logger.info("Drone after returning to home");
                logger.info(drone.toString());
            }
            return ResponseEntity.ok(droneAvailabilityManagerWS.updateDroneStatus(drone, droneStatus));
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}