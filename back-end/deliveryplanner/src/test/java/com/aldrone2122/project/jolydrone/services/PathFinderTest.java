package com.aldrone2122.project.jolydrone.services;

import com.aldrone2122.project.jolydrone.entity.Package;
import com.aldrone2122.project.jolydrone.entity.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ExtendWith(SpringExtension.class)
class PathFinderTest {

    @MockBean
    GetStationInArea stationInArea;

    @Autowired
    PathFinder pathFinder;

    List<Coordinates> coordinates;
    List<ChargingStation> chargingStationList;
    Coordinates destination;
    Package paquet = new Package();


    @BeforeEach
    void setUp() {
        coordinates = new ArrayList<Coordinates>() {{
            //Warehouse Coordinates
            add(new Coordinates(5.0, -3.0));
            add(new Coordinates(8.0, 2.0));
            add(new Coordinates(2.0, 9.0));
            add(new Coordinates(-5.0, 8.0));
            add(new Coordinates(-8.0, 12.0));
            //End
            //Charging Stations Coordinates
            add(new Coordinates(6.0, 1.0));
            add(new Coordinates(7.0, 34.0));
            add(new Coordinates(31.0, 47.0));
            add(new Coordinates(52.0, 41.0));
            add(new Coordinates(28.0, 74.0));
            add(new Coordinates(92.0, 84.0));
            add(new Coordinates(63.0, 39.0));
            add(new Coordinates(25.0, 15.0));
            add(new Coordinates(29.0, 75.0));
            add(new Coordinates(96.0, 78.0));
            //End
            //Destination of package
            add(new Coordinates(58.0, 16.0));
            //End
        }};
        //création de 10 stations de recharges
        chargingStationList = new ArrayList<>();
        for (int i = 5; i < coordinates.size() - 1; i++)
            chargingStationList.add(new ChargingStation(coordinates.get(i)));

        destination = coordinates.get(coordinates.size() - 1);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findOptimalPathNoStops() {
        paquet.setId(1L);
        paquet.setDestination(destination);

        /*
         * Dans ce cas la distance maximale que peut faire l'un des drone
         * lui permet de faire qu'un seul voyage sans arrêt de recharge
         */

        //création de 5 Warehouse
        ArrayList<Warehouse> warehouses = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            warehouses.add(new Warehouse(coordinates.get(i)));


        ArrayList<Drone> drones = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            drones.add(new Drone(warehouses.get(i), 52, DroneStatus.AVAILABLE));
            drones.add(new Drone(warehouses.get(i), 50, DroneStatus.AVAILABLE));
        }

        for (Warehouse warehouse : warehouses) {
            ArrayList<ChargingStation> tmpChaginChargingStations = new ArrayList<>() {{
                addAll(chargingStationList);
            }};
            tmpChaginChargingStations.removeIf(s -> s.getLocation().getX() < warehouse.getLocation().getX() || s.getLocation().getX() > destination.getX());
            tmpChaginChargingStations.sort((o1, o2) -> (int) (o1.getLocation().getX() - o2.getLocation().getX()));
            Mockito.when(stationInArea.getStationsInArea(warehouse.getLocation(), destination)).thenReturn(tmpChaginChargingStations);
        }

        Tuple<FlightPlan, Drone> flightPlanDrone = pathFinder.findOptimalPath(warehouses, paquet);
        assertNotNull(flightPlanDrone);
        assertNotNull(flightPlanDrone.x);
        assertNotNull(flightPlanDrone.y);
        assertEquals(flightPlanDrone.x.getOrigin(), warehouses.get(1).getLocation(), "Correct Choosen Warehouse");
        assertEquals(flightPlanDrone.x.getDestination(), destination, "Destination Reachable");
        assertNull(flightPlanDrone.x.getRefillStops(), "No Stops");
        assertEquals(flightPlanDrone.y.getAutonomyMaximum(), 52, "Correct Choosen Drone");
    }

    @Test
    void findOptimalPath1Stop() {
        paquet.setId(2L);
        paquet.setDestination(destination);
        /*
         * le plan de vol optimale est celui du drone qui ne fait qu'un seul arrêt
         */

        //création de 5 Warehouse
        ArrayList<Warehouse> warehouses = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            warehouses.add(new Warehouse(coordinates.get(i)));


        ArrayList<Drone> drones = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            drones.add(new Drone(warehouses.get(i), 39, DroneStatus.AVAILABLE));


        for (Warehouse warehouse : warehouses) {
            ArrayList<ChargingStation> tmpChaginChargingStations = new ArrayList<>() {{
                addAll(chargingStationList);
            }};
            tmpChaginChargingStations.removeIf(s -> s.getLocation().getX() < warehouse.getLocation().getX() || s.getLocation().getX() > destination.getX());
            tmpChaginChargingStations.sort((o1, o2) -> (int) (o1.getLocation().getX() - o2.getLocation().getX()));
            Mockito.when(stationInArea.getStationsInArea(warehouse.getLocation(), destination)).thenReturn(tmpChaginChargingStations);
        }

        Tuple<FlightPlan, Drone> flightPlanDrone = pathFinder.findOptimalPath(warehouses, paquet);
        assertNotNull(flightPlanDrone);
        assertNotNull(flightPlanDrone.x);
        assertNotNull(flightPlanDrone.y);
        assertEquals(flightPlanDrone.x.getOrigin(), warehouses.get(1).getLocation(), "Correct Choosen Warehouse");
        assertEquals(flightPlanDrone.x.getDestination(), destination, "Destination Reachable");
        assertEquals(flightPlanDrone.x.getRefillStops().size(), 1, "No Stops");
        assertEquals(flightPlanDrone.y.getAutonomyMaximum(), 39, "Correct Choosen Drone");

    }

    @Test
    void findOptimalPathNotFound() {
        paquet.setId(3L);
        paquet.setDestination(destination);
        /**
         * Aucun drone ne peut atteindre cette destination
         * Ils ne peuvent même pas atteindre une station de recharge avant de ocntinuer
         * le trajet
         */

        //création de 5 Warehouse
        ArrayList<Warehouse> warehouses = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            warehouses.add(new Warehouse(coordinates.get(i)));


        ArrayList<Drone> drones = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            drones.add(new Drone(warehouses.get(i), 30, DroneStatus.AVAILABLE));

        for (Warehouse warehouse : warehouses) {
            ArrayList<ChargingStation> tmpChaginChargingStations = new ArrayList<>() {{
                addAll(chargingStationList);
            }};
            tmpChaginChargingStations.removeIf(s -> s.getLocation().getX() < warehouse.getLocation().getX() || s.getLocation().getX() > destination.getX());
            tmpChaginChargingStations.sort((o1, o2) -> (int) (o1.getLocation().getX() - o2.getLocation().getX()));
            Mockito.when(stationInArea.getStationsInArea(warehouse.getLocation(), destination)).thenReturn(tmpChaginChargingStations);
        }

        assertNull(pathFinder.findOptimalPath(warehouses, paquet), "No flight plan for existing drones and delivery destination.");

    }
}
