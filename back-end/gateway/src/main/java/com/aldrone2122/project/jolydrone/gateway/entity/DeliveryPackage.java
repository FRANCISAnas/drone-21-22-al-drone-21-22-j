package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeliveryPackage {


    private Long id;

    private Double weight;


    private Coordinates destination;


    private Dimensions dimensions;


}
