package graphicalItems;

import entities.Coordinates;
import entities.Drone;
import entities.IsInside;
import entities.MapCoordinate;
import vue.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class DroneItem extends Drone implements GraphicalItem, GraphicalStandardPointItem, GraphicalMovementBrowsableItem, GraphicalMovableItem, GraphicalRandomColor {

    private BufferedImage img;

    public DroneItem(String droneName, Coordinates coordinates) {
        super(droneName, coordinates);

        try {
            img = ImageIO.read(Objects.requireNonNull(Position.class.getClassLoader().getResourceAsStream("drone.png")));
        } catch (IOException exc) {
            //TODO: Handle exception.
            System.out.println(exc.toString());
        }
    }

    @Override
    public void setPosition(Coordinates location) {
        super.setPosition(location);
        previousX.add(location.getX());
        previousY.add(location.getY());
    }


    public synchronized void draw(Graphics g, MapCoordinate mapOnX, MapCoordinate mapOnY, IsInside isInside) {
        var previousColor = g.getColor();
        g.setColor(color);
        var x = mapOnX.map(getPosition().getX().intValue());
        var y = mapOnY.map(getPosition().getY().intValue());
        if (isInside.check(getPosition()))
            if (img != null) {
                g.drawImage(img, x, y, width, height, null);
            } else {
                g.fillOval(x, y, width, height);
            }

        if (previousX.size() > 2) {
            for (int i = 0; i < previousX.size() - 2; i++) {
                var pX1 = previousX.get(i);
                var pY1 = previousY.get(i);
                var pX2 = previousX.get(i + 1);
                var pY2 = previousY.get(i + 1);
                var x1 = mapOnX.map(pX1.intValue());
                var y1 = mapOnY.map(pY1.intValue());
                var x2 = mapOnX.map(pX2.intValue());
                var y2 = mapOnY.map(pY2.intValue());
                if (isInside.check(new Coordinates(pX1, pY1)) && isInside.check(new Coordinates(pX2, pY2)))
                    g.drawLine(x1, y1, x2, y2);
            }
        }
        g.setColor(previousColor);
    }

    @Override
    public void moveTo(Coordinates coordinates) {
        setPosition(coordinates);
    }
}
