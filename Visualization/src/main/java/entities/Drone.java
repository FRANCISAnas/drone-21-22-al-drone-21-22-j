package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Drone implements Serializable {

    private Long id;

    private Integer unloadedWeight;

    private Integer autonomyMaximum;

    private Integer batteryLevelInPercent;

    private DroneStatus status;

    private String name;

    private Integer maxWeighingCapacity; // is what the drone can carry at max.

    private Coordinates position;

    public Drone(String name, Coordinates position) {
        setName(name);
        setPosition(position);
    }


    @Override
    public String toString() {
        return "Drone{" +
                "id=" + id +
                ", unloadedWeight=" + unloadedWeight +
                ", autonomyMaximum=" + autonomyMaximum +
                ", batteryLevelInPercent=" + batteryLevelInPercent +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", maxWeighingCapacity=" + maxWeighingCapacity +
                ", position=" + position +
                '}';
    }
}
