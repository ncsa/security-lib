package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.util.functor.JFunctor;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;

import java.util.Stack;

/**
 * This creates functors by adding arguments to them.
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:46 AM
 */
public class FunctorHandler extends AbstractHandler implements DoubleQuoteListener, DelimiterListener, CommaListener {
    public FunctorHandler(JFunctorFactory functorFactory) {
        super(functorFactory);
    }

    Stack<JFunctor> stack = new Stack<>();

    /**
     * Return the result from te functor that was executed.
     *
     * @return
     */
    public Object getFResult() {
        if (getFunctor() == null) {
            throw new IllegalStateException("Error: This handler has not been executed yet.");
        }
        return getFunctor().getResult();
    }

    public JFunctor getFunctor() {
        return functor;
    }

    /**
     * Internal call to the factory to find the functor there by name.
     *
     * @param name
     * @return
     */
    protected JFunctor lookupFunctor(String name) {
        return (JFunctor)getFunctorFactory().lookUpFunctor("$" + name);
    }

    JFunctor functor;    // top-level functor

    @Override
    public void gotComma(CommaEvent commaEvent) {
    }

    @Override
    public int getHandlerType() {
        return FUNCTOR_TYPE;
    }

    @Override
    public void openDelimiter(DelimiterEvent delimeterEvent) {
        super.openDelimiter(delimeterEvent);
        JFunctor currentFunctor = lookupFunctor(delimeterEvent.getName());
        stack.push(currentFunctor);
        if (functor == null) {
            /*
            This is the first element in the stack. Save this here since if the parenthesis balance right,
            the stack should be empty when this is finished running, so there is nothing to pop off the
            stack then.
             */
            functor = currentFunctor;
        }
        if (1 < stack.size()) {
            // size - 2 gets the previous item on the stack since in this LIFO stack element 0 is the most recent.
            stack.get(stack.size() - 2).addArg(stack.peek());
        }
    }

    @Override
    public void closeDelimiter(DelimiterEvent delimeterEvent) {
        super.closeDelimiter(delimeterEvent);
        stack.pop();

    }

    @Override
    public void gotText(DoubleQuoteEvent dqEvent) {
        stack.peek().addArg(dqEvent.getCharacters());
    }

    @Override
    public void reset() {
        super.reset();
        stack.clear();
        functor = null;
    }
}
