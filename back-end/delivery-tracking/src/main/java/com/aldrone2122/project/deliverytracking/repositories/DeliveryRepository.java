package com.aldrone2122.project.deliverytracking.repositories;


import java.util.List;
import java.util.Optional;

import com.aldrone2122.project.deliverytracking.entities.Delivery;
import com.aldrone2122.project.deliverytracking.entities.DeliveryStatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    Optional<Delivery> findTopByTrackingNumberOrderByClockDesc(String trackingNumber);

    Optional<Delivery> findDeliveryByDroneId(Long id);

    List<Delivery> findAllByDeliveryStatus(DeliveryStatus deliveryStatus);


}
