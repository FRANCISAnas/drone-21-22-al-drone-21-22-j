package com.jolydrone.maintenance.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Drone {

    private Long id;

    private Integer unloadedWeight;

    private Integer autonomyMaximum;

    private Integer batteryLevelInPercent;

    private DroneStatus status;

    private Integer maxWeighingCapacity; // is what the drone can carry at max.
    private Warehouse parkedAt;
    private Coordinates currentLocation;
    private String name;

    public Drone(Warehouse parkedAt, int autonomyMaximum) {
        this();
        parkAt(parkedAt);
        this.autonomyMaximum = autonomyMaximum;
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
}
