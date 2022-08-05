package org.itentika.edu.spuzakov.mvc.exception;

public class StoApplicationException extends RuntimeException {
    public StoApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoApplicationException(String message) {
        super(message);
    }
}
