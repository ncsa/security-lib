package edu.uiuc.ncsa.security.util.functor.system;

import edu.uiuc.ncsa.security.core.exceptions.FunctorRuntimeException;
import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * This will throw an exception during execution. Since scripting allows much more complex flows, some sort of
 * ability to raise an exception is needed if, for instance, the state of the script warrants it.
 * All this does on execute is throw a {@link FunctorRuntimeException} with a message.
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/19 at  9:19 AM
 */
public class jRaiseError extends JFunctorImpl {
    public jRaiseError() {
        super(FunctorTypeImpl.RAISE_ERROR);
    }

    @Override
    public Object execute() {
        String message = null;
        if(0 < getArgs().size()){
            message = getArgs().get(0).toString();
        }else{
            message = "(no message available)";
        }

        throw new FunctorRuntimeException(message);
    }
}
