package edu.uiuc.ncsa.security.core.exceptions;

/**
 * This is thrown by servlets to indicate that an error has occurred (on user input) that
 * requires that the page be redisplayed with an error message.
 * <p>Created by Jeff Gaynor<br>
 * on 8/24/15 at  1:11 PM
 */
public class RetryException extends GeneralException {
    public RetryException() {
    }

    public RetryException(Throwable cause) {
        super(cause);
    }

    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
