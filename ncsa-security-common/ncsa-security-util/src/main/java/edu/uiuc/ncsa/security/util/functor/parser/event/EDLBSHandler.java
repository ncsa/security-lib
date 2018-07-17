package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.util.functor.*;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;
import edu.uiuc.ncsa.security.util.functor.parser.ParserError;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  2:34 PM
 */
public class EDLBSHandler extends AbstractHandler implements DelimiterListener, CommaListener {
    public EDLBSHandler(EventDrivenLogicBlockHandler eventDrivenLogicBlockHandler,
                        JFunctorFactory functorFactory) {
        super(functorFactory);
        lbh = eventDrivenLogicBlockHandler;
    }

    EventDrivenLogicBlockHandler lbh;

    public LogicBlocks<? extends LogicBlock> getLogicBlocks() {
        return logicBlocks;
    }

    LogicBlocks logicBlocks;

    @Override
    public void gotComma(CommaEvent commaEvent) {
        if (lbh.areDelimitersBalanced() && lbh.isUsed()) {
            // end of the line for this set of logic blocks.
            logicBlocks.add(lbh.getLogicBlock());
            lbh.reset();
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
        lbh.reset(); // just in case
        delimeterEvent.getParser().addBracketListener(lbh);
        delimeterEvent.getParser().addCommaListener(lbh);
    }

    @Override
    public void closeDelimiter(DelimiterEvent delimeterEvent) {
        super.closeDelimiter(delimeterEvent);
        logicBlocks.add(lbh.getLogicBlock());
        lbh.reset();
    }

    @Override
    public void reset() {
        super.reset();
        logicBlocks = null;
        lbh.reset();

    }

}
