package it.polito.thermostat.tester.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("esp")
public class ESP8266 {
    @Id
    private String idEsp;
    private String idRoom;
    private Boolean isSensor;
    private Boolean isCooler;
}
