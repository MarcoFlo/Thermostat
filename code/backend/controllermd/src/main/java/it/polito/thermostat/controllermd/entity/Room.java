package it.polito.thermostat.controllermd.entity;

import it.polito.thermostat.controllermd.resources.RoomResource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Room")
public class Room {
    @Id
    private String idRoom;
    private List<String> esp8266List;
    private Boolean isManual;
    private Double desiredTemperature;

    public Room(RoomResource roomResource) {
        this.idRoom = roomResource.getIdRoom();
        this.esp8266List = roomResource.getEsp8266List();
        isManual = false;
        desiredTemperature = -1.0;
    }
}
