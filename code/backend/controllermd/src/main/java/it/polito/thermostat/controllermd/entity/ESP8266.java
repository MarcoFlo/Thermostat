package it.polito.thermostat.controllermd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("esp")
@AllArgsConstructor
@NoArgsConstructor
public class ESP8266 {
    @Id
    private String idEsp;
    private String idRoom;
    private Boolean isSensor;
    private Boolean isCooler;
}
