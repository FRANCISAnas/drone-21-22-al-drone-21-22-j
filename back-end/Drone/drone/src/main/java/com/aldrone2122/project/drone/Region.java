package com.aldrone2122.project.drone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Region {
    private Coordinates pointA;
    private Coordinates pointB;

    private String updateRoute;
    private String specialRoute;
    private String name;


    public boolean isInside(Coordinates coordinates) {
        return (Math.min(pointA.getX(), pointB.getX()) < coordinates.getX()
            && coordinates.getX() < Math.max(pointA.getX(), pointB.getX()))
            && (Math.min(pointA.getY(), pointB.getY()) < coordinates.getY()
            && coordinates.getY() < Math.max(pointA.getY(), pointB.getY()));

    }

}
