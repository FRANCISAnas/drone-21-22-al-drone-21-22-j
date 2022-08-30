package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class FlightPlan implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "ORIGIN_X"))
    @AttributeOverride(name = "y", column = @Column(name = "ORIGIN_Y"))
    private Coordinates origin;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "DESTINATION_X"))
    @AttributeOverride(name = "y", column = @Column(name = "DESTINATION_Y"))
    private Coordinates destination;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Stopover> refillStops;
    private Double flightDistance = 0.0;

    public FlightPlan() {

    }

    public FlightPlan(FlightPlan flightPlan) {
        this();
        origin = flightPlan.origin;
        destination = flightPlan.destination;
        refillStops = new ArrayList<>();
        Set<String> existed = new HashSet<>();
        flightPlan.getRefillStops().forEach(stopover -> {
            if (!existed.contains(stopover.getChargingStation().getStationName())) {
                existed.add(stopover.getChargingStation().getStationName());
                refillStops.add(stopover);
            }
        });
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
