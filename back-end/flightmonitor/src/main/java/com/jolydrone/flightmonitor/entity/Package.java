package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

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
