package edu.uiuc.ncsa.qdl.exceptions;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  1:14 PM
 */
public class ReturnException extends QDLException {
    
    public Object result;
    public int resultType;

    @Override
    public String toString() {
        return "ReturnException{" +
                "result=" + result +
                ", resultType=" + resultType +
                '}';
    }
}
