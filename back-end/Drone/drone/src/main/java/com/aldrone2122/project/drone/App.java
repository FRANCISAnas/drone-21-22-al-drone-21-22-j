package com.aldrone2122.project.drone;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        Drone drone = new Drone(System.getenv("DRONE_NAME"), Integer.parseInt(System.getenv("UNLOADED_WEIGHT")),
            Integer.parseInt(System.getenv("AUTONOMY_MAXIMUM")),
            Integer.parseInt(System.getenv("BATTERY_LEVEL_IN_PERCENT")),
            DroneStatus.valueOf(System.getenv("STATUS")), Integer.parseInt(System.getenv("MAXIMUM_WEIGHING_CAPACITY")));

        drone.start();
    }
}
