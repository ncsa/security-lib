package edu.uiuc.ncsa.security.util.functor.logic;

import edu.uiuc.ncsa.security.util.functor.JFunctorImpl;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  10:38 AM
 */
public class jThen extends JFunctorImpl {
    public jThen() {
        super("$then");
    }

    protected jThen(String name) {
        super(name);
    }

    /**
     * There is no actual result from this almost functor. What happens is that each element in its argument list is executed.
     *
     * @return
     */
    @Override
    public Object execute() {
        checkArgs();
        for (int i = 0; i < args.size(); i++) {
            JFunctorImpl ff = (JFunctorImpl) args.get(i);
            ff.execute();
        }

        return null;
    }
}
