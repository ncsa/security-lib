package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.qdl.statements.Statement;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  3:39 PM
 */
public class ImportException extends QDLExceptionWithTrace {
    public ImportException(Statement statement) {
        super(statement);
    }

    public ImportException(Throwable cause, Statement statement) {
        super(cause, statement);
    }

    public ImportException(String message, Statement statement) {
        super(message, statement);
    }

    public ImportException(String message, Throwable cause, Statement statement) {
        super(message, cause, statement);
    }
}
