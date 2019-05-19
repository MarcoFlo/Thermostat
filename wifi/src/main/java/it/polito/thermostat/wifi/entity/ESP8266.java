package it.polito.thermostat.wifi.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "esp")
public class ESP8266 {
    @Id
    private String idEsp;
    private String idRoom;
    private Boolean isSensor;
    private Boolean isCooler;
}
