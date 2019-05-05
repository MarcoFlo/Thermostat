package it.polito.thermostat.wifi.object;

import lombok.Data;
import java.util.Map;

@Data
public class Programm {
    private String idProgramm;
    private Map<Integer,DailyProgram> programs;
}
