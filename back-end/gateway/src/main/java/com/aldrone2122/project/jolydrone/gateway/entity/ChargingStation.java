package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Getter
@Setter
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

}
