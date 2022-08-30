package com.aldrone2122.project.drone;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChargingStation {

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


    public Integer getTerminalCount() {
        return terminalCount;
    }

    public void setTerminalCount(Integer terminalCount) {
        this.terminalCount = terminalCount;
    }

    public List<Terminal> getTerminals() {
        return terminals;
    }

    public void setTerminals(List<Terminal> terminals) {
        this.terminals = terminals;
    }

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public Terminal getTerminal(int index) {
        return this.terminals.get(index);
    }

    public void setTerminal(int index, Terminal terminal) {
        this.terminals.set(index, terminal);
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
