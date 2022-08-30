package com.aldrone2122.project.jolydrone.repositories;

import com.aldrone2122.project.jolydrone.entity.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone, Long> {

    Optional<Drone> findByName(String name);
}
