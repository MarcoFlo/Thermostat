package it.polito.thermostat.controllermd.entity.program;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.polito.thermostat.controllermd.configuration.CustomDateDeserializer;
import lombok.Data;

import java.util.Date;

@Data
public class HourlyProgram {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date time;
    private Double temperature;


}
