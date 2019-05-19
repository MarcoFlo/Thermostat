package it.polito.thermostat.wifi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@Document(collection = "wsal")
public class WSAL {
    private Boolean isWinter;
    private Boolean isSummer;

    private Boolean isAntiFreeze;
    private Double antiFreezeTemperature;

    private Boolean isLeave;
    private Double leaveTemperature;
    private Double leaveBackTemperature;
    private Date leaveEnd;

    private Date creationDate;

}
