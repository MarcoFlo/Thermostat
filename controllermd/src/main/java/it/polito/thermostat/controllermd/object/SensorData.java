package it.polito.thermostat.controllermd.object;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SensorData {
    private String idEsp;
    private Double temperature;
    private Double humidity;
}
