package it.polito.thermostat.wifi.object;

import lombok.Data;

@Data
public class ESP8266 {
    private String id;
    private String idRoom;
    private Double temperature;
    private Double humidity;
    private Boolean isSensor;
    private Boolean isHeater;
}
