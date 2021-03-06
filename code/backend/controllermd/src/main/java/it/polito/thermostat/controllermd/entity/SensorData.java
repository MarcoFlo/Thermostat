package it.polito.thermostat.controllermd.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@RedisHash("sensordata")
public class SensorData {
    @Id
    private String idEsp;
    private String timestamp;
    private Double temperature;
    private Double humidity;
    private Double apparentTemperature;

    public SensorData(String idEsp, Double temperature, Double humidity) {
        this.idEsp = idEsp;
        this.timestamp = LocalDateTime.now().toString();
        this.temperature = temperature;
        this.humidity = humidity;
        apparentTemperature = getApparentTemperature(temperature,humidity);
    }

    /**
     * Robert Steadman formula
     * https://www.vcalc.com/wiki/rklarsen/Australian+Apparent+Temperature+%28AT%29
     * @return
     */
    private Double getApparentTemperature(Double temperature, Double humidity)
    {
        Double r = humidity / 100 * 6.105 * Math.exp((17.27 * temperature) / (237.7 + temperature));
        return temperature + 0.33 * r - 4;
    }
}
