package it.polito.thermostat.wifi.entity;

import it.polito.thermostat.wifi.object.ESP8266;
import it.polito.thermostat.wifi.object.Programm;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "rooms")
public class Room {
    private String idRoom;
    private List<ESP8266> esp8266List;
    private Boolean isManual;
    private Double desiredTemperature;

}
