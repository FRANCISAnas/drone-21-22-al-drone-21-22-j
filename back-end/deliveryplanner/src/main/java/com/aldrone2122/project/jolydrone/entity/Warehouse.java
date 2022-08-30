package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Warehouse {

    //@JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "parkedAt")
    List<Drone> dronesStationned;
    @Id
    @GeneratedValue
    private Long id;
    @Embedded
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
