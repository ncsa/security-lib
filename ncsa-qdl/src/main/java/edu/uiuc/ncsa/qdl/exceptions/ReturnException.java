package edu.uiuc.ncsa.qdl.exceptions;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  1:14 PM
 */
public class ReturnException extends GeneralException {
    
    public Object result;
    public int resultType;
}
