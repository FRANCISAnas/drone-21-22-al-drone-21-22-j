package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Drone implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Integer unloadedWeight;

    private Integer autonomyMaximum;

    private Integer batteryLevelInPercent;

    private DroneStatus status;

    private String name;

    private Integer maxWeighingCapacity; // is what the drone can carry at max.


    @Embedded
    private Coordinates position;


    @Override
    public String toString() {
        return "Drone{" +
                "id=" + id +
                ", unloadedWeight=" + unloadedWeight +
                ", autonomyMaximum=" + autonomyMaximum +
                ", batteryLevelInPercent=" + batteryLevelInPercent +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", maxWeighingCapacity=" + maxWeighingCapacity +
                ", position=" + position +
                '}';
    }
}
