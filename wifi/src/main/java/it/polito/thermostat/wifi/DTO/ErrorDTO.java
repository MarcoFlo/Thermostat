package it.polito.thermostat.wifi.DTO;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDTO {
    private String errorMessage;
}
