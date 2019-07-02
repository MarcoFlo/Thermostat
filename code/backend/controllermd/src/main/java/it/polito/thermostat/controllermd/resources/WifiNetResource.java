package it.polito.thermostat.controllermd.resources;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class WifiNetResource {
    private String essid;
    private String netPassword;
    private Boolean isKnown;

    public WifiNetResource(String essid, boolean isKnown) {
        this.essid = essid;
        this.isKnown = isKnown;
        netPassword = "";
    }
}
