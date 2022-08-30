package com.aldrone2122.project.jolydrone.adapters;

import com.aldrone2122.project.jolydrone.entity.FlightPlan;
import com.aldrone2122.project.jolydrone.entity.Stopover;
import com.aldrone2122.project.jolydrone.services.DeliveryStarter;
import com.aldrone2122.project.jolydrone.services.DeliveryStarter.FlightPlanDTO;
import com.aldrone2122.project.jolydrone.services.DeliveryStarter.StationDTO;
import com.aldrone2122.project.jolydrone.services.DeliveryStarter.TerminalDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoAdapter {

    public FlightPlanDTO transformToDTOWithoutId(FlightPlan flightPlan) {

        List<Stopover> chargingStations = flightPlan.getRefillStops();

        if (chargingStations == null || chargingStations.isEmpty()) {
            return new FlightPlanDTO(flightPlan.getOrigin(), flightPlan.getDestination(), List.of());
        } else {

            return new FlightPlanDTO(flightPlan.getOrigin(), flightPlan.getDestination(),
                    flightPlan.getRefillStops().stream().map(stopover ->
                            new DeliveryStarter.StopoverDTO(
                                    new StationDTO(stopover.getChargingStation().getTerminalCount(),
                                            stopover.getChargingStation().getTerminals() != null ? stopover.getChargingStation().getTerminals().stream().map(terminal -> new TerminalDTO(terminal.getStatus(), terminal.getIdForStation())).toList() : List.of(),
                                            stopover.getChargingStation().getStationName(),
                                            stopover.getChargingStation().getLocation()
                                    ), stopover.getStopoverStatus()
                            )
                    ).toList());

        }
    }
}
