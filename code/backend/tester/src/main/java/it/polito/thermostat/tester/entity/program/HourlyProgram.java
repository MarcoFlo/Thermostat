package it.polito.thermostat.tester.entity.program;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.polito.thermostat.tester.configuration.CustomDateDeserializer;
import it.polito.thermostat.tester.configuration.CustomTimeSerializer;
import lombok.Data;

import java.time.LocalTime;

@Data
public class HourlyProgram {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    @JsonSerialize(using = CustomTimeSerializer.class)
    private LocalTime time;
    private Double temperature;


}
