package edu.uiuc.ncsa.qdl.statements;

import java.io.Serializable;
import java.util.List;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/27/20 at  9:47 AM
 */
public interface Documentable extends Serializable {
    /**
     * List first lines of function documentation given a regular expression.
     * @param regex
     * @return
     */
    TreeSet<String> listFunctions(String regex);

    /**
     * return all the function documentation this module knows.
     * @return
     */
    List<String> listAllDocs();

    /**
     * Get the complete documentation for a specific function.
     * @param fName
     * @param argCount
     * @return
     */
    List<String> getDocumentation(String fName, int argCount);
}
