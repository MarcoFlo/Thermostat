package it.polito.thermostat.wifi.entity.program;

import lombok.Data;

import java.util.Date;

@Data
public class HourlyProgram {
    //deserializzer missing because done in controllermd
    private Date time;
    private Double temperature;
}
