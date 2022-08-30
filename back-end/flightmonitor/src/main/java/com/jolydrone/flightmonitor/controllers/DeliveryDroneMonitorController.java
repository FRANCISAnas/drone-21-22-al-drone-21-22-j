package com.jolydrone.flightmonitor.controllers;

import com.jolydrone.flightmonitor.entity.DeliveryStatus;
import com.jolydrone.flightmonitor.exceptions.NotFoundException;
import com.jolydrone.flightmonitor.services.DeliveryDroneMonitorWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/flight-monitor")
public class DeliveryDroneMonitorController {

    private final Logger logger = Logger.getLogger(DeliveryDroneMonitorController.class.getName());
    @Autowired
    private DeliveryDroneMonitorWS deliveryDroneMonitorWS;

    @GetMapping("/test")
    public String testRepairAdrone() {

        this.logger.info("Testing Drone");
        deliveryDroneMonitorWS.postBrokenDrone();
        return "OK";

    }

    @GetMapping("/delivery/tracking/{trackingNumber}")
    public ResponseEntity<DeliveryStatus> getDeliveryStatus(@PathVariable("trackingNumber") String trackingNumber) {

        try {
            return ResponseEntity.ok().body(deliveryDroneMonitorWS.getDeliveryStatus(trackingNumber));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @GetMapping("/hello")
    public String helloFlightMonitor() {
        return "hello from flight monitor";
    }
}
