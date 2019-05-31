package it.polito.thermostat.controllermd.resources;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class LeaveResource {
    private Double leaveTemperature;
    private Double leaveBackTemperature;
    private LocalDateTime leaveEnd;
}
