package com.aldrone2122.customercare.interfaces;

import com.aldrone2122.customercare.entity.Package;

import java.math.BigDecimal;

public interface IEstimateCost {

    BigDecimal estimateCost(Package package1);
}
