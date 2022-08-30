package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChargingStation implements Serializable {

    private Integer terminalCount;
    private String stationName;
    private List<Terminal> terminals;
    private Coordinates location;


    public ChargingStation() {
        terminals = new ArrayList<>();
    }

    public ChargingStation(Coordinates coordinates) {
        this();
        setLocation(coordinates);
    }

    public boolean isAvailable() {
        for (Terminal terminal : this.terminals) {
            if (terminal.getStatus().equals(TerminalStatus.AVAILABLE))
                return true;
        }
        return false;
    }

    public Double distanceTo(ChargingStation chargingStation) {
        return distanceTo(chargingStation.getLocation());
    }

    public Double distanceTo(Coordinates coordinates) {
        return location.distanceToPoint(coordinates);
    }

    @Override
    public String toString() {
        return "ChargingStation{" +
                " terminalCount=" + terminalCount +
                ", flightPlan='" + stationName + '\'' +
                ", location=" + location +
                ", isAvailable=" + isAvailable() +
                '}';
    }
}
