package edu.uiuc.ncsa.qdl.evaluate;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:22 AM
 */
public interface EvaluatorInterface {
    // only guaranteed value for all of them.
    int UNKNOWN_VALUE = -1;

    int getType(String name);



}
