package it.polito.thermostat.controllermd.object;

import lombok.Data;

@Data
public class SensorData {
    private String idEsp;
    private Double temperature;
    private Double humidity;
    private Double apparentTemperature;

    public SensorData(String idEsp, Double temperature, Double humidity) {
        this.idEsp = idEsp;
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
