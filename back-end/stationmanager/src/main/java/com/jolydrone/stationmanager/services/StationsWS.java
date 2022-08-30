package com.jolydrone.stationmanager.services;

import com.jolydrone.stationmanager.entity.*;
import com.jolydrone.stationmanager.repositories.StationRepository;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class StationsWS {

    @Autowired
    private StationRepository stationRepository;

    private final Logger logger = Logger.getLogger(StationsWS.class.getName());

    public ChargingStationList getStationsInArea(Area area) {


        List<ChargingStation> stations = new ArrayList<>();

        for (ChargingStation chargingStation : stationRepository.findAll()) {
            if (chargingStation.isAvailable() && isInCircle(chargingStation.getLocation(), area)) {
                stations.add(chargingStation);
            }
        }

        logger.info(String.format("Returning %s stations", stations.size()));

        return new ChargingStationList(stations);
    }

    private boolean isInCircle(Coordinates location, Area area) {

        Point center = new Point((area.pointA.getX() + area.pointB.getX()) / 2, (area.pointA.getY() + area.pointB.getY()) / 2);
        double radius = Math.hypot(area.pointB.getX() - area.pointA.getX(), area.pointB.getY() - area.pointA.getY()) / 2;
        return Math.hypot(center.getX() - location.getX(), center.getY() - location.getY()) <= radius;

    }

    public boolean updateTerminal(UpdateTerminalRequest updateTerminalRequest) {
        Optional<ChargingStation> optStation = stationRepository.findByStationName(updateTerminalRequest.getStationName());
        if (optStation.isEmpty() || !EnumUtils.isValidEnum(TerminalStatus.class, updateTerminalRequest.getStatus())) {
            return false;
        }
        ChargingStation chargingStation = optStation.get();
        int idForTerminal = updateTerminalRequest.getIdForStation();
        if (updateTerminalRequest.getIdForStation() + 1 > chargingStation.getTerminals().size()) {
            return false;
        }

        Terminal terminal = chargingStation.getTerminal(idForTerminal);
        terminal.setStatus(TerminalStatus.valueOf(updateTerminalRequest.getStatus()));
        chargingStation.setTerminal(idForTerminal, terminal);

        stationRepository.save(chargingStation);

        return true;
    }

    public ChargingStation createGenericStation(String stationName, Double xCoordinates, Double yCoordinates) {
        ChargingStation chargingStation = new ChargingStation();
        chargingStation.setStationName(stationName);
        chargingStation.setLocation(new Coordinates(xCoordinates, yCoordinates));
        for (int i = 0; i < 5; i++) {
            Terminal terminal = new Terminal();
            terminal.setStatus(TerminalStatus.AVAILABLE);
            terminal.setIdForStation(i);
            chargingStation.getTerminals().add(terminal);
        }
        chargingStation.setTerminalCount(chargingStation.getTerminals().size());
        return stationRepository.save(chargingStation);
    }


    /**
     * Area Coordinates pointA, Coordinates pointB
     */
    public record Area(Coordinates pointA, Coordinates pointB) {

    }

    /**
     * ChargingStationList List<ChargingStation> stations
     **/
    public record ChargingStationList(List<ChargingStation> stations) {
    }
}
