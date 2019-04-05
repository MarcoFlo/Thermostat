package it.polito.thermostat.wifi.view;


import it.polito.thermostat.wifi.validator.EmailIsPresent;
import it.polito.thermostat.wifi.validator.FieldsValueMatch;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;


@FieldsValueMatch(
        field = "pass",
        fieldMatch = "pass1",
        message = "Passwords do not match!"
)
@Data
public class FormUserRegistration {

    @Size(min = 3, max = 50)
    private String first;

    @Size(min = 3, max = 50)
    private String last;

    @Email
    @Size(min = 7, max = 25)
    @EmailIsPresent(expectedResult = false, message = "Mail not available")
    private String email;

    @Size(min = 3, max = 64)
    private String pass;

    @Size(min = 3, max = 64)
    private String pass1;

    @AssertTrue
    private boolean privacy;


}