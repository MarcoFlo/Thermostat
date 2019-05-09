package it.polito.thermostat.controllermd.object;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "rooms")
public class Room {
    @Id
    private String idRoom;
    private List<ESP8266> esp8266List;
    private Boolean isManual;
    private Double desiredTemperature;
    private Programm programm;

}
