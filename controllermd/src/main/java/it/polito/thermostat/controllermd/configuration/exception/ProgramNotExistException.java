package it.polito.thermostat.controllermd.configuration.exception;


public class ProgramNotExistException extends RuntimeException {
    /**
     * Constructs a {@code PrenotazioneNotFoundException} with no detail message.
     */
    public ProgramNotExistException() {
        super();
    }

    /**
     * Constructs a {@code PrenotazioneNotFoundException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public ProgramNotExistException(String s) {
        super(s);
    }
}