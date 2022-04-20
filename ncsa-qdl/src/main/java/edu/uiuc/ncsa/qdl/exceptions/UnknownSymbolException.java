package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/25/20 at  6:47 AM
 */
public class UnknownSymbolException extends QDLExceptionWithTrace {
    public UnknownSymbolException(Statement statement) {
        super(statement);
    }

    public UnknownSymbolException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public UnknownSymbolException(String message, Statement statement) {
        super(message, statement);
    }

    public UnknownSymbolException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }

}
