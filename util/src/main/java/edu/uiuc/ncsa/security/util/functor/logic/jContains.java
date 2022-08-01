package edu.uiuc.ncsa.security.util.functor.logic;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.CONTAINS;

/**
 * contains[needle,haystack]<br>
 * This will search for needle in haystack and return true if found.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  10:43 AM
 */
public class jContains extends jStringComparisons {
    public jContains() {
        super(CONTAINS);
    }

    @Override
    public Object execute() {
        if (executed) {
            return result;
        }
        if (args.size() != 2) {
            throw new IllegalStateException("Error: this functor requires two arguments. It currently has " + args.size() + " arguments.");
        }
        String needle = processArg(args.get(0));
        String haystack = processArg(args.get(1));
        boolean rc = haystack.contains(needle);
        result = new Boolean(rc);
        executed = true;
        return result;
    }


}
