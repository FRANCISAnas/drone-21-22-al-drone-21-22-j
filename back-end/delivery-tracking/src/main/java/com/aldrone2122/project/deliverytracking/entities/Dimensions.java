package com.aldrone2122.project.deliverytracking.entities;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Dimensions implements Serializable {

    private Double height;

    private Double width;

    private Double length;

}
