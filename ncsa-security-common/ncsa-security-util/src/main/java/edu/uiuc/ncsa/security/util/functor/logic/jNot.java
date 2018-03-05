package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:13 AM
 */
public class jNot extends JFunctorImpl {
    public jNot() {
        super("$not");
    }

    @Override
    public Object execute() {
        if (isExecuted()) {
            return result;
        }
        checkArgs();
        if (1 < args.size()) {
            throw new IllegalStateException("Error: too many args");
        }
        JFunctorImpl ff = (JFunctorImpl) args.get(0);
        ff.execute();
        boolean rc = getBooleanResult();
        rc = !rc;
        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
