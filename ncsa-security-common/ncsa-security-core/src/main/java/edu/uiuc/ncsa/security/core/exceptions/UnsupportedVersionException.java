package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 9/25/18 at  8:31 AM
 */
public class UnsupportedVersionException extends GeneralException {
    public UnsupportedVersionException() {
    }

    public UnsupportedVersionException(Throwable cause) {
        super(cause);
    }

    public UnsupportedVersionException(String message) {
        super(message);
    }

    public UnsupportedVersionException(String message, Throwable cause) {
        super(message, cause);
    }
}
