package it.polito.thermostat.controllermd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RedisHash("wsal")
public class WSAL {
    private Boolean isWinter;
    private Boolean isSummer;

    private Boolean isAntiFreeze;
    private Double antiFreezeTemperature;

    private Boolean isLeave;
    private Double leaveTemperature;
    private Double leaveBackTemperature;
    private LocalDateTime leaveEnd;

    private LocalDateTime creationDate;

}
