package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.evaluate.ControlEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.BreakException;
import edu.uiuc.ncsa.qdl.exceptions.ContinueException;
import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.util.StemVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  4:44 PM
 */
public class WhileLoop implements Statement {

    ExpressionNode conditional;

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    public ExpressionNode getConditional() {
        return conditional;
    }

    public void setConditional(ExpressionNode conditional) {
        this.conditional = conditional;
    }

    List<Statement> statements = new ArrayList<>();

    @Override
    public Object evaluate(State state) {
        State localState = state.newStateWithImports();
        if (conditional.getOperatorType() == ControlEvaluator.CHECK_AFTER_TYPE) {
            return doPostLoop(localState);
        }
        if (conditional.getOperatorType() == ControlEvaluator.FOR_NEXT_TYPE) {
            return doForLoop(localState);
        }

        if (conditional.getOperatorType() == ControlEvaluator.FOR_KEYS_TYPE) {
            return hasKeysLoop(localState);
        }
        return doBasicWhile(localState);

    }

    protected Object doPostLoop(State localState) {

        do {
            for (Statement statement : getStatements()) {
                try {
                    statement.evaluate(localState);
                } catch (BreakException b) {
                    return Boolean.TRUE;
                } catch (ContinueException cx) {
                    // just continue.
                } catch (ReturnException rx) {
                    return Boolean.TRUE;
                }
            }
        } while ((Boolean) conditional.evaluate(localState));
        return Boolean.TRUE;
    }


    protected Object doForLoop(State localState) {
        int increment = 1;
        int start = 0;
        int endValue = 0;
        boolean hasEndvalue = false;
        String loopArg = null;
        Object arg = null;

        switch (conditional.getArgumments().size()) {
            case 4:
                arg = conditional.getArgumments().get(3).evaluate(localState);
                if (!(arg instanceof Long)) {
                    throw new IllegalArgumentException("Error: the loop increment must be a number");
                }
                Long zzz = (Long) arg;
                increment = zzz.intValue();
            case 3:
                arg = conditional.getArgumments().get(2).evaluate(localState);
                if (!(arg instanceof Long)) {
                    throw new IllegalArgumentException("Error: the loop starting value must be a number");
                }
                Long yyy = (Long) arg;
                start = yyy.intValue();
            case 2:
                arg = conditional.getArgumments().get(1).evaluate(localState);
                if (!(arg instanceof Long)) {
                    throw new IllegalArgumentException("Error: the loop ending value must be a number");
                }
                hasEndvalue = true;
                Long xxx = (Long) arg;
                endValue = xxx.intValue();
            case 1:
                if (!hasEndvalue) {
                    throw new IllegalArgumentException("Error: You did not specify the ending value for this loop!");
                }
                // Now, the first argument is supposed to be a variable. We don't evaluate it since
                // we are going to set the value in the local state table and increment it manually.
                if (!(conditional.getArgumments().get(0) instanceof VariableNode)) {
                    throw new IllegalArgumentException("Error: You have not specified a variable for looping.");
                }
                VariableNode node = (VariableNode) conditional.getArgumments().get(0);
                loopArg = node.getVariableReference();
                break;
            default:
                throw new IllegalArgumentException("Error: incorrect number of arguments for " + ControlEvaluator.FOR_NEXT + ".");
        }
        // while[for_next(j,0,10,-1)]do[say(j);];  // test statememt
        SymbolTable localST = localState.getSymbolStack().getLocalST();

        for (int i = start; i != endValue; i = i + increment) {
            localST.setLongValue(loopArg, (long) i);
            for (Statement statement : getStatements()) {
                try {
                    statement.evaluate(localState);
                } catch (BreakException b) {
                    return Boolean.TRUE;
                } catch (ContinueException cx) {
                    // just continue.
                } catch (ReturnException rx) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.TRUE;
    }

    protected Object doBasicWhile(State localState) {
        while ((Boolean) conditional.evaluate(localState)) {
            for (Statement statement : getStatements()) {
                try {
                    statement.evaluate(localState);
                } catch (BreakException b) {
                    return Boolean.TRUE;
                } catch (ContinueException cx) {
                    // just continue.
                } catch (ReturnException rx) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.TRUE;
    }

    /**
     * for_keys(var, stem.) --  Loop over the keys in a given stem, assigning each to the var.
     *
     * @param localState
     * @return
     */
    protected Object hasKeysLoop(State localState) {
        if (conditional.getArgumments().size() != 2) {
            throw new IllegalArgumentException("Error: You must supply two arguments for " + ControlEvaluator.FOR_KEYS);
        }
        String loopVar = null;
        StemVariable stemVariable = null;
        Object arg = conditional.getArgumments().get(1).evaluate(localState);
        if (arg instanceof StemVariable) {
            stemVariable = (StemVariable) arg;
        } else {
            throw new IllegalArgumentException("Error: The target of the command must be a stem variable");
        }

        // Don't evaluate -- we just want the name of the variable.
        arg = conditional.getArgumments().get(0);
        if (arg instanceof VariableNode) {
            loopVar = ((VariableNode) arg).getVariableReference();
        } else {
            throw new IllegalArgumentException("Error: The command requires a variable ");

        }

        SymbolTable localST = localState.getSymbolStack().getLocalST();
        // my.foo := 'bar'; my.a := 32; my.b := 'hi'; my.c := -0.432;
        //while[for_keys(j,my.)]do[say('key=' + j + ', value=' + my.j);];
        for (String key : stemVariable.keySet()) {
            localST.setStringValue(loopVar, key);
            for (Statement statement : getStatements()) {
                try {
                    statement.evaluate(localState);
                } catch (BreakException b) {
                    return Boolean.TRUE;
                } catch (ContinueException cx) {
                    // just continue.
                } catch (ReturnException rx) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    String sourceCode;
}