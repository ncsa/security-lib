package edu.uiuc.ncsa.security.core.exceptions;

public class ExpiredKeyException extends GeneralException{
    public ExpiredKeyException() {
    }

    public ExpiredKeyException(Throwable cause) {
        super(cause);
    }

    public ExpiredKeyException(String message) {
        super(message);
    }

    public ExpiredKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
