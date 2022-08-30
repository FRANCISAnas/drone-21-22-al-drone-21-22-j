package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
public class Delivery implements Serializable {

    private Long id;
    private FlightPlan flightPlan;
    private DeliveryStatus deliveryStatus;
    private String trackingNumber;
    private Drone drone;
    private java.lang.Package paquet;

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

}
