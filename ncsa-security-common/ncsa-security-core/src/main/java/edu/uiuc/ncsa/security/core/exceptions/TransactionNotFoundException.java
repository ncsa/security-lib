package edu.uiuc.ncsa.security.core.exceptions;


/**
 * Thrown when an attempt to retrieve a supposedly pending transaction fails.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 16, 2010 at  3:50:58 PM
 */
public class TransactionNotFoundException extends GeneralException {
    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(Throwable cause) {
        super(cause);
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
