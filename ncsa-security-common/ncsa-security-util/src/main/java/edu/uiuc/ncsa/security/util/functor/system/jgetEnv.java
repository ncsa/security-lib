package edu.uiuc.ncsa.security.util.functor.system;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

import java.util.Map;

/**
 * getEnv(key) -- return a value stored in the local runtime environment
 * <p>Created by Jeff Gaynor<br>
 * on 9/20/18 at  2:32 PM
 */
public class jgetEnv extends JFunctorImpl {
    public jgetEnv(Map<String,String> environment) {
        super(FunctorTypeImpl.GET_ENV);
        this.environment = environment;
    }

    protected Map<String,String> environment;

    @Override
    public Object execute() {
        if(executed){
            return result;
        }
        if(getArgs().size() < 1){
            result = null;
        }else{
            result = environment.get(getArgs().get(0));
        }
        executed = true;

        return result;
    }
}
