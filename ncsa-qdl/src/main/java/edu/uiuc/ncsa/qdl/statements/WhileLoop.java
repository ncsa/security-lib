package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.expressions.ExpressionNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.variables.QDLSet;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.VThing;
import edu.uiuc.ncsa.qdl.vfs.VFSEntry;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    public void setTokenPosition(TokenPosition tokenPosition) {
        this.tokenPosition = tokenPosition;
    }

    @Override
    public TokenPosition getTokenPosition() {
        return tokenPosition;
    }

    @Override
    public boolean hasTokenPosition() {
        return tokenPosition != null;
    }

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
        State localState = state.newLocalState();
        //State localState = state.new;
        if (conditional instanceof Dyad) {
            Dyad d = (Dyad) conditional;
            if (d.getOperatorType() == OpEvaluator.EPSILON_VALUE) {
                return forKeysLoop(localState);
            }
        }
        if (conditional instanceof Polyad) {
            Polyad p = (Polyad) conditional;
            if (p.isBuiltIn()) {
                switch (p.getName()) {
                    case FOR_KEYS:
                    case StemEvaluator.HAS_VALUE:
                        return forKeysLoop(localState);
                    case FOR_NEXT:
                        return doForLoop(localState);
                    case CHECK_AFTER:
                        return doPostLoop(localState);
                    case FOR_LINES:
                        return doForLines(localState);
                }
            }
        }

        // No built in looping function, so it is just some ordinary conditional,
        // like i < 5, or a user-defined function.
        // Just evaluate it.
        return doBasicWhile(localState);

    }

    private Object doForLines(State localState) {
        if (conditional.getArgCount() < 2) {
            throw new MissingArgException(FOR_LINES + " requires two arguments", conditional.getArgCount() == 1 ? conditional.getArgAt(0) : conditional);
        }
        if (2 < conditional.getArgCount()) {
            throw new ExtraArgException(FOR_LINES + " requires at most two arguments", conditional.getArgAt(2));

        }
        StatementWithResultInterface swri = conditional.getArgAt(0);
        if (!(swri instanceof VariableNode)) {
            throw new BadArgException(FOR_LINES + " requires a variable as its first argument", conditional.getArgAt(0));
        }
        VariableNode variableNode = (VariableNode) swri;
        String loopArg = variableNode.getVariableReference();
        Object arg2 = conditional.getArgAt(1).evaluate(localState);
        if (!(arg2 instanceof String)) {
            throw new BadArgException(FOR_LINES + " requires a string as its second argument", conditional.getArgAt(1));
        }
        String fileName = (String) arg2;
        VFSEntry vfsEntry = null;
        boolean hasVF = false;
        if (localState.isVFSFile(fileName)) {
            vfsEntry = localState.getMetaEvaluator().resolveResourceToFile(fileName, 1, localState);
            if (vfsEntry == null) {
                throw new QDLException("The resource '" + fileName + "' was not found in the virtual file system");
            }
            hasVF = true;
        } else {
            // Only allow for virtual file reads in server mode.
            // If the file does not live in a VFS throw an exception.
            if (localState.isServerMode()) {
                throw new QDLServerModeException("File system operations not permitted in server mode.");
            }
        }
        if (hasVF) {
            /*
             This is really bad -- it just reads it as a stem and iterates over that.
             What should happen is that FileEntries have a case (probably op type that is
             not available to the general public) that reads the file with a stream and
             returns a Reader. For small files this is not important, but if this is to
             scale up, this functionality needs to be added.
            */
            for (String lineIn : vfsEntry.getLines()) {
                localState.setValue(loopArg, lineIn);
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
        } else {
            try {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String lineIn = bufferedReader.readLine();
                while (lineIn != null) {
                    localState.setValue(loopArg, lineIn);
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
                    lineIn = bufferedReader.readLine();
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                throw new BadArgException(FOR_LINES + " could not find file '" + fileName + "'", conditional.getArgAt(1));
            } catch (IOException e) {
                throw new BadArgException(FOR_LINES + " error reading file '" + fileName + "'", conditional.getArgAt(1));
            }
        }
        return Boolean.TRUE;
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
        QDLSet qdlSet = null;
        Object arg = conditional.getArguments().get(1).evaluate(localState);
        boolean isSet = false;
        if (arg instanceof StemVariable) {
            stemVariable = (StemVariable) arg;
        } else {
            if (arg instanceof QDLSet) {
                qdlSet = (QDLSet) arg;
                isSet = true;
            } else {
                throw new IllegalArgumentException("Error: The target of the command must be a stem or set");
            }
        }

        // Don't evaluate -- we just want the name of the variable.
        arg = conditional.getArguments().get(0); // reuse arg for variable
        if (arg instanceof VariableNode) {
            loopVar = ((VariableNode) arg).getVariableReference();
        } else {
            throw new IllegalArgumentException("Error: The command requires a variable ");

        }

        //   SymbolTable localST = localState.getVStack().getLocalST();
        // my.foo := 'bar'; my.a := 32; my.b := 'hi'; my.c := -0.432;
        //while[for_keys(j,my.)]do[say('key=' + j + ', value=' + my.j);];
        if (isSet) {
            for (Object element : qdlSet) {
                localState.getVStack().localPut(new VThing(new XKey(loopVar), element));
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

        } else {
            for (String key : stemVariable.keySet()) {
                localState.getVStack().localPut(new VThing(new XKey(loopVar), key));
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
