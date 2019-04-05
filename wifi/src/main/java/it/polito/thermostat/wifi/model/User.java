package it.polito.thermostat.wifi.model;



import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class User {
    private String first;

    private String last;

    private String email;

    private String pass;

    private boolean privacy;

    private Date registrationDate;

}
