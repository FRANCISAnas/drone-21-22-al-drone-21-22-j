package com.aldrone2122.project.deliverytracking.services;

import com.aldrone2122.project.deliverytracking.entities.DeliveryStatus;
import com.aldrone2122.project.deliverytracking.repositories.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;


@Service
public class DeliveryDroneMonitorWS {

    @Autowired
    private DeliveryRepository deliveryRepository;

    public DeliveryStatus getDeliveryStatus(String trackingNumber) throws NotFoundException {

        var optDelivery = this.deliveryRepository.findTopByTrackingNumberOrderByClockDesc(trackingNumber);

        if (optDelivery.isPresent()) {
            return optDelivery.get().getDeliveryStatus();
        }
        throw new NotFoundException();


    }
}
