package it.polito.thermostat.controllermd.resources;

import it.polito.thermostat.controllermd.entity.Program;
import it.polito.thermostat.controllermd.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResource {
    private String idRoom;
    private List<String> esp8266List;

    private Program program;

    public RoomResource(Room room, Program program) {
        idRoom = room.getIdRoom();
        esp8266List = room.getEsp8266List();
        this.program = program;
    }
}
