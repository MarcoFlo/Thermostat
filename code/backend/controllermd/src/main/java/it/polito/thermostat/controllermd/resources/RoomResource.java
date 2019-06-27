package it.polito.thermostat.controllermd.resources;

import it.polito.thermostat.controllermd.entity.Program;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomResource {
    private String idRoom;
    private List<String> esp8266List;

    private Program program;
}
