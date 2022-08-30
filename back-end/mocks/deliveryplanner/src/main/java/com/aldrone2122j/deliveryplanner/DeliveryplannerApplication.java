package com.aldrone2122j.deliveryplanner;

import java.util.Random;
import java.util.logging.Logger;

import com.aldrone2122j.deliveryplanner.entities.Drone;
import com.aldrone2122j.deliveryplanner.entities.DroneStatus;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DeliveryplannerApplication {

    private final Logger logger = Logger.getLogger(DeliveryplannerApplication.class.getName());
    private Random random = new Random();

    public static void main(String[] args) {
        SpringApplication.run(DeliveryplannerApplication.class, args);
    }


    @GetMapping("/drones/{name}/status/{droneStatus}")
    public ResponseEntity<Drone> updateDroneStatus(@PathVariable("name") String droneName,
                                                   @PathVariable("droneStatus") DroneStatus droneStatus) {

        this.logger.info("updateDroneStatus drone's name :" + droneName);
        return ResponseEntity.ok(createFakeDrone());

    }

    private Drone createFakeDrone() {
        var drone = new Drone();
        drone.setAutonomyMaximum(100);
        drone.setBatteryLevelInPercent(100);
        drone.setStatus(DroneStatus.DELIVERING);
        drone.setId(random.nextLong());
        drone.setName(RandomStringUtils.randomAlphabetic(4, 11));
        return drone;
    }
}
