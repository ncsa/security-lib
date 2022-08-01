package edu.uiuc.ncsa.security.core.exceptions;

import edu.uiuc.ncsa.security.core.Identifier;

/**
 * Thrown when an attempt has been made to retrieve a non-existent client.
 * <p>Created by Jeff Gaynor<br>
 * on 6/27/12 at  12:24 PM
 */
public class UnknownClientException extends GeneralException {
    public UnknownClientException() {
    }

    public UnknownClientException(String message, Identifier identifier) {
        super(message);
        this.identifier = identifier;
    }

    public UnknownClientException(Throwable cause) {
        super(cause);
    }

    public UnknownClientException(String message) {
        super(message);
    }

    public UnknownClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    Identifier identifier;

}
