package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  6:51 AM
 */
public class UndefinedFunctionException extends QDLExceptionWithTrace {
    public UndefinedFunctionException(Statement statement) {
        super(statement);
    }

    public UndefinedFunctionException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public UndefinedFunctionException(String message, Statement statement) {
        super(message, statement);
    }

    public UndefinedFunctionException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }
}
