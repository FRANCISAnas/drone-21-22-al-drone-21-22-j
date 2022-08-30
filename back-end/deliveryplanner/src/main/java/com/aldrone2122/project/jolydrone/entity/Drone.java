package com.aldrone2122.project.jolydrone.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Drone {

    @Id
    @GeneratedValue
    private Long id;

    private Integer unloadedWeight;

    private Integer autonomyMaximum;

    private Integer defaultAutonomyMaximum;

    private Integer batteryLevelInPercent;

    private DroneStatus status;

    private Integer maxWeighingCapacity; // is what the drone can carry at max.

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    private Warehouse parkedAt;

    @Embedded
    private Coordinates currentLocation;


    @Column(unique = true)
    private String name;


    public Drone(Warehouse parkedAt, int autonomyMaximum, DroneStatus droneStatus) {
        this();
        parkAt(parkedAt);
        this.autonomyMaximum = autonomyMaximum;
        this.status = droneStatus;
        this.defaultAutonomyMaximum = autonomyMaximum;
    }

    public Drone() {

    }

    public Warehouse getParkedAt() {
        return parkedAt;
    }

    public void parkAt(Warehouse parkedAt) {
        this.parkedAt = parkedAt;
        parkedAt.parkDrone(this);
        setCurrentLocation(parkedAt.getLocation());
    }

    public Integer getDefaultAutonomyMaximum() {
        return defaultAutonomyMaximum;
    }

    public void setDefaultAutonomyMaximum(Integer defaultAutonomyMaximum) {
        this.defaultAutonomyMaximum = defaultAutonomyMaximum;
    }

    public Coordinates getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Coordinates currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "id=" + id +
                ", autonomyMaximum=" + autonomyMaximum +
                ", status=" + status +
                ", parkedAt=" + parkedAt +
                ", currentLocation=" + currentLocation +
                '}';
    }

    public void returnToInitialHome() {

        setCurrentLocation(getParkedAt().getLocation());
    }

    public boolean isAvailable() {
        return status == DroneStatus.AVAILABLE;
    }
}
