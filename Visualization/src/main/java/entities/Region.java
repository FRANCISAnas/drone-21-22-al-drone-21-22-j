package entities;

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

    public Coordinates getTopLeft() {
        return new Coordinates(pointA.getX(), pointA.getY() - getHeight());
    }

    public int getWidth() {
        return Math.abs((int) (pointA.getX() - pointB.getX()));
    }

    public int getHeight() {
        return Math.abs((int) (pointA.getY() - pointB.getY()));
    }

    public boolean isInside(Coordinates coordinates) {
        return (Math.min(pointA.getX(), pointB.getX()) < coordinates.getX()
                && coordinates.getX() < Math.max(pointA.getX(), pointB.getX()))
                && (Math.min(pointA.getY(), pointB.getY()) < coordinates.getY()
                && coordinates.getY() < Math.max(pointA.getY(), pointB.getY()));

    }

    @Override
    public String toString() {
        return "Region{" +
                "pointA=" + pointA +
                ", pointB=" + pointB +
                ", updateRoute='" + updateRoute + '\'' +
                ", specialRoute='" + specialRoute + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
