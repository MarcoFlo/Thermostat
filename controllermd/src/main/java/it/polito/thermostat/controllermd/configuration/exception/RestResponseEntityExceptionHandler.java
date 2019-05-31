package it.polito.thermostat.controllermd.configuration.exception;


import it.polito.thermostat.controllermd.configuration.exception.ProgramNotExistException;
import it.polito.thermostat.controllermd.configuration.exception.RoomNotExistException;
import it.polito.thermostat.controllermd.object.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.time.DateTimeException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            NullPointerException.class,
            DateTimeException.class,
            RoomNotExistException.class,
            ProgramNotExistException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        ErrorDTO e = ErrorDTO.builder()
                .exception(ex.getClass().getSimpleName())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .errorMessage(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .build();
        logger.error(e.toString());
        return handleExceptionInternal(ex, e, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}