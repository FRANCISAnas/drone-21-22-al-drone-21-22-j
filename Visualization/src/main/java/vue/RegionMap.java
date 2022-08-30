package vue;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import entities.Coordinates;
import entities.MapCoordinate;
import entities.Region;
import graphicalItems.ChargingStationItem;
import graphicalItems.DroneItem;
import graphicalItems.GraphicalStandardPointItem;
import graphicalItems.WarehouseItem;
import org.json.JSONException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RegionMap extends ZoomablePanel implements GraphicalStandardPointItem {

    private final Region region;
    private final Coordinates origin;
    private final List<ChargingStationItem> chargingStationItemList;
    private final List<WarehouseItem> warehouseItemList;
    private final Map<String, DroneItem> droneItemMap;
    JPanel centeredMap = new JPanel();

    public RegionMap(Region region, List<Coordinates> chargingStations, List<Coordinates> warehousesCoordinates, boolean closeOtherWindowsOnClose) {
        super(String.format("Region: %s", region.getName()));
        this.region = region;
        this.origin = region.getTopLeft();
        chargingStationItemList = new ArrayList<>();
        warehouseItemList = new ArrayList<>();
        droneItemMap = new HashMap<>();

        centeredMap.setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, 200));

        if (closeOtherWindowsOnClose)
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setContentPane(centeredMap);
        pack();
        repaint();

        setPreferredSize(new Dimension(region.getWidth(), region.getHeight()));

        initiateStations(chargingStations);
        initiateWarehouses(warehousesCoordinates);

        initChannelForDroneEvents();
    }

    public MapCoordinate getXOnMap() {
        return (x) -> x - origin.getX().intValue() - width * 4 / 3;
    }

    public MapCoordinate getYOnMap() {
        return (y) -> region.getHeight() - (y - origin.getY().intValue() - 200);
    }

    public void initiateWarehouses(List<Coordinates> warehousesCoordinates) {
        if (warehousesCoordinates == null) return;
        warehousesCoordinates.forEach((warehouseCoordinate) -> warehouseItemList.add(new WarehouseItem(warehouseCoordinate)));
    }

    public void initiateStations(List<Coordinates> stationsCoordinates) {
        if (stationsCoordinates == null) return;
        stationsCoordinates.forEach((stationCoordinates) -> chargingStationItemList.add(new ChargingStationItem(stationCoordinates)));
    }


    private void initChannelForDroneEvents() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv().getOrDefault("RABBIT_HOST", "localhost"));
        factory.setPort(Integer.parseInt(System.getenv().getOrDefault("RABBIT_PORT", "5672")));
        factory.setRequestedHeartbeat(0);

        try {
            Connection connection = factory.newConnection();
            var updateChannel = connection.createChannel();
            //this.updateChannel.queueDeclare("dronecom", false, false, false, null);

            updateChannel.exchangeDeclare("dronecom", "topic", true, false, null);
            var result = updateChannel.queueDeclare("", true, true, false, null);

            updateChannel.queueBind(result.getQueue(), "dronecom", region.getUpdateRoute());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                try {
                    var drone = Utils.convertStringToDrone(message);
                    if (!droneItemMap.containsKey(drone.getName())) {
                        droneItemMap.put(drone.getName(), new DroneItem(drone.getName(), drone.getPosition()));
                    } else {
                        droneItemMap.get(drone.getName()).moveTo(drone.getPosition());
                    }
                    repaint();
                    //System.out.println(" [x] Received '" + message + "'");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            updateChannel.basicConsume(result.getQueue(), true, deliverCallback, consumerTag -> {
            });

        } catch (IOException | TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        chargingStationItemList.forEach(chargingStationItem ->
                chargingStationItem.draw(g, getXOnMap(), getYOnMap(), region::isInside)
        );
        warehouseItemList.forEach(warehouseItem ->
                warehouseItem.draw(g, getXOnMap(), getYOnMap(), region::isInside)
        );
        droneItemMap.values().forEach(droneItem ->
                droneItem.draw(g, getXOnMap(), getYOnMap(), region::isInside)
        );
    }

}
