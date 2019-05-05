package it.polito.thermostat.wifi.resources;
import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.util.Map;

@Data
public class JsonConfigurationResource extends ResourceSupport{
    Map<String,String> mapEspRoom;
}
