package com.jolydrone.flightmonitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlightMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightMonitorApplication.class, args);
    }

}
