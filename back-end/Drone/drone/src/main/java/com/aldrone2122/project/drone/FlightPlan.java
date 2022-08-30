package com.aldrone2122.project.drone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.logging.Logger;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class FlightPlan {

    private static final Logger logger = Logger.getLogger(FlightPlan.class.getName());
    private Long id;
    private Coordinates origin;
    private Coordinates destination;
    private List<Stopover> refillStops;
    private final Double flightDistance = 0.0;
    private Coordinates currentPos;

    public FlightPlan() {

    }


    public void execute(Delivery delivery, IFlightMonitorCommunicator communicator) {
        this.currentPos = origin;
        communicator.startDelivery(delivery);
        if (refillStops == null || refillStops.isEmpty()) {
            this.directTravel(delivery, communicator);
        } else {
            Coordinates currentStart = origin;
            Stopover stopover = null;
            Coordinates nextStop = null;
            int totalCheckpoints = 0;

            nextStop = refillStops.get(0).getChargingStation().getLocation();
            totalCheckpoints = refillStops.size() + 1;
            stopover = refillStops.get(0);

            Vector displacement = new Vector(origin, nextStop).normalize().multiply(0.1);

            int checkpointsTraveled = 0;
            while (checkpointsTraveled < totalCheckpoints) {
                int moveCounter = 0;
                while (!nextStop.match(currentPos)) {
                    currentPos = currentPos.move(displacement);
                    moveCounter++;
                    if (moveCounter == 100) {
                        delivery.getDrone().setPosition(currentPos);
                        communicator.updateMonitor(delivery);
                        moveCounter = 0;
                    }
                }

                checkpointsTraveled++;
                if (checkpointsTraveled == totalCheckpoints) {
                    communicator.finishedDelivery(delivery);
                    System.out.println("Finish Travel");
                    break;
                }

                currentStart = nextStop;
                currentPos = currentStart;

                if (checkpointsTraveled < refillStops.size()) {
                    nextStop = refillStops.get(checkpointsTraveled).getChargingStation().getLocation();
                } else if (checkpointsTraveled == refillStops.size()) {
                    nextStop = destination;
                }

                displacement = new Vector(currentPos, nextStop).normalize().multiply(0.1);

                System.out.println("Current Position :" + currentPos);

            }

        }

    }

    private void originToFirstStopoverTravel() {

    }

    private void stopoversTravel() {

    }

    private void lastStopoverToDestinationTravel() {

    }

    private void directTravel(Delivery delivery, IFlightMonitorCommunicator communicator) {
        Coordinates currentStart = origin;
        Coordinates currentPos = currentStart;

        Coordinates nextStop = destination;

        Vector displacement = new Vector(origin, nextStop).normalize().multiply(0.1);
        int moveCounter = 0;
        while (!nextStop.match(currentPos)) {
            currentPos = currentPos.move(displacement);
            moveCounter++;
            if (moveCounter == 100) {
                communicator.updateMonitor(delivery);
                moveCounter = 0;
            }
        }

        communicator.finishedDelivery(delivery);
        System.out.println("Finish Travel");

    }


    public Coordinates getCurrentPos() {
        return currentPos;
    }

    public void setOrigin(Coordinates origin) {
        this.origin = origin;
    }


    public void setRefillStops(List<Stopover> refillStops) {
        this.refillStops = refillStops;
    }

    public Coordinates getDestination() {
        return destination;
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
