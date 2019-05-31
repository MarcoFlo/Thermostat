package it.polito.thermostat.controllermd.resources;
import lombok.Data;

import java.util.Map;

@Data
public class JsonConfigurationResource {
    Map<String,String> mapEspRoom;
}
