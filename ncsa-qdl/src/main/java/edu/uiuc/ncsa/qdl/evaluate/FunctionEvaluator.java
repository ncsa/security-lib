package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.ReturnException;
import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
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

    /**
     * Function tables may be added here with added functions as well. Just populate them however you
     * want and this will find them. The only caveat is that the names must be unique.
     *
     * @param functionTable
     */
    public void addFunctionTable(FunctionTable functionTable) {
        functionTables.add(functionTable);
    }

    protected FunctionRecord findFunction(String name, int argCount) {
        for (FunctionTable ft : functionTables) {
            if (ft.containsKey(name, argCount)) {
                return ft.get(name, argCount);
            }
        }
        return null;
    }

    protected boolean hasFunction(Polyad polyad) {
        return findFunction(polyad.getName(), polyad.getArgumments().size()) != null;
    }

    public static final int USER_DEFINED_TYPE = 10000;

    @Override
    public int getType(String name) {
        // At parsing time, the function definition class sets the value manually,
        // so call to this should ever get anything other than unknown value.
        return UNKNOWN_VALUE;
    }

    @Override
    public boolean evaluate(Polyad polyad, State state) {

        if (hasFunction(polyad)) {
            doFunctionEvaluation(polyad, state);
            return true;
        }
        return false;
    }

    protected void doFunctionEvaluation(Polyad polyad, State state) {
        FunctionRecord functionRecord = findFunction(polyad.getName(), polyad.getArgumments().size());
        if (functionRecord == null) {
            throw new UndefinedFunctionException("Error: the function \"" + polyad.getName() + "\" with "
                    + polyad.getArgumments().size() + " arguments was not found.");
        }
        State localState = state.newLocalState();
        SymbolTable symbolTable = localState.getSymbolStack();
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
                return;
            }
        }
        polyad.setResult(Boolean.TRUE);
        polyad.setResultType(Constant.BOOLEAN_TYPE);
        polyad.setEvaluated(true);
    }

}
