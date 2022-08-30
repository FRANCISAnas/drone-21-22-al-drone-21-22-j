package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Region {
    private Coordinates pointA;
    private Coordinates pointB;

    private String updateRoute;
    private String specialRoute;
    private String name;

}
