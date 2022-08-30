package com.aldrone2122j.deliveryplanner.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Warehouse {

    //@JsonManagedReference

    List<Drone> dronesStationned;
    private Long id;
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
