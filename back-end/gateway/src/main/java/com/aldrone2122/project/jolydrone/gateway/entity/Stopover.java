package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stopover {

    private Long id;

    private ChargingStation chargingStation;

    private StopoverStatus stopoverStatus;


    public Stopover(ChargingStation chargingStation) {
        this.chargingStation = chargingStation;
        this.stopoverStatus = StopoverStatus.PENDING;
    }

    public Stopover() {

    }


}
