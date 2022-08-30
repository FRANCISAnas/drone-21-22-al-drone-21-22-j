package com.aldrone2122.project.drone;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
    private String name;


    @Override
    public String toString() {
        return "Person [name=" + name + "]";
    }

}
