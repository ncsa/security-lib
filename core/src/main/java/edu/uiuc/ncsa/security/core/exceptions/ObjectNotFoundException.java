package edu.uiuc.ncsa.security.core.exceptions;

public class ObjectNotFoundException extends GeneralException{
    public ObjectNotFoundException() {
    }

    public ObjectNotFoundException(Throwable cause) {
        super(cause);
    }

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
