package com.aldrone2122j.deliveryplanner.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
public class Coordinates {


    private Double x;

    private Double y;

    public Coordinates() {
    }

    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Double distanceToPoint(Coordinates coordinates) {
        return Math.sqrt(Math.pow(coordinates.x - x, 2) + Math.pow(coordinates.y - y, 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates that)) return false;
        return getX().equals(that.getX()) && getY().equals(that.getY());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
