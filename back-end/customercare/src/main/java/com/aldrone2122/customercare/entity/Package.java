package com.aldrone2122.customercare.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Package {

    @Id
    @GeneratedValue
    private Long id;

    private Double weight;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "DESTINATION_X"))
    @AttributeOverride(name = "y", column = @Column(name = "DESTINATION_Y"))
    private Coordinates destination;


    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "ORIGIN_X"))
    @AttributeOverride(name = "y", column = @Column(name = "ORIGIN_Y"))
    private Coordinates origin;

    @Embedded
    private Dimensions dimensions;

    private BigDecimal cost;

    @Override
    public String toString() {
        return "Package [cost=" + cost + ", destination=" + destination + ", dimensions=" + dimensions + ", id=" + id
                + ", origin=" + origin + ", weight=" + weight + "]";
    }


}
