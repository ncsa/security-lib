package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.FALSE;

/**
 * The functor that is always logically false.
 * <p>Created by Jeff Gaynor<br>
 * on 3/23/18 at  9:56 AM
 */
public class jFalse extends JFunctorImpl {
    public jFalse() {
        super(FALSE);
    }

    @Override
    public Object execute() {
        executed = true;
        result = Boolean.FALSE;
        return result;
    }
}
