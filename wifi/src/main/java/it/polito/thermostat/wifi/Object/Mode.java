package it.polito.thermostat.wifi.Object;

import lombok.Data;
import java.util.Map;

@Data
public class Mode {
    private String nome;
    private Map<Integer,DailyProgram> programs;
}
