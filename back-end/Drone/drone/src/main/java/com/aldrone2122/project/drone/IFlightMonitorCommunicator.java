package com.aldrone2122.project.drone;

public interface IFlightMonitorCommunicator {
    void updateMonitor(Delivery delivery);

    void finishedDelivery(Delivery delivery);

    void startDelivery(Delivery delivery);
}
