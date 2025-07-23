package edu.uiuc.ncsa.security.core.inheritance;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  8:19 AM
 */
public class CyclicalError extends MyConfigurationException {
    public CyclicalError() {
    }

    public CyclicalError(Throwable cause) {
        super(cause);
    }

    public CyclicalError(String message) {
        super(message);
    }

    public CyclicalError(String message, Throwable cause) {
        super(message, cause);
    }
}
