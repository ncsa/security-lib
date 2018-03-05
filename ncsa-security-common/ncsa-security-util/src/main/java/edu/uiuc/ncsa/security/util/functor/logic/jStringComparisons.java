package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * Top level class used by all comparison and string search functions.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  11:40 AM
 */
public abstract class jStringComparisons extends JFunctorImpl {
    protected jStringComparisons(String name) {
        super(name);
    }

    /**
     * Takes a Java object and if it's a String, returns that otherwise if it's a functor it processed it
     * @param obj
     * @return
     */
    protected String processArg(Object obj) {
        String needle;
        if(obj instanceof String){
             needle = (String) obj;
        } else if(obj instanceof JFunctorImpl){
            JFunctorImpl ff = (JFunctorImpl) obj;
            ff.execute();
            needle = ff.getStringResult();
        } else{
            throw new IllegalArgumentException("Error: unknown argument \"" + obj + "\"");
        }
        return needle;
    }
}
