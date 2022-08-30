package com.aldrone2122.project.jolydrone.services;

import com.aldrone2122.project.jolydrone.entity.ChargingStation;
import com.aldrone2122.project.jolydrone.entity.Coordinates;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GetStationInArea {


    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(GetStationInArea.class.getName());
    @Value("${charging.station.manager.url}")
    private String chargingStationManagerUrl;


    @Autowired
    public GetStationInArea(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    /**
     * @param pointA point de départ
     * @param pointB point d'arrivée
     * @return dans l'ordre la liste des stations de recharges
     * présent entre les deux point A et B
     */
    public List<ChargingStation> getStationsInArea(Coordinates pointA, Coordinates pointB) {

        logger.info(String.format("Requesting to charging station manager to find available stations between point %s and point %s", pointA.toString(), pointB.toString()));

        ResponseEntity<ChargingStationList> stationsResponse = this.restTemplate.postForEntity(
                chargingStationManagerUrl + "/j/stations", new Area(pointA, pointB), ChargingStationList.class); //TODO handle exception

        if (stationsResponse.getStatusCode() == HttpStatus.OK) {
            var body = stationsResponse.getBody();
            if (body == null) {
                return List.of();
            }
            logger.info(String.format("Received %s stations", body.stations().size()));
            body.stations.sort((o1, o2) -> (int) ((o1.distanceTo(pointA) - o2.distanceTo(pointA))));
            return body.stations();
        } else {
            logger.info("Request returned code " + stationsResponse.getStatusCodeValue());
        }
        return List.of();

    }

    /**
     * ChargingStationList List<CharginStation> stations
     */
    public record ChargingStationList(List<ChargingStation> stations) {
    }

    /**
     * Area Coordinates pointA, Coordinates pointB
     */
    public record Area(Coordinates pointA, Coordinates pointB) {
    }

}
