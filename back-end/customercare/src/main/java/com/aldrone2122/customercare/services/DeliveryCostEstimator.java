package com.aldrone2122.customercare.services;

import com.aldrone2122.customercare.entity.Coordinates;
import com.aldrone2122.customercare.entity.Package;
import com.aldrone2122.customercare.interfaces.IEstimateCost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DeliveryCostEstimator implements IEstimateCost {

    @Value("${delivery.unit.cost}")
    private BigDecimal unitCost;

    @Override
    public BigDecimal estimateCost(Package package1) {
        return unitCost.multiply(BigDecimal.valueOf(this.distance(package1.getDestination(), package1.getOrigin())));
    }


    private double distance(Coordinates a, Coordinates b) {
        return Math.hypot((a.getX() - b.getX()), (a.getY() - b.getY()));
    }

}
