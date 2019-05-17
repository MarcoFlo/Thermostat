package it.polito.thermostat.controllermd.entity;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
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
    private Double leaveDesiredTemperature;
    private Date leaveEnd;

    private Date creationDate;

}
