package com.aldrone2122.project.jolydrone.gateway.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Terminal {

    private Long id;
    private int idForStation;
    private TerminalStatus status;//TODO add an attribute to give this class an actual identity example a name or a number


}
