package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.exceptions.BreakException;
import edu.uiuc.ncsa.qdl.exceptions.ContinueException;
import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.qdl.evaluate.SystemEvaluator.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  4:44 PM
 */
public class WhileLoop implements Statement {
    TokenPosition tokenPosition = null;
    @Override
    public void setTokenPosition(TokenPosition tokenPosition) {this.tokenPosition=tokenPosition;}

    @Override
    public TokenPosition getTokenPosition() {return tokenPosition;}

    @Override
    public boolean hasTokenPosition() {return tokenPosition!=null;}
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
        //State localState = state.new;
        if (conditional instanceof Polyad) {
            Polyad p = (Polyad) conditional;
            if (p.isBuiltIn()) {
                switch (p.getName()) {
                    case FOR_KEYS:
                        return forKeysLoop(localState);
                    case FOR_NEXT:
                        return doForLoop(localState);
                    case CHECK_AFTER:
                        return doPostLoop(localState);
                }
            }
        }

        // No built in looping function, so it is just some ordinary conditional,
        // like i < 5, or a user-defined function.
        // Just evaluate it.
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
        boolean doList = false;
        StemVariable list = null;
        switch (conditional.getArgCount()) {
            case 4:
                arg = conditional.getArguments().get(3).evaluate(localState);
                if (!(arg instanceof Long)) {
                    throw new IllegalArgumentException("Error: the loop increment must be a number");
                }
                Long zzz = (Long) arg;
                increment = zzz.intValue();
            case 3:
                arg = conditional.getArguments().get(2).evaluate(localState);
                if (!(arg instanceof Long)) {
                    throw new IllegalArgumentException("Error: the loop starting value must be a number");
                }
                Long yyy = (Long) arg;
                start = yyy.intValue();
            case 2:
                arg = conditional.getArguments().get(1).evaluate(localState);
                boolean gotArg2 = false;
                if ((arg instanceof Long)) {
                    hasEndvalue = true;
                    Long xxx = (Long) arg;
                    endValue = xxx.intValue();
                    gotArg2 = true;
                }
                if (arg instanceof StemVariable) {
                    list = (StemVariable) arg;
                    doList = true;
                    gotArg2 = true;
                    hasEndvalue = true;

                }
                if (!gotArg2) {
                    throw new IllegalArgumentException("error: the argument \"" + arg + "\" must be a stem or long value");
                }
            case 1:
                if (!hasEndvalue) {
                    throw new IllegalArgumentException("Error: You did not specify the ending value for this loop!");
                }
                // Now, the first argument is supposed to be a variable. We don't evaluate it since
                // we are going to set the value in the local state table and increment it manually.
                if (!(conditional.getArguments().get(0) instanceof VariableNode)) {
                    throw new IllegalArgumentException("Error: You have not specified a variable for looping.");
                }
                VariableNode node = (VariableNode) conditional.getArguments().get(0);
                loopArg = node.getVariableReference();
                break;
            default:
                throw new IllegalArgumentException("Error: incorrect number of arguments for " + FOR_NEXT + ".");
        }
        // while[for_next(j,0,10,-1)]do[say(j);];  // test statememt
        // SymbolTable localST = localState.getSymbolStack().getLocalST();
        if (doList) {
            for (Object key : list.keySet()) {
                localState.setValue(loopArg, list.get(key));
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
        for (int i = start; i != endValue; i = i + increment) {
            localState.setValue(loopArg, (long) i);
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
        try {
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
        } catch (ClassCastException cce) {
            throw new IllegalStateException("Error: You must have a boolean value for your conditional");
        }
    }

    /**
     * for_keys(var, stem.) --  Loop over the keys in a given stem, assigning each to the var.
     *
     * @param localState
     * @return
     */
    protected Object forKeysLoop(State localState) {
        if (conditional.getArgCount() != 2) {
            throw new IllegalArgumentException("Error: You must supply two arguments for " + FOR_KEYS);
        }
        String loopVar = null;
        StemVariable stemVariable = null;
        Object arg = conditional.getArguments().get(1).evaluate(localState);
        if (arg instanceof StemVariable) {
            stemVariable = (StemVariable) arg;
        } else {
            throw new IllegalArgumentException("Error: The target of the command must be a stem variable");
        }

        // Don't evaluate -- we just want the name of the variable.
        arg = conditional.getArguments().get(0);
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
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    List<String> sourceCode;
}
