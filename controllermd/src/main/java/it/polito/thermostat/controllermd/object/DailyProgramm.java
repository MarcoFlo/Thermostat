package it.polito.thermostat.controllermd.object;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import it.polito.thermostat.controllermd.configuration.CustomDateDeserializer;
import lombok.Data;

import java.util.Date;

/**
 * wake
 * leave
 * return
 * sleep
 */
@Data
public class DailyProgramm {

    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date wakeTime;
    private Integer wakeTemperature;


    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date leaveTime;
    private Integer leaveTemperature;


    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date backTime;
    private Integer backTemperature;


    @JsonDeserialize(using = CustomDateDeserializer.class)
    private Date sleepTime;
    private Integer sleepTemperature;

}