package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vector {

    private final double a;
    private final double b;

    public Vector(Coordinates pointA, Coordinates pointB) {
        a = pointB.getX() - pointA.getX();
        b = pointB.getY() - pointA.getY();
    }

    public Vector(double a, double b) {
        this.a = a;
        this.b = b;
    }

}
