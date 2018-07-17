package edu.uiuc.ncsa.security.util.functor.parser;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/13/18 at  2:04 PM
 */
public class ParserError extends GeneralException {
    public ParserError() {
    }

    public ParserError(Throwable cause) {
        super(cause);
    }

    public ParserError(String message) {
        super(message);
    }

    public ParserError(String message, Throwable cause) {
        super(message, cause);
    }
}
