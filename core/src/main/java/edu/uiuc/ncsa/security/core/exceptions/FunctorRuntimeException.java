package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/19 at  9:25 AM
 */
public class FunctorRuntimeException extends GeneralException {
    public FunctorRuntimeException() {
    }

    public FunctorRuntimeException(Throwable cause) {
        super(cause);
    }

    public FunctorRuntimeException(String message) {
        super(message);
    }

    public FunctorRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
