package vue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import entities.Coordinates;
import entities.Drone;
import entities.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {
    public static List<Coordinates> loadCoordinates(String url, String location_key) {
        var jsonArray = loadData(url);
        if (jsonArray == null) return List.of();
        List<Coordinates> coordinates = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = null;
            try {
                object = jsonArray.getJSONObject(i);
                var coordinatesString = object.getJSONObject(location_key);
                coordinates.add(new Coordinates(coordinatesString.getDouble("x"), coordinatesString.getDouble("y")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return coordinates;
    }

    public static JSONArray loadData(String string_url) {
        try {
            URL url = new URL(string_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            con.disconnect();

            return new JSONArray(content.toString());


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Map<String, Region> loadAllRegions() {
        try {
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            Map<String, Region> regionsMap;

            regionsMap = yamlMapper.readValue(
                    JolyDroneMap.class.getClassLoader().getResourceAsStream("map.yaml"),
                    new TypeReference<Map<String, Region>>() {
                    });

            regionsMap.forEach((regionName, value) -> value.setName(regionName));
            //System.out.println(regionsMap);
            return regionsMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Drone convertStringToDrone(String message) throws JSONException {
        JSONObject deliveryUpdate = new JSONObject(message);
        JSONObject droneObject = deliveryUpdate.getJSONObject("drone");
        String droneName = droneObject.getString("name");
        JSONObject dronePosition = droneObject.getJSONObject("position");
        var x = dronePosition.getDouble("x");
        var y = dronePosition.getDouble("y");
        return new Drone(droneName, new Coordinates(x, y));
    }
}
