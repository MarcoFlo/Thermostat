package it.polito.thermostat.controllermd.entity.program;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@RedisHash("weeklyPrograms")
public class Program {

    @Id
    private String idProgram;
    private Map<Integer, DailyProgram> weeklyMap;

}
