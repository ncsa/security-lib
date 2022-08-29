package edu.uiuc.ncsa.sas.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:37 AM
 */
public class EncryptionException extends SASException {
    public EncryptionException() {
    }

    public EncryptionException(Throwable cause) {
        super(cause);
    }

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
