package it.polito.thermostat.controllermd.configuration.exception;

public class WifiCredentialsException extends RuntimeException {
    /**
     * Constructs a {@code PrenotazioneNotFoundException} with no detail message.
     */
    public WifiCredentialsException() {
        super();
    }

    /**
     * Constructs a {@code PrenotazioneNotFoundException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public WifiCredentialsException(String s) {
        super(s);
    }
}