package com.aldrone2122j.deliveryplanner.entities;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightPlan {


    private Coordinates origin;


    private Coordinates destination;


    private List<Stopover> refillStops;


    private Double flightDistance = 0.0;

    public Coordinates getOrigin() {
        return origin;
    }

    public void setOrigin(Coordinates origin) {
        this.origin = origin;
    }


    public void setRefillStops(List<Stopover> refillStops) {
        this.refillStops = refillStops;

    }

    public void setRefillStopsFromChargingStations(List<ChargingStation> refillStops) {
        this.refillStops = new ArrayList<>();
        for (ChargingStation cs : refillStops) {
            this.refillStops.add(new Stopover(cs));
        }

    }


    public void setDestination(Coordinates destination) {
        this.destination = destination;

    }


    @Override
    public String toString() {
        return "FlightPlan{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", flightDistance=" + flightDistance +
                "refillStops=" + refillStops +
                '}';
    }
}
