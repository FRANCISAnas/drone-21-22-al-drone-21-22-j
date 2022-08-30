package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Package {

    @Id
    @GeneratedValue
    private Long id;

    private Double weight;

    @Embedded
    private Coordinates destination;

    @Embedded
    private Dimensions dimensions;

}
