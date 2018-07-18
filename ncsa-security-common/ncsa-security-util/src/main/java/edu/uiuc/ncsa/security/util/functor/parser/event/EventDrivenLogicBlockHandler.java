package edu.uiuc.ncsa.security.util.functor.parser.event;

import edu.uiuc.ncsa.security.util.functor.*;
import edu.uiuc.ncsa.security.util.functor.logic.jElse;
import edu.uiuc.ncsa.security.util.functor.logic.jIf;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;
import edu.uiuc.ncsa.security.util.functor.parser.ParserError;

import java.util.Stack;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  12:00 PM
 */
public class EventDrivenLogicBlockHandler extends AbstractHandler implements DelimiterListener, CommaListener {
    EventDrivenFunctorHandler fHandler;
    Stack<LogicBlock> stack = new Stack<>();

    public EventDrivenLogicBlockHandler(EventDrivenFunctorHandler fHandler, JFunctorFactory functorFactory) {
        super(functorFactory);
        this.fHandler = fHandler;
    }
    // trick here is that this must manage the fHandler as well.

    /**
     * Call this absolutely last since it has to have all the parts to assemble itself.
     *
     * @return
     */
    public LogicBlock getLogicBlock() {
        return logicBlock;
    }

    LogicBlock logicBlock;
    jIf ifBlock = null;
    jThen thenBlock = null;
    jElse elseBlock = null;

    public JFunctorImpl getCurrentBlock() {
        return currentBlock;
    }

    JFunctorImpl currentBlock;

    @Override
    public void openDelimiter(DelimiterEvent delimeterEvent) {
        super.openDelimiter(delimeterEvent);
        boolean gotOne = false;
        String name = delimeterEvent.getName();
        if (equals(name, FunctorTypeImpl.IF)) {
            if (ifBlock != null) {
                throw new ParserError("Error: malformed conditional. There is already an if statement");
            }
            ifBlock = new jIf();
            logicBlock = new LogicBlock(getFunctorFactory(),ifBlock,null);
            stack.push(logicBlock);
            currentBlock = ifBlock;
            gotOne = true;
        }
        if (equals(name, FunctorTypeImpl.THEN)) {
            if (thenBlock != null) {
                throw new ParserError("Error: malformed conditional. There is already a then statement");
            }
            thenBlock = new jThen();
            stack.peek().setThenBlock(thenBlock);
            currentBlock = thenBlock;
            gotOne = true;
        }
        if (equals(name, FunctorTypeImpl.ELSE)) {
            if (elseBlock != null) {
                throw new ParserError("Error: malformed conditional. There is already an else statement");
            }
            elseBlock = new jElse();
            stack.peek().setElseBlock(elseBlock);
            currentBlock = elseBlock;
            gotOne = true;
        }

        if (gotOne) {
            fHandler.reset();
            delimeterEvent.getParser().addParenthesisListener(fHandler);
            delimeterEvent.getParser().addCommaListener(fHandler);
            delimeterEvent.getParser().addDoubleQuoteListener(fHandler);

        } else {
            throw new ParserError("Error: unknown logic block type");

        }
    }


    @Override
    public void closeDelimiter(DelimiterEvent delimeterEvent) {
        super.closeDelimiter(delimeterEvent);
        JFunctor functor = getjFunctor(delimeterEvent.getName());
        currentBlock.addArg(functor);
        delimeterEvent.getParser().removeCommaListener(fHandler);
        delimeterEvent.getParser().removeDQListener(fHandler);
        delimeterEvent.getParser().removeParenthesisListener(fHandler);
        fHandler.reset();
        currentBlock = null;
    }

    protected JFunctor getjFunctor(String name) {
        JFunctor functor = fHandler.getFunctor();
        if (functor == null) {
            // ok, special case that the keywords true/false get turned into functors.
            if (name.equals(FunctorTypeImpl.TRUE.getValue().substring(1))) {
                functor = getFunctorFactory().lookUpFunctor(FunctorTypeImpl.TRUE);
            }
            if (name.equals(FunctorTypeImpl.FALSE.getValue().substring(1))) {
                functor = getFunctorFactory().lookUpFunctor(FunctorTypeImpl.FALSE);
            }
        }
        if (functor == null) {
            throw new IllegalArgumentException("Error: Could not find the functor for \"" + name + "\"");
        }

        return functor;
    }


    @Override
    public void gotComma(CommaEvent commaEvent) {
        if (fHandler.areDelimitersBalanced()&&fHandler.isUsed()) {// this has to be in use so that the comma is inside some set of delimiters.
            // then we have finished with this functor
            if(currentBlock == null){
                System.out.println(getClass().getSimpleName() + ".gotComma: text=" + commaEvent.getText());
            }
            getCurrentBlock().addArg(getjFunctor(commaEvent.getText()));
            fHandler.reset();
        }
    }

    @Override
    public void reset() {
        super.reset();
        fHandler.reset();
        logicBlock = null;
        ifBlock = null;
        thenBlock = null;
        elseBlock = null;
        currentBlock = null;
    }
}
