package entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Package implements Serializable {
    private Long id;

    private Double weight;

    private Coordinates destination;

    private Dimensions dimensions;
}
