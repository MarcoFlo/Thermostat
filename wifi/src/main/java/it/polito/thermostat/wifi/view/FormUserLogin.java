package it.polito.thermostat.wifi.view;

import it.polito.thermostat.wifi.validator.EmailIsPresent;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;


@Data
public class FormUserLogin {

    @Email
    @Size(min = 7, max = 255)
    @EmailIsPresent(expectedResult = true, message = "Mail not valid")
    private String email;

    @Size(min = 3, max = 64)
    private String pass;

}
