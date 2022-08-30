import entities.Region;
import vue.JolyDroneMap;
import vue.RegionMap;
import vue.Utils;

import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Map<String, Region> regions = Utils.loadAllRegions();

        var stationsCoordinate = Utils.loadCoordinates(System.getenv().getOrDefault("CHARGING_STATION_LINK", "http://localhost:8084/j/stations"), "location");
        var warehousesCoordinate = Utils.loadCoordinates(System.getenv().getOrDefault("DELIVERY_PLANNER_LINK", "http://localhost:8082/j/warehouses"), "location");

        JolyDroneMap frame = new JolyDroneMap("JolyDrone Map", stationsCoordinate, warehousesCoordinate, regions);
        frame.setSize(1300, 700);
        frame.setVisible(true);

        /*if (regions != null) {
            regions.values().forEach(region -> {
                var regionFrame = new RegionMap(region, stationsCoordinate, warehousesCoordinate, false);
                regionFrame.setSize(region.getWidth() + 100, region.getHeight() + 25);
                regionFrame.setVisible(true);
            });
        }*/

    }
}
