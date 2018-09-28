package edu.uiuc.ncsa.security.util.functor.system;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import java.util.Map;

/**
 * setEnv(key, value) - set a value in the local runtime environment
 * <p>Created by Jeff Gaynor<br>
 * on 9/20/18 at  2:28 PM
 */
public class jsetEnv extends JFunctorImpl {
    public jsetEnv(Map<String, String> envirnoment) {
        super(FunctorTypeImpl.SET_ENV);
        this.envirnoment = envirnoment;
    }

    protected Map<String, String> envirnoment;

    @Override
    public Object execute() {
        if (executed) {
            return result;
        }
        if (getArgs().size() < 2 || envirnoment == null) {
            // skip it
            result = null;
        } else {
            envirnoment.put(getArgs().get(0).toString(), getArgs().get(1).toString());
            result = getArgs().get(1).toString();
        }
        executed = true;
        return result;
    }
}
