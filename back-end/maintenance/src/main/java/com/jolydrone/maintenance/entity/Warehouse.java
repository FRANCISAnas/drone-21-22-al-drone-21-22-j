package com.jolydrone.maintenance.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Warehouse {

    private Long id;

    private List<Drone> dronesStationned;

    private Coordinates location;//TODO add an attribute like a name to give this entity more identity

    public Warehouse() {
        dronesStationned = new ArrayList<>();
    }

    public Warehouse(Coordinates location) {
        this();
        this.location = location;
    }

    public void parkDrone(Drone drone) {
        dronesStationned.add(drone);
    }
}
