package com.aldrone2122.project.deliverytracking.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialEvent {
    private String type;

    private String trackingNumber;

    private Delivery delivery;
}
