package it.polito.thermostat.wifi.object;

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

    private Date wakeTime;
    private Integer wakeTemperature;


    private Date leaveTime;
    private Integer leaveTemperature;


    private Date backTime;
    private Integer backTemperature;


    private Date sleepTime;
    private Integer sleepTemperature;

}