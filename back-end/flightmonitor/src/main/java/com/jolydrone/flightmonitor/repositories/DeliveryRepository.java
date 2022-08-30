package com.jolydrone.flightmonitor.repositories;

import com.jolydrone.flightmonitor.entity.Delivery;
import com.jolydrone.flightmonitor.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    Optional<Delivery> findDeliveryByDroneId(Long id);

    List<Delivery> findAllByDeliveryStatus(DeliveryStatus deliveryStatus);


}
