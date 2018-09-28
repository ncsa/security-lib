package edu.uiuc.ncsa.security.util.functor.strings;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * echo["string"]<br>
 * This is intended to be a debugging tool. If this is invoked on the server, all output is discarded. It is not possible
 * to enable output on the server, only from the command line (where it is on by default.)
 * <p>Created by Jeff Gaynor<br>
 * on 9/20/18 at  11:50 AM
 */
public class jEcho extends JFunctorImpl {
    public jEcho(boolean verboseOn) {
        super(FunctorTypeImpl.ECHO);
        this.verboseOn = verboseOn;
    }

    public jEcho() {
        this(false);
    }

    boolean verboseOn = false;

    @Override
    public Object execute() {
        // This can get executed any number of times, so just keep doing it if requested
        if (verboseOn) {
            for (Object arg : getArgs()) {
                System.out.println(arg);
                result = arg;
            }
        }
        return result;

    }
}
