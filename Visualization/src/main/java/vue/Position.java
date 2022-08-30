package vue;

import entities.Coordinates;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.ConstructorProperties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Getter
@Setter
public class Position {

    public final static int positionW = 10;
    public final static int positionH = 10;
    public int id;
    Double longitude;
    Double latitude;
    List<Double> previousLongitudes;
    List<Double> previousLatitudes;
    Double initialLongitude;
    Double initialLatitude;
    private Color imageBorderColor;
    private BufferedImage img = null;
    private Color color = getRandomColor();
    private JFrame jFrame;

    @ConstructorProperties({"id", "longitude", "latitude"})
    public Position(int id, Double longitude, Double latitude) {
        this.id = id;
        this.longitude = longitude;
        previousLongitudes = new ArrayList<>();
        previousLongitudes.add(longitude);
        this.latitude = latitude;
        previousLatitudes = new ArrayList<>();
        previousLatitudes.add(latitude);
    }

    public Position(int id, Double longitude, Double latitude, Color color) {
        this(id, longitude, latitude);
        this.color = color;
    }

    public Position(int id, Double longitude, Double latitude, String iconName, JFrame jFrame, Color imageBorderColor) {
        this(id, longitude, latitude, iconName, jFrame);
        this.imageBorderColor = imageBorderColor;
    }

    public Position(int id, Double longitude, Double latitude, String iconName, JFrame jFrame) {
        this(id, longitude, latitude, iconName);
        this.jFrame = jFrame;
    }

    public Position(int id, Double longitude, Double latitude, String iconName) {
        this(id, longitude, latitude);
        try {
            //this.getClass().getClassLoader().getResourceAsStream("delivery-test.json")new File(
            img = ImageIO.read(Objects.requireNonNull(Position.class.getClassLoader().getResourceAsStream(iconName)));
        } catch (IOException exc) {
            //TODO: Handle exception.
            System.out.println(exc.toString());
        }
    }

    public Position(int id, Double longitude, Double latitude, JFrame jFrame) {
        this(id, longitude, latitude);
        this.jFrame = jFrame;
    }

    private Color getRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }

    private int getXOnMap(int width) {

        return getXOnMap(longitude.intValue(), width);
    }

    private int getXOnMap(int x, int width) {

        return (width / 2 - positionW / 2 + x);
    }

    private int getYOnMap(int height) {
        return getYOnMap(latitude.intValue(), height);
    }

    private int getYOnMap(int y, int height) {
        return (height / 2 - positionH / 2 - y);
    }

    @Override
    public String toString() {
        return longitude + " " + latitude;
    }

    public synchronized void draw(Graphics g, Dimension size) {
        g.setColor(color);
        var x = getXOnMap((int) size.getWidth());
        var y = getYOnMap((int) size.getHeight());
        if (img != null) {
            g.drawImage(img, x, y, 15, 15, null);
            if (previousLatitudes.size() > 2) {
                for (int i = 0; i < previousLatitudes.size() - 2; i++) {
                    var x1 = getXOnMap(previousLongitudes.get(i).intValue(), (int) size.getWidth());
                    var y1 = getYOnMap(previousLatitudes.get(i).intValue(), (int) size.getHeight());
                    var x2 = getXOnMap(previousLongitudes.get(i + 1).intValue(), (int) size.getWidth());
                    var y2 = getYOnMap(previousLatitudes.get(i + 1).intValue(), (int) size.getHeight());
                    g.drawLine(x1, y1, x2, y2);
                }
            }
            if (imageBorderColor != null) {
                g.drawRect(x, y, 15, 15);
            }
        } else {
            g.fillOval(x, y, positionW, positionH);
        }
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
        previousLongitudes.add(longitude);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
        previousLatitudes.add(latitude);
    }

    public void move() {
        incrementLongitude();
        incrementLatitude();
        jFrame.repaint();
    }

    public void moveTo(Coordinates coordinates) throws InterruptedException {
        setLongitude(coordinates.getX());
        setLatitude(coordinates.getY());
        jFrame.repaint();
        //new Thread(() -> {
            /*while (!longitude.equals(coordinates.getX()) || !latitude.equals(coordinates.getY())){
                Thread.sleep(10);
                if(longitude-coordinates.getX()>=1)decrementLongitude();
                if(longitude-coordinates.getX()<=-1)incrementLongitude();
                if(latitude-coordinates.getY()>=1)decrementLatitude();
                if(latitude-coordinates.getY()<=-1)incrementLatitude();
                if(longitude-coordinates.getX()<1&&longitude-coordinates.getX()>-1)longitude=coordinates.getX();
                if(latitude-coordinates.getY()<1&&latitude-coordinates.getY()>-1)latitude=coordinates.getY();
                jFrame.repaint();
            }*/
        //}).start();*/
    }

    private void incrementLatitude() {
        latitude = latitude + 1 >= 360 ? -360 : ++latitude;
    }

    private void incrementLongitude() {
        longitude = longitude + 1 >= 360 ? -360 : ++longitude;
    }

    private void decrementLatitude() {
        latitude = latitude - 1 <= -360 ? 360 : --latitude;
    }

    private void decrementLongitude() {
        longitude = longitude - 1 <= -360 ? 360 : --longitude;
    }
}

