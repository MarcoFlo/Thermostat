package it.polito.thermostat.wifi.resources;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

@Data
public class RoomSettingResource extends ResourceSupport {
    private String idRoom;
    private Double desiredTemperature;

}
