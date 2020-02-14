package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Element;
import edu.uiuc.ncsa.qdl.statements.ModuleStatement;
import edu.uiuc.ncsa.qdl.statements.Statement;

import java.util.List;

/**
 * /**
 * The main class that runs a parse tree
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  6:15 AM
 */
public class QDLRunner {
    public boolean isEchoModeOn() {
        return echoModeOn;
    }

    public void setEchoModeOn(boolean echoModeOn) {
        this.echoModeOn = echoModeOn;
    }

    boolean echoModeOn = false;

    public State getState() {
        return state;
    }

    /**
     * You may inject state at runtime if you need this to start with some existing state.
     *
     * @param state
     */
    public void setState(State state) {
        this.state = state;
    }

    State state;

    public QDLRunner(List<Element> elements) {
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    List<Element> elements;

    public void run() throws Throwable {
        State currentState = getState();
        for (Element element : elements) {
            if (element.getStatement() != null) {
                // it can happen that the parser returns an empty statement. Skip it.
                Statement stmt = element.getStatement();
                if (stmt instanceof ModuleStatement) {
                    ModuleStatement ms = (ModuleStatement) stmt;
                    ms.evaluate(currentState);
                } else {
                    if (isEchoModeOn()) {
                        // used by the workspace to print each statement's result to the console.
                        if ((stmt instanceof ExpressionImpl)) {
                            ExpressionImpl expression = (ExpressionImpl) stmt;
                            if (expression.getOperatorType() != currentState.getMetaEvaluator().getType("say")) {
                                Polyad p = new Polyad("say");
                                p.setOperatorType(IOEvaluator.SAY_TYPE);
                                p.addArgument(expression);
                                stmt = p;
                            }
                        }
                    }
                    stmt.evaluate(currentState);
                }
            }
        }

    }

}
