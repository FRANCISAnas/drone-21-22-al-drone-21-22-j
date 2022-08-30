package com.jolydrone.flightmonitor.controllers;

import com.jolydrone.flightmonitor.entity.DeliveryStatus;
import com.jolydrone.flightmonitor.entity.Drone;
import com.jolydrone.flightmonitor.entity.FlightPlan;
import com.jolydrone.flightmonitor.entity.Package;
import com.jolydrone.flightmonitor.services.DroneLauncherWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.logging.Logger;

@RestController
@RequestMapping("/flight-monitor")
public class DroneLauncherController {

    @Autowired
    javax.validation.Validator validator;
    @Autowired
    private DroneLauncherWS droneLauncherWS;
    private final Logger logger = Logger.getLogger(DroneLauncherController.class.getName());

    @PostMapping("/flight/start")
    public String launchDrone(@RequestBody @Valid DeliveryDTO deliveryDTO) {
        this.logger.info(String.format(
                "---------- Receive launch request to start delivery with trackingNumber %s, from %s to %s. ----------",
                deliveryDTO.trackingNumber, deliveryDTO.flightPlan.getOrigin().toString(),
                deliveryDTO.flightPlan.getDestination().toString()));
        this.droneLauncherWS.launchDrone(deliveryDTO);

        return "OK";
    }

    /**
     * DeliveryDTO
     */
    public record DeliveryDTO(@NotNull FlightPlan flightPlan, @NotNull DeliveryStatus deliveryStatus,
                              @NotBlank String trackingNumber,
                              @NotNull Package paquet, @NotNull Drone drone) {
    }
}
