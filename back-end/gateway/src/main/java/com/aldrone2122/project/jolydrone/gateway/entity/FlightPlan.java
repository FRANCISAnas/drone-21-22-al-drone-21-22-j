package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class FlightPlan {

    private Coordinates origin;

    private Coordinates destination;

    private List<Stopover> refillStops;

    private Double flightDistance = 0.0;

}
