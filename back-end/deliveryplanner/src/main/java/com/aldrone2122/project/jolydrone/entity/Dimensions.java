package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Dimensions {

    private Double height;

    private Double width;

    private Double length;

}
