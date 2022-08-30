package com.jolydrone.flightmonitor.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.PrePersist;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class Terminal implements Serializable {

    @GeneratedValue
    private Long id;
    private int idForStation;
    private TerminalStatus status;//TODO add an attribute to give this class an actual identity example a name or a number

    @PrePersist
    void printPersistContent() {
        System.out.println("before Terminal");
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "id=" + id +
                ", idForStation=" + idForStation +
                ", status=" + status +
                '}';
    }
}
