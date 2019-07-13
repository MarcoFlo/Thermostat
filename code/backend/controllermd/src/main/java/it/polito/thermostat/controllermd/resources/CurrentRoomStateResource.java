package it.polito.thermostat.controllermd.resources;

import it.polito.thermostat.controllermd.configuration.SeasonGetter;
import it.polito.thermostat.controllermd.entity.Room;
import it.polito.thermostat.controllermd.entity.WSAL;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurrentRoomStateResource {

    private Boolean isWinter;
    private Boolean isSummer;

    private Boolean isAntiFreeze;
    private Double antiFreezeTemperature;

    private Boolean isLeave;
    private Double leaveTemperature;

    private Boolean isManual;

    public CurrentRoomStateResource(WSAL wsal, Room room) {
        isWinter = wsal.getIsWinter();
        isSummer = wsal.getIsSummer();
        isAntiFreeze = wsal.getIsAntiFreeze();
        antiFreezeTemperature = wsal.getAntiFreezeTemperature();
        isLeave = wsal.getIsLeave();
        leaveTemperature = wsal.getLeaveTemperature();
        isManual = room.getIsManual();
    }

    public CurrentRoomStateResource()
    {
        isWinter = !SeasonGetter.isSummer();
        isSummer = !isWinter;
        isAntiFreeze = false;
        antiFreezeTemperature = 10.0;
        isLeave = false;
        leaveTemperature = 18.0;
        isManual = false;
    }

}
