package it.polito.thermostat.wifi.entity.program;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.polito.thermostat.wifi.configuration.CustomDateDeserializer;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HourlyProgram {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalTime time;
    private Double temperature;


}
