package edu.uiuc.ncsa.security.util.functor.parser.old;

import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/15/18 at  5:38 PM
 */
public class FunctorHandler2 extends AbstractHandler implements FunctorHandlerInterface {
    public FunctorHandler2(JFunctorFactory functorFactory) {
        super(functorFactory);
    }

    protected JFunctor getFunctor(String name){
        return getFunctorFactory().lookUpFunctor("$" + name);
    }


    @Override
    public void reset() {
        currentFunctor = null;
        previousFunctor = null;
        functor = null;
    }

    @Override
    public void startParenthesis(String name) {
        previousFunctor = currentFunctor;
        currentFunctor = getFunctor(name);
        if(functor == null ) {
            functor = currentFunctor;
            previousFunctor = null;
        }
        if(previousFunctor != null){
            previousFunctor.addArg(currentFunctor);
        }
    }

    public JFunctor getFunctor() {
        return functor;
    }

    JFunctor functor;    // top-level functor
    JFunctor currentFunctor = null;
    JFunctor previousFunctor = null;
    @Override
    public void endParenthesis(String name) {
              currentFunctor = previousFunctor;
    }

    @Override
    public void foundComma(String name) {

    }

    @Override
    public void foundDoubleQuotes(String content) {
           currentFunctor.addArg(content);
    }
}
