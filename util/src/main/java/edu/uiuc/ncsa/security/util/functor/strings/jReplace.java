package edu.uiuc.ncsa.security.util.functor.strings;

import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.REPLACE;

/**
 * This replaces all instance of one string with another. Syntax is
 * <pre>
 *     $replace[target,new,old]
 * </pre>
 * Every instance of old in the target is replaced with new. E.g.
 * <pre>
 *     $replace["mairzy dotes and dozey dotes","stoats","dotes"]
 * </pre>
 * yields
 * <pre>
 *     mairzy stoats and dozey stoats
 * </pre>
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/18 at  3:59 PM
 */
public class jReplace extends JFunctorImpl {
    public jReplace() {
        super(REPLACE);
    }

    @Override
    public Object execute() {
        if (getArgs().size() != 3) {
            throw new IllegalStateException("Error: this functor requires three arguments");
        }
        String target = getValue(0);
        String newString = getValue(1);
        String oldString = getValue(2);
        result = target.replace(newString, oldString);
        executed = true;
        return result;
    }

    protected String getValue(int index) {
        Object obj = getArgs().get(index);
        if (obj == null) {
            return null;  // valueOf would return the word "null" which is a big no-no here.
        }
        if (obj instanceof JFunctor) {
            JFunctor ff = (JFunctor) obj;
            ff.execute();
            return String.valueOf(ff.getResult());
        }
        return obj.toString();
    }
}
