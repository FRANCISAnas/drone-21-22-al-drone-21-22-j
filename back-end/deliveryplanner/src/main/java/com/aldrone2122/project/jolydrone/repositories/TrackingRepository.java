package com.aldrone2122.project.jolydrone.repositories;

import com.aldrone2122.project.jolydrone.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingRepository extends JpaRepository<Tracking, Long> {
    boolean existsByTrackingNumber(String trackingNumber);
}
