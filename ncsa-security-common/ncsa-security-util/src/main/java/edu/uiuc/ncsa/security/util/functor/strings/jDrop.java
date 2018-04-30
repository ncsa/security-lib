package edu.uiuc.ncsa.security.util.functor.strings;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * $drop["A",""] removes A from B and returns the rest, e.g. <br/>
 * <pre>
 * $drop["@bigstate.edu","bob@bigstate.edu"]
 * </pre>
 * would return the string "bob". This is very useful for dropping things from the ends of string.
 * If A is not a substring of B then nothing happens. E.g. drop "abcdf" from "abc" has no effect (so
 * watch the order of your arguments.)  Also, this removes all occurances, so removing "ab" from abcabc returns cc.
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/18 at  4:47 PM
 */
public class jDrop extends JFunctorImpl {
    public jDrop() {
        super(FunctorTypeImpl.DROP);
    }

    protected String getStringArg(int index) {
        String arg0 = null;
        Object obj0 = getArgs().get(index);
        if (obj0 instanceof JFunctor) {
            JFunctor j0 = (JFunctor) obj0;
            j0.execute();
            arg0 = (String) j0.getResult();
        } else {
            arg0 = obj0.toString();
        }
        return arg0;
    }

    @Override
    public Object execute() {
        if (getArgs().size() < 2) {
            throw new IllegalArgumentException("Error: not enough arguments");
        }
        String arg0, arg1;
        arg0 = getStringArg(0);
        arg1 = getStringArg(1);
        int index = arg1.indexOf(arg0);
        if (index == -1) {
            executed = true;
            result = arg1;
            return result;
        }
        String output = arg1;

        while (-1 < index) {
            output = output.substring(0, index) + output.substring(index + arg0.length());
            index = output.indexOf(arg0);
        }
        executed = true;
        result = output;
        return result;
    }
}
