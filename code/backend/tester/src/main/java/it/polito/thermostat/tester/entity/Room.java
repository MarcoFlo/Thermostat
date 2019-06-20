package it.polito.thermostat.tester.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Room")
public class Room {
    @Id
    private String idRoom;
    private List<String> esp8266List;
    private Boolean isManual;
    private Double desiredTemperature;
}
