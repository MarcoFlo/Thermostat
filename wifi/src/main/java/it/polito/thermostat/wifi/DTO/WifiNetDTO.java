package it.polito.thermostat.wifi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WifiNetDTO {
    private String essid;
    private Boolean isKnown;
}
