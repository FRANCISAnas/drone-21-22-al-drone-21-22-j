package graphicalItems;

import entities.ChargingStation;
import entities.Coordinates;
import entities.IsInside;
import entities.MapCoordinate;
import vue.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ChargingStationItem extends ChargingStation implements GraphicalItem, GraphicalStandardPointItem, GraphicalRandomColor {

    private BufferedImage img;

    public ChargingStationItem(Coordinates coordinates) {
        super(coordinates);
        try {
            img = ImageIO.read(Objects.requireNonNull(Position.class.getClassLoader().getResourceAsStream("station.png")));
        } catch (IOException exc) {
            //TODO: Handle exception.
            System.out.println(exc.toString());
        }
    }

    public synchronized void draw(Graphics g, MapCoordinate mapOnX, MapCoordinate mapOnY, IsInside isInside) {
        if (!isInside.check(getLocation())) return;
        var previousColor = g.getColor();
        g.setColor(color);
        var x = mapOnX.map(getLocation().getX().intValue());
        var y = mapOnY.map(getLocation().getY().intValue());

        if (img != null) {
            g.drawImage(img, x, y, width, height, null);
        } else {
            g.fillOval(x, y, width, height);
        }
        g.setColor(previousColor);
    }

}
