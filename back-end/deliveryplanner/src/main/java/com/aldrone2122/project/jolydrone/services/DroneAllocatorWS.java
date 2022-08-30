package com.aldrone2122.project.jolydrone.services;

import com.aldrone2122.project.jolydrone.controllers.DroneAllocatorController;
import com.aldrone2122.project.jolydrone.entity.Package;
import com.aldrone2122.project.jolydrone.entity.*;
import com.aldrone2122.project.jolydrone.repositories.DroneRepository;
import com.aldrone2122.project.jolydrone.repositories.TrackingRepository;
import com.aldrone2122.project.jolydrone.repositories.WarehouseRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.logging.Logger;

@Service
public class DroneAllocatorWS {
    private final Logger logger = Logger.getLogger(DroneAllocatorWS.class.getName());
    @Autowired
    private PathFinder pathFinder;
    @Autowired
    private DeliveryStarter deliveryStarter;
    @Autowired
    private DroneRepository droneRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private TrackingRepository trackingRepository;

    public DeliverPackageResponse deliverPackage(Package deliveryPackage) {

        if (warehouseRepository.count() > 0 && droneRepository.count() > 0) {// TODO
            var delivery = new Delivery();

            delivery.setDeliveryStatus(DeliveryStatus.IN_WAREHOUSE);

            delivery.setPaquet(deliveryPackage);

            logger.info("Fetching all warehouses.");
            Tuple<FlightPlan, Drone> flightPlanDrone = pathFinder
                    .findOptimalPath(warehouseRepository.findAll(), deliveryPackage);


            if (flightPlanDrone == null || flightPlanDrone.x == null) {
                logger.info("No flight plan was found !");
                //TODO what to do when there is no available flightplan
                return new DeliverPackageResponse(this.generateTrackingNumber(), new Warehouse());
            }

            String trackingNumber = this.generateTrackingNumber();

            delivery.setTrackingNumber(trackingNumber);


            Drone drone = flightPlanDrone.y;

            drone.setStatus(DroneStatus.DELIVERING);

            droneRepository.save(drone);

            delivery.setDrone(drone);

            delivery.setFlightPlan(flightPlanDrone.x);


            deliveryStarter.startDelivery(delivery);

            return new DeliverPackageResponse(trackingNumber, drone.getParkedAt());
        }

        return new DeliverPackageResponse(this.generateTrackingNumber(), new Warehouse());

    }

    private String generateTrackingNumber() {
        String trackingNumber = RandomStringUtils.randomAlphabetic(3, 4).toUpperCase(Locale.ENGLISH) + "-"
                + RandomStringUtils.randomNumeric(5);
        while (this.trackingRepository.existsByTrackingNumber(trackingNumber)) {
            trackingNumber = RandomStringUtils.randomAlphabetic(3, 4).toUpperCase(Locale.ENGLISH) + "-"
                    + RandomStringUtils.randomNumeric(5);
        }

        var tmp = new Tracking();
        tmp.setTrackingNumber(trackingNumber);
        trackingRepository.saveAndFlush(tmp);
        return trackingNumber;

    }

    public FlightPlan findPathFor(DroneAllocatorController.OriginDestination originDestination, String droneName, Integer droneCurrentCapacity) {
        var droneOpt = droneRepository.findByName(droneName);
        if (droneOpt.isPresent()) {
            var foundDrone = droneOpt.get();
            foundDrone.setAutonomyMaximum(droneCurrentCapacity);
            var savedDrone = droneRepository.save(foundDrone);
            return pathFinder.findOptimalPath(originDestination.origin(), originDestination.destination(), savedDrone);
        } else {
            logger.severe("Drone with name " + droneName + " was not found...");
            return null;
        }
    }

    public Drone updateCurrentPositionFor(String droneName, Coordinates currentPosition) {
        var droneOpt = droneRepository.findByName(droneName);
        if (droneOpt.isPresent()) {
            var drone = droneOpt.get();
            logger.info("Updating current position of " + droneName);
            drone.setCurrentLocation(currentPosition);
            return droneRepository.save(drone);
        } else {
            logger.info("Drone with name " + droneName + " were not found...");
            return null;
        }
    }

    /**
     * DeliverPackageResponse
     */
    public record DeliverPackageResponse(String trackingNumber, Warehouse warehouse) {
    }

}
