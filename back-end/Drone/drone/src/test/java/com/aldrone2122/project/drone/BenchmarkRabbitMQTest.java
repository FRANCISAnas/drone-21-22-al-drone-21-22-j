package com.aldrone2122.project.drone;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

public class BenchmarkRabbitMQTest {
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    Random random = new Random();
    Map<String, Region> regionsMap;

    @Test
    void runBenchMark() throws IOException {
        long startTime = System.currentTimeMillis();
        regionsMap = this.yamlMapper.readValue(
            Drone.class.getClassLoader().getResourceAsStream("map.yaml"),
            new TypeReference<Map<String, Region>>() {
            });
        List<Thread> threads = new ArrayList<>();
        regionsMap.forEach((regionName, value) -> value.setName(regionName));
        for (int i = 0; i < 1000; i++) {
            threads.add(this.start(createFakeDelivery()));
        }

        for (var thread1 : threads) {
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Total time: " + (endTime - startTime));

    }

    private Thread start(Delivery delivery) {
        Thread thread1 = new Thread(() -> {
            delivery.execute(new FlightMonitorCommunicator(new ArrayList<>(regionsMap.values())));
        });
        thread1.start();
        return thread1;
    }

    private Delivery createFakeDelivery() {

        var delivery = new Delivery();

        delivery.setDeliveryStatus(DeliveryStatus.IN_WAREHOUSE);

        delivery.setTrackingNumber(RandomStringUtils.randomAlphabetic(3, 4).toUpperCase(Locale.ENGLISH) + "-"
            + RandomStringUtils.randomNumeric(5));

        delivery.setDrone(createFakeDrone());

        delivery.setFlightPlan(createFakeFlightPlan());

        delivery.setPaquet(createFakePackage());

        return delivery;
    }

    private Drone createFakeDrone() {
        var drone = new Drone();

        drone.setAutonomyMaximum(100);
        drone.setBatteryLevelInPercent(100);

        drone.setMaximumWeighingCapacity(1000);
        drone.setStatus(DroneStatus.DELIVERING);
        drone.setId(random.nextLong());

        drone.setName(RandomStringUtils.randomAlphabetic(4, 11));

        return drone;
    }

    private FlightPlan createFakeFlightPlan() {
        var flp = new FlightPlan();
        flp.setDestination(createFakeCoordinates());
        flp.setOrigin(createFakeCoordinates());

        int nbStops = this.random.nextInt(2, 8);
        List<Stopover> stops = new ArrayList<>();
        for (int i = 0; i < nbStops; i++) {
            stops.add(createFakeStopover());
        }

        flp.setRefillStops(stops);
        return flp;
    }

    private Coordinates createFakeCoordinates() {
        return new Coordinates(this.random.nextDouble(0.0, 500), this.random.nextDouble(-200, 500));
    }

    private Stopover createFakeStopover() {
        return new Stopover(this.createFakeChargingStation());
    }

    private ChargingStation createFakeChargingStation() {
        return new ChargingStation(this.createFakeCoordinates());
    }

    private JPackage createFakePackage() {
        return new JPackage((long) 0, this.random.nextDouble(50, 100), createFakeCoordinates(), createFakeDimensions());
    }

    private Dimensions createFakeDimensions() {
        return new Dimensions(this.random.nextDouble(0, 50), this.random.nextDouble(0, 100),
            this.random.nextDouble(0, 150));
    }
}
