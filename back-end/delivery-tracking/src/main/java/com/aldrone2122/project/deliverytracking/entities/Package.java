package com.aldrone2122.project.deliverytracking.entities;

import java.io.Serializable;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Package implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private Double weight;

    @Embedded
    private Coordinates destination;

    @Embedded
    private Dimensions dimensions;


}
