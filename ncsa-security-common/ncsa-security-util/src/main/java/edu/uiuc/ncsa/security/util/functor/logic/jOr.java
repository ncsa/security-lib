package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.OR;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:11 AM
 */
public class jOr extends JFunctorImpl {
    public jOr() {
        super(OR);
    }

    @Override
    public Object execute() {
        if (isExecuted()) {
                  return result;
              }
        checkArgs();
        boolean rc = false;
        for (int i = 0; i < args.size(); i++) {
            JFunctorImpl ff = (JFunctorImpl) args.get(i);
            ff.execute();
            rc = rc || ff.getBooleanResult();
        }
        result = new Boolean(rc);
        executed = true;
        return rc;
    }
}
