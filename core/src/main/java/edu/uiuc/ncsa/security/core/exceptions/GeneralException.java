package edu.uiuc.ncsa.security.core.exceptions;

/**
 * Top-level exception. This extends RuntimeException so subclasses don't have to be
 * explicitly declared or caught.
 * <p>Created by Jeff Gaynor<br>
 * on Jan 25, 2011 at  2:19:45 PM
 */
public class GeneralException extends RuntimeException {
    public GeneralException() {
        super();
    }

    public GeneralException(Throwable cause) {
        super(cause);
    }

    public GeneralException(String message) {
        super(message);
    }

    public GeneralException(String message, Throwable cause) {
        super(message, cause);
    }
}
