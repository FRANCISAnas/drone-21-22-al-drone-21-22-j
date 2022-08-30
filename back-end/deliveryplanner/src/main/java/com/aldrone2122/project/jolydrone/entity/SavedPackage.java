package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity

@Getter
@Setter
public class SavedPackage {


    @Id
    @GeneratedValue
    private Long id;

    private Double weight;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "ORIGIN_X"))
    @AttributeOverride(name = "y", column = @Column(name = "ORIGIN_Y"))
    private Coordinates origin;

    @Embedded
    @AttributeOverride(name = "x", column = @Column(name = "DESTINATION_X"))
    @AttributeOverride(name = "y", column = @Column(name = "DESTINATION_Y"))
    private Coordinates destination;

    @Embedded
    private Dimensions dimensions;

    private Long packageId;

    private LocalDateTime timeStamp;


    @Override
    public String toString() {
        return "SavedPackage{" +
                "id=" + id +
                ", weight=" + weight +
                ", origin=" + origin +
                ", destination=" + destination +
                ", dimensions=" + dimensions +
                ", packageId=" + packageId +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
