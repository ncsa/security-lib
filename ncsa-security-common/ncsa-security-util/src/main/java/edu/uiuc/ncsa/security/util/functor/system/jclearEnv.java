package edu.uiuc.ncsa.security.util.functor.system;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import java.util.Map;

/**
 * clearEnv()<br>
 *     Clears all the values of the current environment.
 *     return true if successful.
 * <p>Created by Jeff Gaynor<br>
 * on 9/22/18 at  5:20 PM
 */
public class jclearEnv extends JFunctorImpl{
    public jclearEnv(Map<String,String> environment) {
        super(FunctorTypeImpl.CLEAR_ENV);
        this.environment = environment;
    }

    Map<String,String> environment;
    @Override
    public Object execute() {
        environment.clear();
        result = Boolean.TRUE;
        return result;
    }
}
