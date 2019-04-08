package edu.uiuc.ncsa.security.util.functor.user_defined;

import edu.uiuc.ncsa.security.util.functor.FunctorType;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/5/19 at  2:42 PM
 */
public class UserDefined extends JFunctorImpl {
    public UserDefined(FunctorType type) {
        super(type);
    }

    @Override
    public Object execute() {
        return null;
    }
}
