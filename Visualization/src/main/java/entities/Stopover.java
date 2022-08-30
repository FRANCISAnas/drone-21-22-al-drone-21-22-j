package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Stopover implements Serializable {
    private Long id;

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
