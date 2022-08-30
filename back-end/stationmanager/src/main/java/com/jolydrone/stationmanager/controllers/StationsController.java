package com.jolydrone.stationmanager.controllers;

import com.jolydrone.stationmanager.entity.ChargingStation;
import com.jolydrone.stationmanager.entity.Terminal;
import com.jolydrone.stationmanager.entity.TerminalStatus;
import com.jolydrone.stationmanager.entity.UpdateTerminalRequest;
import com.jolydrone.stationmanager.repositories.StationRepository;
import com.jolydrone.stationmanager.services.StationsWS;
import com.jolydrone.stationmanager.services.StationsWS.Area;
import com.jolydrone.stationmanager.services.StationsWS.ChargingStationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
public class StationsController {


    @Autowired
    private StationsWS stationsWS;

    @Autowired
    private StationRepository stationRepository;

    private final Logger logger = Logger.getLogger(StationsController.class.getName());

    @PostMapping("/j/stations")
    public ChargingStationList fetchStationsInArea(@RequestBody Area area) {
        logger.info(String.format("Received request to find available stations between point %s and point %s", area.pointA().toString(), area.pointB().toString()));

        return stationsWS.getStationsInArea(area);

    }

    @GetMapping("/j/stations")
    public List<ChargingStation> getAllStations() {
        logger.info("Fetching all station");

        return stationRepository.findAll();

    }

    @GetMapping("/j/stations/{name}/repair")
    public ChargingStation repairStation(@PathVariable("name") String name) {
        Optional<ChargingStation> optionalChargingStation = stationRepository.findByStationName(name);
        if (optionalChargingStation.isPresent()) {
            logger.info("Repairing Station " + name);
            var chargingStation = optionalChargingStation.get();
            for (int i = 0; i < chargingStation.getTerminalCount(); i++) {
                chargingStation.getTerminal(i).setStatus(TerminalStatus.AVAILABLE);
            }
            return stationRepository.save(chargingStation);
        }

        logger.info("Station " + name + " not found.");
        return null;

    }

    @GetMapping("/j/stations/{name}/sentForRepair")
    public ChargingStation sentForRepair(@PathVariable("name") String name) {
        Optional<ChargingStation> optionalChargingStation = stationRepository.findByStationName(name);
        if (optionalChargingStation.isPresent()) {
            logger.info(String.format("Set station %s as damaged.", name));
            var chargingStation = optionalChargingStation.get();
            for (int i = 0; i < chargingStation.getTerminalCount(); i++) {
                chargingStation.getTerminal(i).setStatus(TerminalStatus.OUT_OF_SERVICE);
            }
            //chargingStation.setTerminalCount(0);
            return stationRepository.save(chargingStation);
        }

        logger.info("Station " + name + " not found.");
        return null;

    }

    @PostMapping("/j/updateTerminal")
    public void updateTerminal(@RequestBody UpdateTerminalRequest updateTerminalRequest) {

        stationsWS.updateTerminal(updateTerminalRequest);

    }

    @GetMapping("/j/createTerminal/{name}+{xCoordinates}+{yCoordinates}")
    public ChargingStation createTerminal(@PathVariable("name") String name, @PathVariable("xCoordinates") Double xCoordinates, @PathVariable("yCoordinates") Double yCoordinates) {
        logger.info("Creating charging station " + name + " at location : " + xCoordinates.toString() + ", " + yCoordinates.toString());
        return stationsWS.createGenericStation(name, xCoordinates, yCoordinates);

    }

    @GetMapping("/j/viewStations")
    public void viewStations() {
        List<ChargingStation> stations = stationRepository.findAll();
        for (ChargingStation c : stations) {
            logger.info(c.toString());
        }
    }

    @GetMapping("/j/viewStationTerminals/{name}")
    public void viewStations(@PathVariable("name") String name) {
        Optional<ChargingStation> optStation = stationRepository.findByStationName(name);
        if (optStation.isEmpty()) {
            return;
        }
        ChargingStation station = optStation.get();
        for (Terminal t : station.getTerminals()) {
            logger.info(t.toString());
        }
    }

}
