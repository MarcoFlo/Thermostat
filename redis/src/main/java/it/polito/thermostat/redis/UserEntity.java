package it.polito.thermostat.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@RedisHash("User")
public class UserEntity implements Serializable {

    @Id
    private String name;
private Esame esame;
    private LocalDateTime localDateTime;

}