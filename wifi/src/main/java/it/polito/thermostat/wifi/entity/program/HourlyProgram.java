package it.polito.thermostat.wifi.entity.program;

import lombok.Data;

import java.time.LocalTime;

@Data
public class HourlyProgram {
    //deserializzer missing because done in controllermd
    private LocalTime time;
    private Double temperature;
}
