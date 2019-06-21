package it.polito.thermostat.controllermd.entity.program;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.polito.thermostat.controllermd.configuration.CustomDateDeserializer;
import it.polito.thermostat.controllermd.configuration.CustomTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Data
public class HourlyProgram {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomTimeSerializer.class)
    private LocalTime time;
    private Double temperature;


}
