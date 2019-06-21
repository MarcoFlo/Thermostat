package it.polito.thermostat.tester.entity.program;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * wake
 * leave
 * return
 * sleep
 */
@Data
public class DailyProgram {

    Map<String, HourlyProgram> dailyMap;

    public DailyProgram()
    {
        dailyMap = new HashMap<>();
    }

//    @JsonDeserialize(using = CustomDateDeserializer.class)
//    private Date wakeTime;
//    private Integer wakeTemperature;
//
//
//    @JsonDeserialize(using = CustomDateDeserializer.class)
//    private Date leaveTime;
//    private Integer leaveTemperature;
//
//
//    @JsonDeserialize(using = CustomDateDeserializer.class)
//    private Date backTime;
//    private Integer backTemperature;
//
//
//    @JsonDeserialize(using = CustomDateDeserializer.class)
//    private Date sleepTime;
//    private Integer sleepTemperature;

}