package com.aldrone2122.project.jolydrone.services;

import com.aldrone2122.project.jolydrone.entity.Drone;
import com.aldrone2122.project.jolydrone.entity.DroneStatus;
import com.aldrone2122.project.jolydrone.entity.Package;
import com.aldrone2122.project.jolydrone.entity.SavedPackage;
import com.aldrone2122.project.jolydrone.repositories.DroneRepository;
import com.aldrone2122.project.jolydrone.repositories.PackageDeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class DroneAvailabilityManagerWS {

    private final Logger logger = Logger.getLogger(DroneAvailabilityManagerWS.class.getName());
    @Autowired
    private DroneRepository droneRepository;

    @Autowired
    private DroneAllocatorWS droneAllocatorWS;

    @Autowired
    private PackageDeliveryRepository packageDeliveryRepository;

    public Drone updateDroneStatus(Drone drone, DroneStatus droneStatus) {

        this.logger.info("Changing drone " + drone.getName() + " status from " + drone.getStatus() + " to " + droneStatus);
        drone.setStatus(droneStatus);

        drone = this.droneRepository.save(drone);


        //packageDeliveryRepository.findLastByTimeStamp(LocalDateTime.now()).orElse(null);

        Optional<SavedPackage> nextDeliveryOpt = packageDeliveryRepository.findTopByOrderByTimeStamp();


        if (nextDeliveryOpt.isPresent()) {

            logger.info("there is a delivery waiting...\nRelaunch a delivery planner");
            SavedPackage nextDelivery = nextDeliveryOpt.get();
            Package paquet = new Package();

            paquet.setId(nextDelivery.getPackageId());
            paquet.setWeight(nextDelivery.getWeight());
            paquet.setDestination(nextDelivery.getDestination());
            paquet.setDimensions(nextDelivery.getDimensions());

            droneAllocatorWS.deliverPackage(paquet);
        }
        // Pop de la BD et lancer une livraison


        return this.droneRepository.save(drone);
    }


}
