package edu.uiuc.ncsa.security.util.cli;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/17/13 at  11:12 AM
 */
public class CommandNotFoundException extends GeneralException {
    public CommandNotFoundException() {
    }

    public CommandNotFoundException(Throwable cause) {
        super(cause);
    }

    public CommandNotFoundException(String message) {
        super(message);
    }

    public CommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
