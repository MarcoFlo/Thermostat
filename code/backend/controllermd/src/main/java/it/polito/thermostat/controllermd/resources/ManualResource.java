package it.polito.thermostat.controllermd.resources;

import lombok.Data;

@Data
public class ManualResource {
    private String idRoom;
    private Double desiredTemperature;
}
