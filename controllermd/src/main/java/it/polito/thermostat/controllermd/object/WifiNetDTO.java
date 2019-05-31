package it.polito.thermostat.controllermd.object;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WifiNetDTO {
    private String essid;
    private Boolean isKnown;
}
