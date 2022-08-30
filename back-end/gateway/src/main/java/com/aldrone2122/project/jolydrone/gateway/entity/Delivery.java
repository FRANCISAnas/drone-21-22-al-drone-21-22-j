package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Delivery {

    private Long id;

    private FlightPlan flightPlan;

    private DeliveryStatus deliveryStatus;

    private String trackingNumber;

    private Drone drone;

    private DeliveryPackage paquet;


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((trackingNumber == null) ? 0 : trackingNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Delivery other = (Delivery) obj;
        if (trackingNumber == null) {
            if (other.trackingNumber != null)
                return false;
        } else if (!trackingNumber.equals(other.trackingNumber))
            return false;
        return true;
    }

    public DeliveryPackage getPaquet() {
        return paquet;
    }

    public void setPaquet(DeliveryPackage paquet) {
        this.paquet = paquet;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "id=" + id +
                ", flightPlan=" + flightPlan +
                ", deliveryStatus=" + deliveryStatus +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", drone=" + drone +
                ", paquet=" + paquet +
                '}';
    }
}
