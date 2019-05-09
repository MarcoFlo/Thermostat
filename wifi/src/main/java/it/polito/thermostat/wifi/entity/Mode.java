package it.polito.thermostat.wifi.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;

@Data
@Document(collection = "config")
public class Mode {
    private Boolean isSummer;
    private Boolean isAntifreeze;
    private LocalTime leaveTime;
    private Double desiredTemperature;
}
