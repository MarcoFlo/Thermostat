package it.polito.thermostat.wifi.resources;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalTime;

@Data
public class LeaveResource extends ResourceSupport {
    private LocalTime leaveTime;
    private Double desiredTemperature;
}
