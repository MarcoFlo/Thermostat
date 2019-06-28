package it.polito.thermostat.tester.entity;


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
        private LocalTime time;
        private Double temperature;
    }

    @Data
    public static class DailyProgram {
        Map<String, HourlyProgram> dailyMap;
    }
}

