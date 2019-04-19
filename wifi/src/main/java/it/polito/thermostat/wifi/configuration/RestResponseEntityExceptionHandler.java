package it.polito.thermostat.wifi.configuration;
/**
 * Gestione delle exception stile spring
 * https://www.baeldung.com/exception-handling-for-rest-with-spring
 */

import it.polito.thermostat.wifi.DTO.ErrorDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.DateTimeException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value
            = {IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class, InterruptedException.class})
    protected ResponseEntity<Object> handleConflict(
            RuntimeException ex, WebRequest request) {
        ErrorDTO e = ErrorDTO.builder().errorMessage(ex.getMessage()).build();
        return handleExceptionInternal(ex, e, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}