package it.polito.thermostat.controllermd.configuration.exception;

public class RoomNotExistException extends RuntimeException {
    /**
     * Constructs a {@code PrenotazioneNotFoundException} with no detail message.
     */
    public RoomNotExistException() {
        super();
    }

    /**
     * Constructs a {@code PrenotazioneNotFoundException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public RoomNotExistException(String s) {
        super(s);
    }
}
