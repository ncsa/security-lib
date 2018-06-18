package edu.uiuc.ncsa.security.util.functor.logic;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.EQUALS;

/**
 * A functor to test simple string equality.
 * <pre>
 *     $equals[A,B]
 * </pre>
 * return true if and only if A and B match as string, including case.
 * <p>Created by Jeff Gaynor<br>
 * on 6/13/18 at  5:33 PM
 */
public class jEquals extends jStringComparisons {
    public jEquals() {
        super(EQUALS);
    }

    @Override
    public Object execute() {
        if (executed) {
            return result;
        }
        if (args.size() != 2) {
            throw new IllegalStateException("Error: this functor requires two arguments. It currently has " + args.size() + " arguments.");
        }

        String source = processArg(args.get(0));
        String target = processArg(args.get(1));
        boolean rc = false;
        if (source == null) {
            // can't test if source if null since we'd get an NPE, even if target were null.
            if (target == null) {
                rc = true;
            } else {
                rc = false;
            }
        } else {
            rc = source.equals(target);
        }
        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
