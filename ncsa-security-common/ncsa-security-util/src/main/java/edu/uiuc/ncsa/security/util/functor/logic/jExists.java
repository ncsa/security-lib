package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctor;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.EXISTS;

/**
 * A functor to test if a string exists.
 * <pre>
 *     $exists[A]
 * </pre>
 * this yields false if A is null or empty. IT yields true otherwise. Note that
 * <pre>
 *     $exists["   "]
 * </pre>
 * (a bunch of blanks) is true. If you wanted to check against a string of blanks you might use something like
 * <pre>
 *     $exists[$trim[A]]
 * </pre>
 * which would remove all the whitspace first.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  1:09 PM
 */
public class jExists extends jStringComparisons {
    public jExists() {
        super(EXISTS);
    }

    @Override
    public Object execute() {
        if (isExecuted()) {
            return result;
        }
        boolean rc = false;
        if (args.size() == 0) {
            rc = false;
        } else {
            // only check first arg
            Object obj = args.get(0);

            if (obj instanceof JFunctor) {
                JFunctor ff = (JFunctor) obj;
                ff.execute();
                obj = ff.getResult();
            }

            if (obj == null) {
                rc = false;
            } else {
                if (obj instanceof String) {
                    rc = !((String) obj).isEmpty();
                } else {
                    rc = true; // not sure what it is, but it is something...
                }
            }
        }
        result = rc;
        executed = true;
        return result;
    }
}
