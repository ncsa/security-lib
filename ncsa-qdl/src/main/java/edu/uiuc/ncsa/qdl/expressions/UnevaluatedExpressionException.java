package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:19 PM
 */
public class UnevaluatedExpressionException extends GeneralException {
    public UnevaluatedExpressionException() {
    }

    public UnevaluatedExpressionException(Throwable cause) {
        super(cause);
    }

    public UnevaluatedExpressionException(String message) {
        super(message);
    }

    public UnevaluatedExpressionException(String message, Throwable cause) {
        super(message, cause);
    }
}
