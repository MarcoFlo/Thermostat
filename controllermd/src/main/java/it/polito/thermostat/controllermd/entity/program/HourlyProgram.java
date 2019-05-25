package it.polito.thermostat.controllermd.entity.program;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.polito.thermostat.controllermd.configuration.CustomDateDeserializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
public class HourlyProgram {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private LocalTime time;
    private Double temperature;


}
