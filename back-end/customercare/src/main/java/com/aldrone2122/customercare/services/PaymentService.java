package com.aldrone2122.customercare.services;

import com.aldrone2122.customercare.entity.CreditCard;
import com.aldrone2122.customercare.entity.PaymentResult;
import com.aldrone2122.customercare.interfaces.IPayDelivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.logging.Logger;

@Service
public class PaymentService implements IPayDelivery {

    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(PaymentService.class.getName());
    @Value("${bank.server.url}")
    private String bankUrl;
    @Value("${jolydrone.iban}")
    private String iban;
    @Value("${jolydrone.swiftcode}")
    private String swiftcode;
    private BankAccount bankAccount;

    @Autowired
    public PaymentService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    @Override
    public PaymentResult payDelivery(CreditCard creditCard, BigDecimal amount) {

        var paymentRequest = new PaymentRequest(creditCard, this.bankAccount, amount);

        logger.info("Payment...");

        var response = restTemplate.postForEntity(bankUrl, paymentRequest, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {

            logger.info("Payment succeeded!");
            return PaymentResult.valueOf(response.getBody());
        }

        logger.info("Payment Failed!");
        return PaymentResult.FAILURE;

    }

    @PostConstruct
    private void initAccount() {
        this.bankAccount = new BankAccount(this.iban, this.swiftcode);
    }

    /**
     * PaymentRequest
     */
    public record PaymentRequest(CreditCard creditCard, BankAccount jolydroneAccount, BigDecimal amount) {
    }

    public record BankAccount(String iban, String swiftCode) {
    }

}
