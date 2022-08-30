package com.aldrone2122.project.drone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialEvent {
    private final String type;

    private final String trackingNumber;

    private final Delivery delivery;

    public SpecialEvent(String type, String trackingNumber, Delivery delivery) {
        this.type = type;
        this.trackingNumber = trackingNumber;
        this.delivery = delivery;
    }


}
