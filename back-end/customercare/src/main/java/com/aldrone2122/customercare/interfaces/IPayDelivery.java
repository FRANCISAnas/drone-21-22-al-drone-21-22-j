package com.aldrone2122.customercare.interfaces;

import com.aldrone2122.customercare.entity.CreditCard;
import com.aldrone2122.customercare.entity.PaymentResult;

import java.math.BigDecimal;

public interface IPayDelivery {


    PaymentResult payDelivery(CreditCard creditCard, BigDecimal amount);
}
