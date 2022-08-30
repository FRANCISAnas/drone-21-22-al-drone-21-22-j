package com.aldrone2122.project.deliverytracking.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
public class Stopover implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private ChargingStation chargingStation;

    private boolean passed;

    private StopoverStatus stopoverStatus;


    public Stopover(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
        this.stopoverStatus = StopoverStatus.PENDING;
    }

    public Stopover() {

    }

    @Override
    public String toString() {
        return "Stopover{" +
                "chargingStation=" + chargingStation +
                ", stopoverStatus=" + stopoverStatus +
                '}';
    }
}
