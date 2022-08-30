package com.aldrone2122.project.jolydrone.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Tracking {

    @Column(unique = true)
    private String trackingNumber;
    @Id
    @GeneratedValue
    private Long id;

}
