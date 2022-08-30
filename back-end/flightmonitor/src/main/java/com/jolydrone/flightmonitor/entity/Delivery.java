package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

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
            return other.trackingNumber == null;
        } else return trackingNumber.equals(other.trackingNumber);
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
