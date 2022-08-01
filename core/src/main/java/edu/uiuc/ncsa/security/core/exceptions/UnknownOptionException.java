package edu.uiuc.ncsa.security.core.exceptions;


/**
 * Thrown by command line clients when the user attempts to invoke a command
 * (i.e. a menu option) that is not known.
 * <p>Created by Jeff Gaynor<br>
 * on Jun 25, 2010 at  4:38:01 PM
 */
public class UnknownOptionException extends GeneralException {

    public UnknownOptionException() {
    }

    public UnknownOptionException(Throwable cause) {
        super(cause);
    }

    public UnknownOptionException(String message) {
        super(message);
    }

    public UnknownOptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
