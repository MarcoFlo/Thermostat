package it.polito.thermostat.controllermd.configuration.exception;

public class StatNotExistException extends RuntimeException{
    public StatNotExistException() {
        super();
    }
    public StatNotExistException(String s) {
        super(s);
    }

}
