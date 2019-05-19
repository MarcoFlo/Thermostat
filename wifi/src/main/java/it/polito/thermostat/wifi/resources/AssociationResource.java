package it.polito.thermostat.wifi.resources;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

@Data
public class AssociationResource extends ResourceSupport {
    private String idEsp;
    private String idRoom;
    private Boolean addBool;
}
