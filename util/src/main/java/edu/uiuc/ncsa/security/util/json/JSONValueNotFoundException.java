package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/9/20 at  9:30 AM
 */
public class JSONValueNotFoundException extends GeneralException {
    public JSONValueNotFoundException() {
    }

    public JSONValueNotFoundException(Throwable cause) {
        super(cause);
    }

    public JSONValueNotFoundException(String message) {
        super(message);
    }

    public JSONValueNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
