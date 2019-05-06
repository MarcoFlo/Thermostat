package it.polito.thermostat.wifi.object;

import lombok.Data;

import java.time.LocalTime;
import java.util.Map;

@Data
public class Programm {
    private String idProgramm;
    private Map<Integer,DailyProgramm> programs;



}
