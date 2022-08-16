package org.itentika.edu.spuzakov.mvc.exception;

public class InvalidInputStoException extends StoApplicationException {

    public InvalidInputStoException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputStoException(String message) {
        super(message);
    }
}
