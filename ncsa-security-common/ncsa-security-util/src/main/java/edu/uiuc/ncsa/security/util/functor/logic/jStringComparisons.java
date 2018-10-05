package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * Top level class used by all comparison and string search functions.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  11:40 AM
 */
public abstract class jStringComparisons extends JFunctorImpl {
    protected jStringComparisons(FunctorTypeImpl type) {
        super(type);
    }

    /**
     * Takes a Java object and if it's a String, returns that otherwise if it's a functor it processed it
     * @param obj
     * @return
     */
    protected String processArg(Object obj) {
        String needle = null;
        boolean isString = false;
        if(obj == null){
            needle = "";
            isString = true;
        }
        if(obj instanceof String){
             needle = (String) obj;
            isString = true;
        }
        boolean isFunctor = false;
        if(obj instanceof JFunctorImpl){
            JFunctorImpl ff = (JFunctorImpl) obj;
            ff.execute();
            needle = ff.getStringResult();
            isFunctor = true;
        }

        if(!isString && !isFunctor){
            needle = String.valueOf(obj); // um, whatever it is, return it as a string...
        }
        return needle;
    }
}
