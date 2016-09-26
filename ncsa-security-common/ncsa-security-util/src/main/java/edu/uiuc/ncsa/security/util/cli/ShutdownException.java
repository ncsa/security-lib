package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * Thrown by the command line interpreter to signal a shutdown should happen.
 * <p>Created by Jeff Gaynor<br>
 * on 5/21/13 at  3:03 PM
 */
public class ShutdownException extends GeneralException {
    public ShutdownException() {
    }

    public ShutdownException(Throwable cause) {
        super(cause);
    }

    public ShutdownException(String message) {
        super(message);
    }

    public ShutdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
