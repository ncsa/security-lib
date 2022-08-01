package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/25/13 at  4:28 PM
 */
public class FilePermissionsException extends GeneralException {
    public FilePermissionsException() {
    }

    public FilePermissionsException(Throwable cause) {
        super(cause);
    }

    public FilePermissionsException(String message) {
        super(message);
    }

    public FilePermissionsException(String message, Throwable cause) {
        super(message, cause);
    }
}
