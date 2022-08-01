package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;
import edu.uiuc.ncsa.security.util.functor.JSONFunctor;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.NOT;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:13 AM
 */
public class jNot extends JFunctorImpl {
    public jNot() {
        super(NOT);
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
        JSONFunctor ff = (JSONFunctor) args.get(0);
        ff.execute();
        boolean rc = (Boolean)ff.getResult();
        rc = !rc;
        result = new Boolean(rc);
        executed = true;
        return result;
    }
}
