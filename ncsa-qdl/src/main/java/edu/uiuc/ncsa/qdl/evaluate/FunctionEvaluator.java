package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.MissingArgumentException;
import edu.uiuc.ncsa.qdl.exceptions.RecursionException;
import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.statements.FR_WithState;
import edu.uiuc.ncsa.qdl.statements.FunctionRecord;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.variables.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/22/20 at  10:53 AM
 */
public class FunctionEvaluator extends AbstractFunctionEvaluator {
    List<FunctionTable> functionTables = new ArrayList<>();
    public static final int BASE_FUNCTION_VALUE = 6000;
    public static final String IS_FUNCTION = "is_function";
    public static final int IS_FUNCTION_TYPE = 1 + 6000;

    @Override
    public int getType(String name) {
        if (name.equals(IS_FUNCTION)) return IS_FUNCTION_TYPE;
        // At parsing time, the function definition class sets the value manually,
        // so call to this should ever get anything other than unknown value.
        return UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getOperatorType()) {
            case IS_FUNCTION_TYPE:
                if (polyad.getArgumments().size() != 1) {
                    throw new IllegalArgumentException("Error: You must supply at least one argument.");
                }
                Object object = polyad.evalArg(0,state);;
                if (object == null) {
                    throw new MissingArgumentException("Error: You must supply an argument for the " + IS_FUNCTION + " command.");
                }
                if (!isString(object)) {
                    throw new IllegalArgumentException("Error: the " + IS_FUNCTION + " command requires a string as its first argument.");
                }
                String name = object.toString();
                int argCount = -1; // default -- get any
                if (polyad.getArgumments().size() == 2) {
                    Object object2 = polyad.evalArg(1,state);;
                    if (!isLong(object2)) {
                        throw new IllegalArgumentException("Error: The argument count must be a number.");
                    }
                    argCount = ((Long) object2).intValue();
                }

                polyad.setResult(state.resolveFunction(name, argCount).functionRecord != null);
                polyad.setResultType(Constant.BOOLEAN_TYPE);
                polyad.setEvaluated(true);
                return true;
            case UNKNOWN_VALUE:
                figureOutEvaluation(polyad, state);
                return true;

        }
        return false;
    }

    protected void doJavaFunction(Polyad polyad, State state, FR_WithState frs) {
        // Contains a java function that is wrapped in a QDLFunction. The polyad here contains the
        // arguments that are needed to unpack this.
        Object[] argList = new Object[polyad.getArgumments().size()];
        for (int i = 0; i < polyad.getArgumments().size(); i++) {
            argList[i] = polyad.getArgumments().get(i).evaluate(state);
        }
        QDLFunctionRecord qfr = (QDLFunctionRecord) frs.functionRecord;
        Object result = qfr.qdlFunction.getInstance().evaluate(argList);
        polyad.setResult(result);
        polyad.setEvaluated(true);
        polyad.setResultType(Constant.getType(result));
        return;


    }

    protected void figureOutEvaluation(Polyad polyad, State state) {
        FR_WithState frs = state.resolveFunction(polyad);
        if (frs.isExternalModule) {
            doJavaFunction(polyad, state, frs);
        } else {
            doFunctionEvaluation(polyad, state, frs);
        }
    }

    protected void doFunctionEvaluation(Polyad polyad, State state, FR_WithState frs) {
        FunctionRecord functionRecord = frs.functionRecord;
        if (functionRecord == null) {
            throw new UndefinedFunctionException("Error: the function \"" + polyad.getName() + "\" with "
                    + polyad.getArgumments().size() + " arguments was not found.");
        }
        State localState = frs.state.newLocalState();
        // we are going to write local variables here and the MUST get priority over already exiting ones
        // but without actually changing them (or e.g., recursion is impossible). 
        SymbolTable symbolTable = localState.getSymbolStack().getTopST();
        // now we populate the local state with the variables.
        for (int i = 0; i < functionRecord.getArgCount(); i++) {
            // note that the call evaluates the state in the non-local environment as per contract,
            // but the named result goes in to the localState.
            symbolTable.setValue(functionRecord.argNames.get(i), polyad.getArgumments().get(i).evaluate(state));
        }
        for (Statement statement : functionRecord.statements) {
            try {
                statement.evaluate(localState);
            } catch (ReturnException rx) {
                polyad.setResult(rx.result);
                polyad.setResultType(rx.resultType);
                polyad.setEvaluated(true);
                for (int i = 0; i < functionRecord.getArgCount(); i++) {
                    symbolTable.remove(functionRecord.argNames.get(i));
                }
                return;
            } catch (java.lang.StackOverflowError sx) {
                throw new RecursionException();
            }
        }
        // Now remove the variables we created from the stack since they are no longer needed AND there is no
        // way to otherwise be rid of them.
        for (int i = 0; i < functionRecord.getArgCount(); i++) {
            symbolTable.remove(functionRecord.argNames.get(i));
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

}
