package edu.uiuc.ncsa.security.core.exceptions;


/**
 * Thrown when an invalid timestamp (such as from parsing a date) is encountered. Since Parse exceptions
 * in contexts are really runtime exception, it is better to catch them and rethrow this.
 * <p>Created by Jeff Gaynor<br>
 * on Oct 25, 2010 at  10:54:45 AM
 */
public class InvalidTimestampException extends GeneralException {
    public InvalidTimestampException() {
    }

    public InvalidTimestampException(Throwable cause) {
        super(cause);
    }

    public InvalidTimestampException(String message) {
        super(message);
    }

    public InvalidTimestampException(String message, Throwable cause) {
        super(message, cause);
    }
}
