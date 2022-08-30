package com.jolydrone.stationmanager.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Terminal {

    @Id
    @GeneratedValue
    private Long id;

    private int idForStation;

    private TerminalStatus status;//TODO add an attribute to give this class an actual identity example a name or a number

    @Override
    public String toString() {
        return "Terminal{" +
                "id=" + id +
                ", idForStation=" + idForStation +
                ", status=" + status +
                '}';
    }
}
