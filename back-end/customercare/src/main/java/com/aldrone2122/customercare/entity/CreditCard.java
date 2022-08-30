package com.aldrone2122.customercare.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCard {

    private String cvv;
    private String cardNumber;
    private String expirationDate;

    @Override
    public String toString() {
        return "CreditCard [cardNumber=" + cardNumber + ", cvv=" + cvv + ", expirationDate=" + expirationDate + "]";
    }

}
