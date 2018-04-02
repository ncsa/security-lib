package edu.uiuc.ncsa.security.util.functor.logic;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.EXISTS;

/**
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
            rc = true;
        }
        if (1 == args.size()) {
            Object obj = args.get(0);
            if (obj == null) {
                rc = true;
            } else {
                if (!(obj instanceof String)) {
                    rc = false;
                } else {
                    String x = (String) obj;
                    rc = x.isEmpty();
                }
            }
        }

        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
