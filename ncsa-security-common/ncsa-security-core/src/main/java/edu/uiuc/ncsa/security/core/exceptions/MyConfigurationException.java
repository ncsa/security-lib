package edu.uiuc.ncsa.security.core.exceptions;


/**
 * Exception thrown when there is a problem reading a configuration or interpreting the configuration.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 5, 2010 at  11:45:13 AM
 */
public class MyConfigurationException extends GeneralException {
    public MyConfigurationException() {
    }

    public MyConfigurationException(Throwable cause) {
        super(cause);
    }

    public MyConfigurationException(String message) {
        super(message);
    }

    public MyConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
