package it.polito.thermostat.wifi.resources;

import lombok.Data;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDateTime;


@Data
public class LeaveResource extends ResourceSupport {
    private Double leaveTemperature;
    private Double leaveBackTemperature;
    private LocalDateTime leaveEnd;
}
