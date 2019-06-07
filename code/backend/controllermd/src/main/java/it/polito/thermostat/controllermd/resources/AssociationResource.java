package it.polito.thermostat.controllermd.resources;

import lombok.Data;

@Data
public class AssociationResource {
    private String idEsp;
    private String idRoom;
    private Boolean addBool;
}
