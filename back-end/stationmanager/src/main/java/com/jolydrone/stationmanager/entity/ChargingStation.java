package com.jolydrone.stationmanager.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class ChargingStation {

    @Id
    @GeneratedValue
    private Long id;

    private Integer terminalCount;

    @NotBlank
    @Column(unique = true)
    private String stationName;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Terminal> terminals = new ArrayList<>();


    @Embedded
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

    public void setTerminal(int index, Terminal terminal) {
        this.terminals.set(index, terminal);
    }

    public boolean isAvailable() {
        for (Terminal terminal : this.terminals) {
            if (terminal.getStatus().equals(TerminalStatus.AVAILABLE)) {
                return true;
            }

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
