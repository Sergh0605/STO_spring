package org.itentika.edu.spuzakov.mvc.exception;

public class ConversionStoException extends StoApplicationException {

    public ConversionStoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConversionStoException(String message) {
        super(message);
    }
}
