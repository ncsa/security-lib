package edu.uiuc.ncsa.qdl.evaluate;

import java.io.Serializable;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:22 AM
 */
public interface EvaluatorInterface extends Serializable {
    // only guaranteed value for all of them.
    int UNKNOWN_VALUE = -1;

    int getType(String name);

     TreeSet<String> listFunctions(boolean listFQ);

}
