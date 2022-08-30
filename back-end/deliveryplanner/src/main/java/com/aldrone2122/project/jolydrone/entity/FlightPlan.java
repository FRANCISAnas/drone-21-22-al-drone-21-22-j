package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
        if (destination != null)
            computeFlightDistance();
        this.origin = origin;
    }


    public void setRefillStops(List<Stopover> refillStops) {
        this.refillStops = refillStops;
        computeFlightDistance();
    }

    public void setRefillStopsFromChargingStations(List<ChargingStation> refillStops) {
        this.refillStops = new ArrayList<>();
        for (ChargingStation cs : refillStops) {
            this.refillStops.add(new Stopover(cs));
        }
        computeFlightDistance();
    }


    public void setDestination(Coordinates destination) {
        this.destination = destination;
        if (origin != null) {
            computeFlightDistance();
        }
    }

    private void computeFlightDistance() {
        Double distance = 0.0;
        if (refillStops != null && refillStops.size() > 0) {
            distance += origin.distanceToPoint(refillStops.get(0).getChargingStation().getLocation());
            for (int i = 1; i < refillStops.size(); i++) {
                distance += refillStops.get(i - 1).getChargingStation().distanceTo(refillStops.get(i).getChargingStation());
            }
            distance += refillStops.get(refillStops.size() - 1).getChargingStation().distanceTo(destination);
        } else {
            distance = origin.distanceToPoint(destination);
        }
        flightDistance = distance;
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
