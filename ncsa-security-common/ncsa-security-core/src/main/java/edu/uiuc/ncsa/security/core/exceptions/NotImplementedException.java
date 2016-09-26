package edu.uiuc.ncsa.security.core.exceptions;


/**
 * For methods that have not been implemented.
 * <p>Created by Jeff Gaynor<br>
 * on Oct 31, 2010 at  6:44:13 PM
 */
public class NotImplementedException extends GeneralException {
    public NotImplementedException() {
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }
}
