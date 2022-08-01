package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.util.functor.*;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;
import edu.uiuc.ncsa.security.util.functor.parser.ParserError;

import java.util.List;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * Event-Driven Logic BlockS handler. This is the outermost part of any parsing operation.
 * LogicBlocks are basicallt just a type of switch statement that allows for some awareness
 * of how to evaluate the components (standard switch is to evaluate exactly one, out xor case).
 * <p/>
 * This has a few specialized calls to let you get results from running logic blocks.
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  2:34 PM
 */
public class SwitchHandler extends AbstractHandler implements DelimiterListener, CommaListener {
    public SwitchHandler(ConditionalHandler conditionalHandler,
                         JFunctorFactory functorFactory) {
        super(functorFactory);
        logicBlockHandler = conditionalHandler;
    }

    public void runLogicBlocks() {
        getLogicBlocks().execute();
    }

    @Override
    public int getHandlerType() {
        return SWITCH_TYPE;
    }

    /**
     * Get the logic block at the <code>index</code> and return the consequent (i.e. result of
     * having executed the logic block.)
     *
     * @param index
     * @return
     */
    public jThen getConsequentAt(int index) {
        if (!getLogicBlocks().isExecuted()) {
            throw new GeneralException("Error: logic blocks have not been executed");
        }
        return getLogicBlocks().get(index).getConsequent();
    }

    /**
     * Fetches the consequent at <code>consequentIndex</code> then grabs the list at
     * <code>resultIndex</code> (since consequents have lists of results) and returns the
     * object.
     *
     * @param consequentIndex
     * @param resultIndex
     * @return
     */
    public Object getResultAt(int consequentIndex, int resultIndex) {
        return getConsequentAt(consequentIndex).getListResult().get(resultIndex);
    }

    public boolean hasConsequentAt(int index) {
        if (!getLogicBlocks().isExecuted()) {
            throw new GeneralException("Error: logic blocks have not been executed");
        }
        if (index < 0) {
            throw new IllegalArgumentException("Error: index must be non-negative");
        }
        return getLogicBlocks().size() < index;
    }

    public List<JFunctor> getFunctorMapEntry(String key) {
        return getLogicBlocks().getFunctorMap().get(key);
    }

    ConditionalHandler logicBlockHandler;

    public LogicBlocks<? extends LogicBlock> getLogicBlocks() {
        return logicBlocks;
    }

    public void execute() {
        if (logicBlocks != null) {
            getLogicBlocks().execute();
        }
    }

    LogicBlocks logicBlocks;

    @Override
    public void gotComma(CommaEvent commaEvent) {
        if (logicBlockHandler.areDelimitersBalanced() && logicBlockHandler.isUsed()) {
            // end of the line for this set of logic blocks.
            logicBlocks.add(logicBlockHandler.getLogicBlock());
            logicBlockHandler.reset();
        }

    }

    @Override
    public void openDelimiter(DelimiterEvent delimeterEvent) {
        super.openDelimiter(delimeterEvent);
        String name = delimeterEvent.getName();
        if (equals(name, XOR)) {
            logicBlocks = new XORLogicBlocks();
        }
        if (equals(name, AND)) {
            logicBlocks = new ANDLogicBlocks();
        }
        if (equals(name, OR)) {
            logicBlocks = new ORLogicBlocks();
        }
        if (logicBlocks == null) {
            throw new ParserError("Unknown logic blocks structure");
        }
        logicBlockHandler.reset(); // just in case
        delimeterEvent.getParser().addBracketListener(logicBlockHandler);
        delimeterEvent.getParser().addCommaListener(logicBlockHandler);
    }

    @Override
    public void closeDelimiter(DelimiterEvent delimeterEvent) {
        super.closeDelimiter(delimeterEvent);
        logicBlocks.add(logicBlockHandler.getLogicBlock());
        delimeterEvent.getParser().removeBracketListener(logicBlockHandler);
        delimeterEvent.getParser().removeCommaListener(logicBlockHandler);
        logicBlockHandler.reset();
    }

    @Override
    public void reset() {
        super.reset();
        logicBlocks = null;
        logicBlockHandler.reset();

    }

}
