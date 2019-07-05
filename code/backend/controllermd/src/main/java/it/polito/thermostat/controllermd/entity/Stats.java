package it.polito.thermostat.controllermd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@RedisHash("stats")
@AllArgsConstructor
public class Stats {
    @Id
    private String keyDayRoom;

    private LocalDate day;
    private String idRoom;

    private Long amount;
    private LocalTime commandTime;
    private Boolean on;

}
