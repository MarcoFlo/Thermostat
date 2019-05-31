package it.polito.thermostat.controllermd.resources;

import lombok.Data;

@Data
public class WifiNetResource {
    private String essid;
    private String netPassword;
}
