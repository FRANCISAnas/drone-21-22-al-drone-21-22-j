package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class Dimensions implements Serializable {

    private Double height;

    private Double width;

    private Double length;

}
