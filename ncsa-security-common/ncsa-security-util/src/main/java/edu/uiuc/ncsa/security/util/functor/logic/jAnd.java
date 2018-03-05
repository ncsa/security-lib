package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  8:57 AM
 */
public class jAnd extends JFunctorImpl {
    public jAnd() {
        super("$and");
    }

    @Override
    public Object execute() {
        if (isExecuted()) {
            return result;
        }
        checkArgs();
        boolean rc = true;
        for (int i = 0; i < args.size(); i++) {
            JFunctorImpl ff = (JFunctorImpl) args.get(i);
            ff.execute();
            rc = rc && ff.getBooleanResult();
        }
        result = new Boolean(rc);

        executed = true;
        return rc;
    }
}
