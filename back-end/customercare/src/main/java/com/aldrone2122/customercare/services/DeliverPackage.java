package com.aldrone2122.customercare.services;

import com.aldrone2122.customercare.entity.Package;
import com.aldrone2122.customercare.entity.Warehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@Service
public class DeliverPackage {


    private final RestTemplate restTemplate;

    @Value("${delivery.planner.url}")
    private String deliveryPlannerUrl;

    private final Logger logger = Logger.getLogger(DeliverPackage.class.getName());

    @Autowired
    public DeliverPackage(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public DeliverPackageResponse deliverPackage(Package deliveryPackage) {
        logger.info(String.format("Asking to deliver package from %s to %s.", deliveryPackage.getOrigin().toString(), deliveryPackage.getDestination().toString()));
        var response = this.restTemplate.postForEntity(deliveryPlannerUrl + "/deliver", deliveryPackage, DeliverPackageResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("Package sent to delivery planner with success!");
            return response.getBody();
        }

        logger.info("Oops somthing went wrong!");
        //TODO handle exceptions .....
        return null;

    }

    /**
     * DeliverPackageResponse
     */
    public record DeliverPackageResponse(String trackingNumber, Warehouse warehouse) {
    }
}
