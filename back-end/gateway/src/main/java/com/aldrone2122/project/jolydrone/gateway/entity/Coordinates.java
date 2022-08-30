package com.aldrone2122.project.jolydrone.gateway.entity;


import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Coordinates {

    private Double x;

    private Double y;

    public Coordinates() {
    }

    public Coordinates(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates move(Vector displacement) {
        return new Coordinates(x + displacement.getA(), y + displacement.getB());
    }

    public boolean match(Coordinates other) {
        return this.distanceToPoint(other) <= 1;
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

    /**
     * @param region the region in which the drone is
     * @return true if Xa <= x <= Xb and Ya <= y <=Yb
     */

    public boolean isInRegion(Region region) {
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("region = " + region);
        System.out.println();

        return (Math.min(region.getPointA().x, region.getPointB().x) <= this.x
                && this.x <= Math.max(region.getPointA().x, region.getPointB().x))
                && (Math.min(region.getPointA().y, region.getPointB().y)) <= this.y
                && this.y <= Math.max(region.getPointA().y, region.getPointB().y);

    }
}
