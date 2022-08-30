package vue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import entities.Coordinates;
import entities.Region;
import org.json.JSONException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class JolyDroneMap extends ZoomablePanel {
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
    private final String DRONE_MOVE_QUEUE = "drone.regular.update.#";
    JPanel centeredMap;
    Map<Integer, Position> positions;
    Map<String, Position> dronePositions;
    Thread movingPosition;
    Map<String, Region> regionsMap;
    private Channel updateChannel;

    public JolyDroneMap(String title, List<Coordinates> chargingStations, List<Coordinates> warehousesCoordinates, Map<String, Region> regionsMap) {
        super(title);
        positions = new HashMap<>();
        dronePositions = new HashMap<>();
        centeredMap.setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, 200));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(centeredMap);
        pack();
        repaint();
        initChannel();
        loadData(chargingStations, "station.png");
        loadData(warehousesCoordinates, "warehouse.png");
        this.regionsMap = regionsMap;
        movingPosition = new Thread(this::getPositions);
    }

    private void loadAllRegions() {
        try {
            regionsMap = this.yamlMapper.readValue(
                    JolyDroneMap.class.getClassLoader().getResourceAsStream("map.yaml"),
                    new TypeReference<Map<String, Region>>() {
                    });

            regionsMap.forEach((regionName, value) -> value.setName(regionName));
            //System.out.println(regionsMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int addPositionWithJFrame(Double x, Double y) {
        var index = addPosition(x, y, "drone.png", this);
        repaint();
        return index;
    }

    private int addPosition(Double x, Double y, String iconName, JFrame jFrame) {
        var index = positions.size();
        positions.put(index, new Position(index, x, y, iconName, jFrame));
        return index;
    }

    private int addPosition(Double x, Double y, String iconName) {
        var index = positions.size();
        positions.put(index, new Position(index, x, y, iconName));
        return index;
    }

    private void loadData(List<Coordinates> coordinates, String iconName) {

        for (Coordinates coordinate : coordinates) {
            addPosition(coordinate.getX(), coordinate.getY(), iconName);

            //System.out.printf("StationName: %s, Coordinate: %s%n",object.getString("stationName"),object.getString("location"));
        }


    }

    private void initChannel() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().getOrDefault("RABBIT_HOST", "localhost"));
        factory.setPort(Integer.parseInt(System.getenv().getOrDefault("RABBIT_PORT", "5672")));
        factory.setRequestedHeartbeat(0);

        try {
            Connection connection = factory.newConnection();
            this.updateChannel = connection.createChannel();
            //this.updateChannel.queueDeclare("dronecom", false, false, false, null);

            this.updateChannel.exchangeDeclare("dronecom", "topic", true, false, null);
            var result = this.updateChannel.queueDeclare("", true, true, false, null);

            this.updateChannel.queueBind(result.getQueue(), "dronecom", DRONE_MOVE_QUEUE);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    var drone = Utils.convertStringToDrone(message);
                    if (!dronePositions.containsKey(drone.getName())) {
                        var dronePosition = drone.getPosition();
                        int dronePositionIndex = addPositionWithJFrame(dronePosition.getX(), dronePosition.getY());
                        dronePositions.put(drone.getName(), positions.get(dronePositionIndex));
                    } else {
                        var position = dronePositions.get(drone.getName());
                        position.moveTo(drone.getPosition());
                    }

                    //System.out.println(" [x] Received '" + message + "'");
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                }
            };
            this.updateChannel.basicConsume(result.getQueue(), true, deliverCallback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawDashedLine(g, -getWidth() * 10, getHeight() / 2, getWidth() * 10, getHeight() / 2);
        drawDashedLine(g, getWidth() / 2, -getHeight() * 10, getWidth() / 2, getHeight() * 10);
        //g.setColor(Color.BLUE);
        g.fillOval(getWidth() / 2 - 2, getHeight() / 2 - 3, 5, 5);


        int yblue = 0;
        for (Region region : regionsMap.values()) {

            Coordinates topLeftCoordinates = null;
            if (region.getPointA().getX() < region.getPointB().getX()) {
                topLeftCoordinates = region.getPointA();
            } else {
                topLeftCoordinates = region.getPointB();
            }

            var x = 0;
            var y = 0;
            var width = (int) Math.abs(region.getPointA().getX() - region.getPointB().getX());
            var height = (int) Math.abs(region.getPointA().getY() - region.getPointB().getY());
            if (topLeftCoordinates.getX() == 0.0 && topLeftCoordinates.getY() == 0.0) {
                g.setColor(Color.YELLOW);
                yblue = topLeftCoordinates.getY().intValue() + getHeight() / 2 - height;
                x = topLeftCoordinates.getX().intValue() + getWidth() / 2;
                y = yblue;
                g.drawRect(x, y, width, height);
                drawString(g, region.getName(), x + width / 3, y + height * 6 / 11);
            } else if (topLeftCoordinates.getX() == 200.0 && topLeftCoordinates.getY() == 200.0) {
                g.setColor(Color.GREEN);
                x = topLeftCoordinates.getX().intValue() + getWidth() / 2;
                y = yblue - height;
                g.drawRect(x, y, width, height);
                drawString(g, region.getName(), x + width / 3, y + height / 2);
            } else if (topLeftCoordinates.getX() == 200.0 && topLeftCoordinates.getY() == 0.0) {
                g.setColor(Color.RED);
                x = topLeftCoordinates.getX().intValue() + getWidth() / 2;
                y = topLeftCoordinates.getY().intValue() + getHeight() / 2 - height;
                g.drawRect(x, y, width, height);
                drawString(g, region.getName(), x + width / 3, y + height / 2);
            } else if (topLeftCoordinates.getX() == 0.0 && topLeftCoordinates.getY() == -200.0) {
                g.setColor(Color.BLUE);
                x = topLeftCoordinates.getX().intValue() + getWidth() / 2;
                y = topLeftCoordinates.getY().intValue() + getHeight() / 2 + height;
                g.drawRect(x, y, width, height);
                drawString(g, region.getName(), x + width / 3, y + height / 2);
            } else if (topLeftCoordinates.getX() == 0.0 && topLeftCoordinates.getY() == 200.0) {
                g.setColor(Color.PINK);
                x = topLeftCoordinates.getX().intValue() + getWidth() / 2;
                y = yblue - height;
                g.drawRect(x, y, width, height);
                drawString(g, region.getName(), x + width / 3, y + height / 2);
            }

        }

        //System.out.printf("Printed Region %s %s %s %s%n",topLeftCoordinates.getX().intValue()+getWidth()/2,topLeftCoordinates.getY().intValue()+getHeight()/2+height,topLeftCoordinates.getX(),topLeftCoordinates.getY());

        positions.values().forEach(position -> position.draw(g, getSize()));
    }

    private void drawString(Graphics g, String string, int x, int y) {
        if (g instanceof Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawString(string, x, y);
        }
    }

    private int getXOnMap(int width, int x, int weight) {
        return (width / 2 - weight / 2 + x);
    }

    private int getYOnMap(int height, int y, int Height) {
        return (height / 2 - Height / 2 - y);
    }

    public void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2) {

        // Create a copy of the Graphics instance
        Graphics2D g2d = (Graphics2D) g.create();

        // Set the stroke of the copy, not the original
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0);
        g2d.setStroke(dashed);

        // Draw to the copy
        g2d.drawLine(x1, y1, x2, y2);

        // Get rid of the copy
        g2d.dispose();
    }


    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        positions.values().forEach(position -> position.draw(g, getSize()));
        Graphics2D g2d = (Graphics2D) g.create();
        for (var position : positions.values()) {
            String text = position.longitude + "|" + position.latitude;
            var defaultCaret = new DefaultCaret();
            var ellipse2D = new Ellipse2D.Double(position.longitude, position.latitude, 5, 5);
            g2d.fill(ellipse2D);
        }
        g2d.dispose();

        //positions.forEach(position->position.draw(getContentPane().getGraphics(),getSize()));
    }

    public void getPositions() {
        try {
            //On se connecte à Wikipedia
            Socket sock = new Socket("localhost", 8080);

            //Nous allons faire une demande au serveur web
            //ATTENTION : les \r\n sont OBLIGATOIRES ! Sinon ça ne fonctionnera pas ! !
            String request = "GET /api/positions/flux HTTP/1.1\r\n";
            request += "Host: localhost:8080\r\n";
            request += "Content-Type: application/json\r\n";
            request += "\r\n";

            //nous créons donc un flux en écriture
            BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());

            //nous écrivons notre requête
            bos.write(request.getBytes());
            //Vu que nous utilisons un buffer, nous devons utiliser la méthode flush
            //afin que les données soient bien écrite et envoyées au serveur
            bos.flush();

            //On récupère maintenant la réponse du serveur
            //dans un flux, comme pour les fichiers...
            BufferedInputStream bis = new BufferedInputStream(sock.getInputStream());

            //Il ne nous reste plus qu'à le lire
            String content = "";
            int stream;
            byte[] b = new byte[1024];
            String data = "";
            ObjectMapper objectMapper = new ObjectMapper();

            System.out.println("#recieving new positions...");
            while ((stream = bis.read(b)) != -1) {
                content = new String(b, 0, stream);
                if (content.contains("\"id\":")) {
                    Coordinates coordinates = objectMapper.readValue(content.substring(content.indexOf('{'), content.lastIndexOf('}') + 1), Coordinates.class);
                    System.out.println(coordinates);
                        /*if(!positions.containsKey(coordinates.getId()))
                        positions.put(position.getId(),new Position(
                                position.getId(),
                                position.getLongitude(),
                                position.getLatitude(),
                                this
                        ));
                        positions.get(position.getId()).moveTo(position);
                        System.out.println("value after move: "+positions.get(position.getId()));*/
                    repaint();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
