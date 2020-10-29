package edu.uiuc.ncsa.qdl.parsing;

import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.InterruptException;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.ExpressionImpl;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.SIEntry;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.Element;
import edu.uiuc.ncsa.qdl.statements.ModuleStatement;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemListNode;
import edu.uiuc.ncsa.qdl.variables.StemVariableNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * /**
 * The main class that runs a parse tree
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  6:15 AM
 */
public class QDLRunner implements Serializable {
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

    Boolean prettyPrint = Boolean.FALSE;

    public Boolean getPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
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

    public QDLRunner(ArrayList<Element> elements) {
        this.elements = elements;
    }

    public QDLInterpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(QDLInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    QDLInterpreter interpreter;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Element> elements) {
        this.elements = elements;
    }

    ArrayList<Element> elements;

    public void run() throws Throwable {
        State currentState = getState();
        run(0, currentState);

    }

    public void restart(SIEntry siEntry) {
        run(siEntry.lineNumber + 1, siEntry.state);
    }

    protected void run(int startIndex, State currentState) {
        for (int i = startIndex; i < elements.size(); i++) {
            //for (Element element : elements) {
            Element element = elements.get(i);
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
                            if (expression instanceof Polyad) {
                                // so if this is already a print statement, don't wrap it in one
                                boolean isPrint = ((Polyad) expression).getName().equals(IOEvaluator.SAY_FUNCTION) ||
                                        ((Polyad) expression).getName().equals(IOEvaluator.PRINT_FUNCTION);
                                if (!isPrint) {
                                    Polyad p = new Polyad(IOEvaluator.SAY_FUNCTION);
                                    p.addArgument(expression);
                                    p.addArgument(new ConstantNode(prettyPrint, Constant.BOOLEAN_TYPE));
                                    stmt = p;

                                }
                            } else {
                                Polyad p = new Polyad(IOEvaluator.SAY_FUNCTION);
                                p.addArgument(expression);
                                p.addArgument(new ConstantNode(prettyPrint, Constant.BOOLEAN_TYPE));
                                stmt = p;
                            }
                        }
                        if (stmt instanceof StemVariableNode || stmt instanceof StemListNode) {
                            stmt.evaluate(state);
                            ConstantNode cNode = new ConstantNode(((StatementWithResultInterface) stmt).getResult(), Constant.STEM_TYPE);
                            Polyad p = new Polyad(IOEvaluator.SAY_FUNCTION);
                            p.addArgument(cNode);
                            p.addArgument(new ConstantNode(prettyPrint, Constant.BOOLEAN_TYPE));
                            stmt = p;
                        }
                    }
                    try {
                        stmt.evaluate(currentState);
                    } catch (InterruptException ix) {
                        if(!ix.getSiEntry().initialized) {
                            // if it was set up, pass it up the stack
                            ix.getSiEntry().qdlRunner = this;
                            ix.getSiEntry().lineNumber = i; // number where this happened.
                            ix.getSiEntry().interpreter = getInterpreter();
                            ix.getSiEntry().initialized = true;
                        }
                        throw ix;
                    }
                }
            }
        }

    }

}
