package com.aldrone2122.project.jolydrone.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlightPlanTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void computeDistance() {
        FlightPlan flightPlanNoStops = new FlightPlan();
        Coordinates originCoordinates = new Coordinates(10.0, 0.0);
        Coordinates destinationCoordinates = new Coordinates(20.0, 0.0);
        Coordinates chargingStationCoordinates1 = new Coordinates(12.0, 5.0);
        Coordinates chargingStationCoordinates2 = new Coordinates(14.0, 6.0);
        List<ChargingStation> chargingStations = new ArrayList<>() {{
            add(new ChargingStation(chargingStationCoordinates1));
        }};

        flightPlanNoStops.setOrigin(originCoordinates);
        flightPlanNoStops.setDestination(destinationCoordinates);
        assertEquals(flightPlanNoStops.getFlightDistance(), originCoordinates.distanceToPoint(destinationCoordinates), "Distance without stops");

        FlightPlan flightPlan1Stop = new FlightPlan();
        flightPlan1Stop.setOrigin(originCoordinates);
        flightPlan1Stop.setDestination(destinationCoordinates);
        flightPlan1Stop.setRefillStopsFromChargingStations(chargingStations);
        Double distance = 0.0;
        distance += originCoordinates.distanceToPoint(chargingStationCoordinates1);
        distance += chargingStationCoordinates1.distanceToPoint(destinationCoordinates);
        assertEquals(flightPlan1Stop.getFlightDistance(), distance, "Distance with one stop");

        chargingStations.add(new ChargingStation(chargingStationCoordinates2));

        FlightPlan flightPlanManyStops = new FlightPlan();
        flightPlanManyStops.setOrigin(originCoordinates);
        flightPlanManyStops.setDestination(destinationCoordinates);
        flightPlanManyStops.setRefillStopsFromChargingStations(chargingStations);
        distance = 0.0;
        distance += originCoordinates.distanceToPoint(chargingStationCoordinates1);
        distance += chargingStationCoordinates1.distanceToPoint(chargingStationCoordinates2);
        distance += chargingStationCoordinates2.distanceToPoint(destinationCoordinates);
        assertEquals(flightPlanManyStops.getFlightDistance(), distance, "Distance with many stops");

    }
}