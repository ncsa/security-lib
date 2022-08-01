package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:12 AM
 */
public class jIf extends JFunctorImpl {
    public jIf() {
        super(FunctorTypeImpl.IF);
    }

    @Override
    public Object execute() {
        if (isExecuted()) {
            return result;
        }
        checkArgs();
        if (args.size() != 1) {
            throw new IllegalStateException("error: too many arguments");
        }
        JFunctorImpl ff = (JFunctorImpl) args.get(0);
        ff.execute();
        result = ff.getBooleanResult();
        executed = true;
        return result;
    }


}
