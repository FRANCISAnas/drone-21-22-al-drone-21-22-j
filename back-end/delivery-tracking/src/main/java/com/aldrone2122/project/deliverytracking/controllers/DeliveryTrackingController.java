package com.aldrone2122.project.deliverytracking.controllers;

import com.aldrone2122.project.deliverytracking.entities.DeliveryStatus;
import com.aldrone2122.project.deliverytracking.services.DeliveryDroneMonitorWS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeliveryTrackingController {
    @Autowired
    private DeliveryDroneMonitorWS deliveryDroneMonitorWS;

    @GetMapping("/delivery/tracking/{trackingNumber}")
    public ResponseEntity<DeliveryStatus> getDeliveryStatus(@PathVariable("trackingNumber") String trackingNumber) {

        try {

            return ResponseEntity.ok().body(deliveryDroneMonitorWS.getDeliveryStatus(trackingNumber));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
}
