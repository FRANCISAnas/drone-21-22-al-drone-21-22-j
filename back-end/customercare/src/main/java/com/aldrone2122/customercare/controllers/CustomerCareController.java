package com.aldrone2122.customercare.controllers;

import com.aldrone2122.customercare.entity.CreditCard;
import com.aldrone2122.customercare.repositories.PackageRepository;
import com.aldrone2122.customercare.services.CustomerCareWS;
import com.aldrone2122.customercare.services.CustomerCareWS.CostEstimation;
import com.aldrone2122.customercare.services.CustomerCareWS.DeliveryConfirmationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
public class CustomerCareController {

    private final Logger logger = Logger.getLogger(CustomerCareController.class.getName());
    @Autowired
    private CustomerCareWS customerCareWS;
    @Autowired
    private PackageRepository packageRepository;

    @PostMapping("/customers/package/cost")
    public CostEstimation estimateCost(@RequestBody @Valid com.aldrone2122.customercare.entity.Package paquet) {
        logger.info("---------- Estimating the price of the delivery ----------");
        return this.customerCareWS.estimateCost(paquet);
    }


    @PostMapping("/customers/package/confirm/{id}")
    public DeliveryConfirmationResult confirmDelivery(@PathVariable("id") com.aldrone2122.customercare.entity.Package paquet, @RequestBody @Valid CreditCard creditCard) {
        logger.info(String.format("---------- Validating delivery with credit card %s. ----------", creditCard));
        return customerCareWS.confirmDelivery(paquet, creditCard);
    }


    @GetMapping("/customers/delivery/{trackingNumber}/status")
    public ResponseEntity<String> checkDeliveryStatus(@PathVariable("trackingNumber") String trackingNumber) {
        return this.customerCareWS.checkDeliveryStatus(trackingNumber);
    }

    /**
     * ConfirmDeliveryDTO
     */
    public record ConfirmDeliveryDTO(Long temporaryId) {
    }

    /**
     *
     */

}



