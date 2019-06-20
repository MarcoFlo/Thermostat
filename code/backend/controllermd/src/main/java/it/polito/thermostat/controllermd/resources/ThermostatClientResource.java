package it.polito.thermostat.controllermd.resources;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ThermostatClientResource {
    private Double desiredTemperature;
    private Double currentApparentTemperature;
}
