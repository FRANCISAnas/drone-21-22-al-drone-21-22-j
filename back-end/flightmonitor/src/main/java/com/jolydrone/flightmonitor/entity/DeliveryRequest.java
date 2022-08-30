package com.jolydrone.flightmonitor.entity;

import java.io.Serializable;


public class DeliveryRequest implements Serializable {
    private String id;

    public DeliveryRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
