package it.polito.thermostat.controllermd.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@RedisHash("sensordatalogging")
public class SensorDataLogging {
    @Id
    private LocalDateTime dateTime;
    private String idEsp;
    private Boolean isOn;
    private Double desiredTemperature;
    private Double actualTemperature;
}
