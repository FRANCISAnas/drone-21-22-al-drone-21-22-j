package com.jolydrone.stationmanager;

import com.jolydrone.stationmanager.entity.ChargingStation;
import com.jolydrone.stationmanager.entity.Coordinates;
import com.jolydrone.stationmanager.repositories.StationRepository;
import com.jolydrone.stationmanager.services.StationsWS;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ExtendWith(SpringExtension.class)
class StationWSTest {

    @Autowired
    private StationsWS stationsWS;

    @MockBean
    private StationRepository stationRepository; // DB mocked

    private ChargingStation s1;
    private ChargingStation s2;
    private ChargingStation s3;
    private ChargingStation s4;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        Coordinates cs1 = new Coordinates();
        cs1.setX(10.0);
        cs1.setY(15.0);

        Coordinates cs2 = new Coordinates();
        cs2.setX(13.5);
        cs2.setY(20.0);

        Coordinates cs3 = new Coordinates();
        cs3.setX(-16.0);
        cs3.setY(8.0);

        Coordinates cs4 = new Coordinates();
        cs4.setX(23.0);
        cs4.setY(31.5);

        s1 = mock(ChargingStation.class);

        s2 = mock(ChargingStation.class);

        s3 = mock(ChargingStation.class);

        s4 = mock(ChargingStation.class);

        List<ChargingStation> stations = new ArrayList<>();
        stations.add(s1);
        stations.add(s2);
        stations.add(s3);
        stations.add(s4);

        when(s1.isAvailable()).thenReturn(true);
        when(s2.isAvailable()).thenReturn(true);
        when(s3.isAvailable()).thenReturn(true);
        when(s4.isAvailable()).thenReturn(true);

        when(s1.getLocation()).thenReturn(cs1);
        when(s2.getLocation()).thenReturn(cs2);
        when(s3.getLocation()).thenReturn(cs3);
        when(s4.getLocation()).thenReturn(cs4);

        when(stationRepository.findAll()).thenReturn(stations);
    }

    @Test
    void getStationsInAreaTest() {

        Coordinates pointA = new Coordinates();
        Coordinates pointB = new Coordinates();

        pointA.setX(7.0);
        pointA.setY(22.0);

        pointB.setX(37.0);
        pointB.setY(26.0);

        StationsWS.Area area = new StationsWS.Area(pointA, pointB);


        List<ChargingStation> listExpectedStations = new ArrayList<>();
        listExpectedStations.add(s1);
        listExpectedStations.add(s2);
        listExpectedStations.add(s4);

        StationsWS.ChargingStationList expectedStations = new StationsWS.ChargingStationList(listExpectedStations);

        assertEquals(expectedStations, stationsWS.getStationsInArea(area));

        List<ChargingStation> all = new ArrayList<>();
        all.add(s1);
        all.add(s2);
        all.add(s3);
        all.add(s4);

        StationsWS.ChargingStationList allStations = new StationsWS.ChargingStationList(all);
        assertNotEquals(allStations, stationsWS.getStationsInArea(area));


        Coordinates pointC = new Coordinates();
        pointC.setX(20.0);
        pointC.setY(21.0);
        Coordinates pointD = new Coordinates();
        pointD.setX(-20.0);
        pointD.setY(15.0);
        StationsWS.Area area2 = new StationsWS.Area(pointC, pointD);

        listExpectedStations = new ArrayList<>();
        listExpectedStations.add(s1);
        listExpectedStations.add(s2);
        listExpectedStations.add(s3);

        expectedStations = new StationsWS.ChargingStationList(listExpectedStations);
        assertEquals(expectedStations, stationsWS.getStationsInArea(area2));

        assertNotEquals(allStations, stationsWS.getStationsInArea(area2));
    }

}
