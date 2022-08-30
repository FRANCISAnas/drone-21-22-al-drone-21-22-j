package com.aldrone2122j.deliveryplanner.entities;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Package {
    private Long id;
    private Double weight;
    private Coordinates destination;
    private Dimensions dimensions;
}
