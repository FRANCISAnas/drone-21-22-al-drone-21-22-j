package com.aldrone2122.project.drone;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegionTest {

    @Test
    void isInsideTest() {
        Region tiscus = new Region();

        tiscus.setName("Tiscus");
        tiscus.setPointA(new Coordinates(200.0, 200.0));
        tiscus.setPointB(new Coordinates(500.0, 500.0));

        Assertions.assertTrue(tiscus.isInside(new Coordinates(350.0, 400.0)));


        Region quendaylon = new Region();

        quendaylon.setName("Quendaylon");
        quendaylon.setPointA(new Coordinates(0.0, -200.0));
        quendaylon.setPointB(new Coordinates(500.0, 0.0));

        Assertions.assertTrue(quendaylon.isInside(new Coordinates(448.0, -68.0)));

        Assertions.assertFalse(quendaylon.isInside(new Coordinates(350.0, 400.0)));
    }
}
