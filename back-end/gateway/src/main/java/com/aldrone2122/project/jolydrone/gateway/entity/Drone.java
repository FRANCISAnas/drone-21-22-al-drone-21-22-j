package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;

@Getter
public class Drone {
    private Long id;

    private Integer unloadedWeight;

    private Integer autonomyMaximum;

    private Integer batteryLevelInPercent;

    private DroneStatus status;

    private String name;

    private Integer maxWeighingCapacity; // is what the drone can carry at max.

    private Coordinates position;

}
