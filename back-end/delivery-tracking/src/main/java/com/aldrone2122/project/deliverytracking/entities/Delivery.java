package com.aldrone2122.project.deliverytracking.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
public class Delivery implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private FlightPlan flightPlan;

    private DeliveryStatus deliveryStatus;

    @NotBlank
    @Column(unique = true)
    private String trackingNumber;

    @OneToOne(cascade = CascadeType.ALL)
    private Drone drone;

    @OneToOne(cascade = CascadeType.ALL)
    private Package paquet;

    private LocalDateTime timeStamp;

    private long clock;


    public void setFlightPlan(FlightPlan flightPlan) {
        if (this.flightPlan != null)
            this.flightPlan = null;
        this.flightPlan = flightPlan;
    }


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
