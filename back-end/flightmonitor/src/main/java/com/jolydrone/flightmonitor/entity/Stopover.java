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
