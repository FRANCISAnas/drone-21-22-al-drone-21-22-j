package com.aldrone2122j.deliveryplanner.entities;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Terminal {
    private Long id;
    private int idForStation;
    private TerminalStatus status;

    @Override
    public String toString() {
        return "Terminal{" +
                "id=" + id +
                ", idForStation=" + idForStation +
                ", status=" + status +
                '}';
    }
}
