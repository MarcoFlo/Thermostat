package it.polito.thermostat.controllermd.resources;

import lombok.Data;

@Data
public class RoomManualResource  {
    private String idRoom;
    private Double desiredTemperature;
}
