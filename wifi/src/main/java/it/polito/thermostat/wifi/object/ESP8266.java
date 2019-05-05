package it.polito.thermostat.wifi.object;

import lombok.Data;

@Data
public class ESP8266 {
    private String id;
    private Boolean isActuator;
    private String idRoom;
    private Double temperature;
    private Double humidity;
}
