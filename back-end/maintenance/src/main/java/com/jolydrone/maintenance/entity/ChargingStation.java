package com.jolydrone.maintenance.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChargingStation {
    public static final int MaxTerminalNumber = 5;
    private Long id;

    private Integer terminalCount;

    private String stationName;

    private List<Terminal> terminals = new ArrayList<>();

    private Coordinates location;

    public ChargingStation() {
    }

    public ChargingStation(Coordinates coordinates) {
        this();
        setLocation(coordinates);
    }

    public Terminal getTerminal(int index) {
        return this.terminals.get(index);
    }


    public boolean isAvailable() {
        for (Terminal terminal : this.terminals) {
            if (terminal.getStatus().equals(TerminalStatus.AVAILABLE)) ;
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
                "id=" + id +
                ", terminalCount=" + terminalCount +
                ", stationName='" + stationName + '\'' +
                ", location=" + location +
                ", isAvailable=" + isAvailable() +
                '}';
    }
}
