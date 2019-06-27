package it.polito.thermostat.controllermd.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.polito.thermostat.controllermd.configuration.CustomDateDeserializer;
import it.polito.thermostat.controllermd.configuration.CustomTimeSerializer;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
@RedisHash("weeklyPrograms")
public class Program {
    @Id
    private String idProgram;
    private List<DailyProgram> weeklyList;


    @Data
    public static class HourlyProgram {
        @JsonDeserialize(using = CustomDateDeserializer.class)
        @JsonSerialize(using = CustomTimeSerializer.class)
        private LocalTime time;
        private Double temperature;
    }

    @Data
    public static class DailyProgram {
        Map<String, HourlyProgram> dailyMap;
    }
}

