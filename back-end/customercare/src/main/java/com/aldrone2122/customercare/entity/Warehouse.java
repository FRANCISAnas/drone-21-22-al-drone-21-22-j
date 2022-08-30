package com.aldrone2122.customercare.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Warehouse {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    private Coordinates location;//TODO add an attribute like a name to give this entity more identity

}
