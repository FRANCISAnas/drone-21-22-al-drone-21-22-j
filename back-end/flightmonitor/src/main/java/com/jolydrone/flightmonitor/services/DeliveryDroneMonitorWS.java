package com.jolydrone.flightmonitor.services;

import com.jolydrone.flightmonitor.entity.DeliveryStatus;
import com.jolydrone.flightmonitor.entity.Drone;
import com.jolydrone.flightmonitor.exceptions.NotFoundException;
import com.jolydrone.flightmonitor.repositories.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class DeliveryDroneMonitorWS {

    private final RestTemplate restTemplate;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Value("${maintenance.url}")
    private String maintenanceUrl;

    @Autowired
    public DeliveryDroneMonitorWS(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public DeliveryStatus getDeliveryStatus(String trackingNumber) throws NotFoundException {

        var optDelivery = this.deliveryRepository.findByTrackingNumber(trackingNumber);

        if (optDelivery.isPresent()) {
            return optDelivery.get().getDeliveryStatus();
        }
        throw new NotFoundException();


    }

    public void postBrokenDrone() {

        this.restTemplate.postForEntity(
                maintenanceUrl + "/j/drone", new Drone(), Drone.class); //TODO handle exception

    }
}
