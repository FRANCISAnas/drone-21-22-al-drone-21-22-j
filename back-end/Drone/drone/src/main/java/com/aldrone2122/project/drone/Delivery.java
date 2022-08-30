package com.aldrone2122.project.drone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Delivery {

    private long id;
    private FlightPlan flightPlan;
    private DeliveryStatus deliveryStatus;
    private String trackingNumber;
    private Drone drone;
    private JPackage paquet;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timeStamp;
    private long clock;


    public Delivery() {

    }

    public Delivery(long id, FlightPlan flightPlan, DeliveryStatus deliveryStatus, String trackingNumber, Drone drone,
                    JPackage paquet) {
        this.id = id;
        this.flightPlan = flightPlan;
        this.deliveryStatus = deliveryStatus;
        this.trackingNumber = trackingNumber;
        this.drone = drone;
        this.paquet = paquet;
    }

    public void execute(IFlightMonitorCommunicator communicator) {

        this.flightPlan.execute(this, communicator);
    }


    public FlightPlan getFlightPlan() {
        return flightPlan;
    }

    public void setFlightPlan(FlightPlan flightPlan) {
        if (this.flightPlan != null)
            this.flightPlan = null;
        this.flightPlan = flightPlan;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Drone getDrone() {
        return drone;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getClock() {
        return clock;
    }

    public void setClock(long clock) {
        this.clock = clock;
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

    public JPackage getPaquet() {
        return paquet;
    }

    public void setPaquet(JPackage paquet) {
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

            ", paquet=" + paquet +
            '}';
    }

    public record DroneAndFlightPlan(Drone drone, FlightPlan flightPlan) {

    }
}
