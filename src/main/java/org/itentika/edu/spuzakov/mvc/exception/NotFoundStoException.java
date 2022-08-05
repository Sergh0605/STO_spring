package org.itentika.edu.spuzakov.mvc.exception;

public class NotFoundStoException extends StoApplicationException {

    public NotFoundStoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundStoException(String message) {
        super(message);
    }
}
