package edu.uiuc.ncsa.security.core.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/8/14 at  10:53 AM
 */
public class InvalidCertRequestException extends GeneralException {
    public InvalidCertRequestException() {
    }

    public InvalidCertRequestException(Throwable cause) {
        super(cause);
    }

    public InvalidCertRequestException(String message) {
        super(message);
    }

    public InvalidCertRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
