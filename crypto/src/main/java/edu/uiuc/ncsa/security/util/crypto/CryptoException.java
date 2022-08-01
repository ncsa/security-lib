package edu.uiuc.ncsa.security.util.crypto;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/22/22 at  10:17 AM
 */
public class CryptoException extends RuntimeException{
    public CryptoException() {
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(Throwable cause) {
        super(cause);
    }

    public CryptoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
