package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.TRUE;

/**
 * The functor that is always logically true.
 * <p>Created by Jeff Gaynor<br>
 * on 3/23/18 at  9:54 AM
 */
public class jTrue extends JFunctorImpl {
    public jTrue() {
        super(TRUE);
    }

    @Override
    public Object execute() {
        executed = true;
        result = Boolean.TRUE;
        return result;
    }

}
