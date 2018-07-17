package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:46 AM
 */
public class EventDrivenFunctorHandler extends AbstractHandler implements DoubleQuoteListener, DelimiterListener, CommaListener {
    public EventDrivenFunctorHandler(JFunctorFactory functorFactory) {
        super(functorFactory);
    }


    public JFunctor getFunctor() {
        return functor;
    }

    protected JFunctor getFunctor(String name) {
        return getFunctorFactory().lookUpFunctor("$" + name);
    }

    JFunctor functor;    // top-level functor
    JFunctor currentFunctor = null;
    JFunctor previousFunctor = null;
    @Override
    public void gotComma(CommaEvent commaEvent) {
          // really nothing to do at this level.
    }

    @Override
    public void openDelimiter(DelimiterEvent delimeterEvent) {
        super.openDelimiter(delimeterEvent);
        previousFunctor = currentFunctor;
        currentFunctor = getFunctor(delimeterEvent.getName());
        if (functor == null) {
            functor = currentFunctor;
            previousFunctor = null;
        }
        if (previousFunctor != null) {
            previousFunctor.addArg(currentFunctor);
        }
    }

    @Override
    public void closeDelimiter(DelimiterEvent delimeterEvent) {
        super.closeDelimiter(delimeterEvent);
        currentFunctor = previousFunctor;
    }

    @Override
    public void gotText(DoubleQuoteEvent dqEvent) {
        currentFunctor.addArg(dqEvent.getCharaceters());
    }

    @Override
    public void reset() {
        super.reset();
        currentFunctor = null;
        previousFunctor = null;
        functor = null;
    }
}
