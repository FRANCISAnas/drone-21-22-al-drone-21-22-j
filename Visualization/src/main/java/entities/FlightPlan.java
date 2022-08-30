package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FlightPlan implements Serializable {

    private Long id;
    private Coordinates origin;
    private Coordinates destination;

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
