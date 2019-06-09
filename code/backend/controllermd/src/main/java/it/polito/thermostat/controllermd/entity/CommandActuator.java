package it.polito.thermostat.controllermd.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class CommandActuator {
    private String idEsp;
    private Boolean commandBoolean;
    private String timestamp;

    public CommandActuator(String idEsp, Boolean commandBoolean)
    {
        this.idEsp = idEsp;
        this.commandBoolean = commandBoolean;
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
    }
}
