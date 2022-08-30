package com.aldrone2122.customercare.services;

import com.aldrone2122.customercare.entity.CreditCard;
import com.aldrone2122.customercare.entity.PaymentResult;
import com.aldrone2122.customercare.entity.Warehouse;
import com.aldrone2122.customercare.interfaces.IEstimateCost;
import com.aldrone2122.customercare.repositories.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.logging.Logger;

@Service
public class CustomerCareWS {
    private final Logger logger = Logger.getLogger(CustomerCareWS.class.getName());
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private IEstimateCost costEstimator;
    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private DeliverPackage deliverPackage;
    private final RestTemplate restTemplate;

    @Value("${flight.monitor.gateway}")
    private String gateway;

    @Value("${delivery.tracker.url}")
    private String deliveryTrackerUrl;

    public CustomerCareWS(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public CostEstimation estimateCost(com.aldrone2122.customercare.entity.Package paquet) {

        var cost = costEstimator.estimateCost(paquet);

        paquet.setCost(cost);

        paquet = packageRepository.save(paquet);
        logger.info("Returning the estimation cost");
        return new CostEstimation(cost, paquet.getId());
    }

    public DeliveryConfirmationResult confirmDelivery(com.aldrone2122.customercare.entity.Package paquet,
                                                      CreditCard creditCard) {


        PaymentResult paymentResult = paymentService.payDelivery(creditCard, paquet.getCost());

        if (paymentResult == PaymentResult.FAILURE) {
            return new DeliveryConfirmationResult(paymentResult, null, null);
        }

        if (paymentResult == PaymentResult.SUCCESS) {
            var deliveryResponse = this.deliverPackage.deliverPackage(paquet);
            if (deliveryResponse == null) {
                //TODO throw exception
                throw new NullPointerException();
            }
            return new DeliveryConfirmationResult(paymentResult, deliveryResponse.trackingNumber(), deliveryResponse.warehouse());
        }
        //TODO
        return null;


    }

    public ResponseEntity<String> checkDeliveryStatus(String trackingNumber) {
        return this.restTemplate.getForEntity(this.deliveryTrackerUrl + "/delivery/tracking/{trackingNumber}", String.class, trackingNumber);
    }

    public record CostEstimation(BigDecimal cost, Long temporaryId) {

    }

    /**
     * DeliveryConfirmationResult
     */
    public record DeliveryConfirmationResult(PaymentResult paymentResult, String trackingNumber, Warehouse warehouse) {
    }
}
