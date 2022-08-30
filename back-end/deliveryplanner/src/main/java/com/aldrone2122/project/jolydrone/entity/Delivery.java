package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Delivery {


    private Long id;

    private String trackingNumber;

    private FlightPlan flightPlan;

    private DeliveryStatus deliveryStatus;

    private Drone drone;

    private Package paquet;

}
