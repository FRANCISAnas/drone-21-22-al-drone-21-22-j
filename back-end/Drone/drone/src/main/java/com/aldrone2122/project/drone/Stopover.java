package com.aldrone2122.project.drone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stopover {

    private Long id;
    private ChargingStation chargingStation;
    private StopoverStatus stopoverStatus;
    private boolean passed;

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
