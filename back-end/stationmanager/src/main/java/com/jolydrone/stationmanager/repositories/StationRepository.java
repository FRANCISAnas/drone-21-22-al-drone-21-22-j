package com.jolydrone.stationmanager.repositories;

import com.jolydrone.stationmanager.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<ChargingStation, Long> {
    Optional<ChargingStation> findByStationName(String stationName);
}
