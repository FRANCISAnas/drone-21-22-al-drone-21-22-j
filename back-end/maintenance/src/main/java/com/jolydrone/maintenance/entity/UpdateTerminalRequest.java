package com.jolydrone.maintenance.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTerminalRequest {

    private String stationName;

    private int idForStation;

    private String status;

}
