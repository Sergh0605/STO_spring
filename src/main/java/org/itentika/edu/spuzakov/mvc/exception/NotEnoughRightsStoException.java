package org.itentika.edu.spuzakov.mvc.exception;

public class NotEnoughRightsStoException extends StoApplicationException {

    public NotEnoughRightsStoException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughRightsStoException(String message) {
        super(message);
    }
}
