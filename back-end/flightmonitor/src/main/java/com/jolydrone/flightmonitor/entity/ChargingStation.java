package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@Setter
public class ChargingStation implements Serializable {

    private Integer terminalCount;

    @NotBlank
    private String stationName;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Terminal> terminals;


    @Embedded
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
