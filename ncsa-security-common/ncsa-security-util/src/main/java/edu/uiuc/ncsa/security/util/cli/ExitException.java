package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/30/13 at  10:46 AM
 */
public class ExitException extends GeneralException {
    public ExitException() {
    }

    public ExitException(Throwable cause) {
        super(cause);
    }

    public ExitException(String message) {
        super(message);
    }

    public ExitException(String message, Throwable cause) {
        super(message, cause);
    }
}
