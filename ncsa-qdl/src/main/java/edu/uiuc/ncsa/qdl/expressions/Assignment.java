package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.state.QDLConstants;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.HasResultInterface;
import edu.uiuc.ncsa.qdl.statements.Statement;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import java.util.List;

import static edu.uiuc.ncsa.qdl.variables.Constant.*;
import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * Once evaluated, this will return its value as the result.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  5:08 PM
 */
public class Assignment implements Statement, HasResultInterface {
    public boolean isFlippedAssignment() {
        return flippedAssignment;
    }

    public void setFlippedAssignment(boolean flippedAssignment) {
        this.flippedAssignment = flippedAssignment;
    }

    boolean flippedAssignment = false;
    Object result;

    public Object getResult() {
        if (!isEvaluated()) {
            throw new UnevaluatedExpressionException("source='" + (getSourceCode() == null ? "(none)" : getSourceCode()) + "\'");
        }
        return result;
    }

    public StatementWithResultInterface getExpStatement() {
        return expStatement;
    }

    public void setExpStatement(StatementWithResultInterface expStatement) {
        this.expStatement = expStatement;
    }

    StatementWithResultInterface expStatement = null;

    public boolean hasExpStatement() {
        return expStatement != null;
    }

    public int getResultType() {
        return resultType;
    }

    @Override
    public void setResult(Object object) {
        this.result = object;
    }

    @Override
    public void setResultType(int type) {
        this.resultType = type;
    }

    protected boolean evaluated;

    @Override
    public boolean isEvaluated() {
        return evaluated;
    }

    @Override
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    int resultType;

    public String getVariableReference() {
        return variableReference;
    }

    public void setVariableReference(String variableReference) {
        this.variableReference = variableReference;
    }

    public Statement getArgument() {
        return argument;
    }

    public void setArgument(Statement argument) {
        this.argument = argument;
    }

    String variableReference;
    Statement argument;

    // just a case
    protected HasResultInterface getHRI() {
        return (HasResultInterface) argument;
    }

    public Object evaluate(State state) {
        if (QDLConstants.isReservedWord(variableReference)) {
            throw new IllegalArgumentException("Error: Cannnot use reserved word \"" + variableReference + "\" as a variable.");
        }
        if (flippedAssignment) {
            Statement origArg = argument;
            if ((argument instanceof ExpressionStemNode) && expStatement == null) {
                // edge case of ESN. This is resolved to a variable reference so it has to
                // be turned into a variable node to get the current value from the state
                // at the right time.
                argument = new VariableNode(variableReference);
            } else {
                argument = expStatement;
            }

            if (origArg instanceof VariableNode) {
                variableReference = ((VariableNode) origArg).getVariableReference();
                expStatement = null;
            } else {
                // If this is chained with other assignments it can get close to impossible
                // to determine what should be assigned to what with our simple-minded
                // flipping scheme a =: b --> b := a
                // Consider 5 =: a := 7  or a ^= b =: c += 7
                // Parentheses would help, but this is getting out of hand.
                if (origArg instanceof Assignment) {
                    throw new IllegalArgumentException("error: cannot chain =: with any assignments");
                }
                if (origArg instanceof StatementWithResultInterface) {
                    expStatement = (StatementWithResultInterface) origArg;
                }
            }
        }
        result = argument.evaluate(state);

        getHRI().setEvaluated(true);
        setResult(getHRI().getResult());
        evaluated = true;
        resultType = getHRI().getResultType();
        if (hasExpStatement()) {
            return setExpValue(state, resultType, result);
        }
        return setVariableValue(state, resultType, result);
    }

    /*
      j(n)->n;a.:=i(5);(a.).j(2) :=100;

     */
    protected Object setExpValue(State state, int resultType, Object result) {
        if (getExpStatement() instanceof ExpressionStemNode) {
            ExpressionStemNode esn = (ExpressionStemNode) getExpStatement();
            return esn.setValue(state, result);
        }
        Object target = getExpStatement().evaluate(state);
        getExpStatement().setEvaluated(true);
        getExpStatement().setResultType(Constant.getType(target));

        switch (resultType) {
            case STEM_TYPE:
                break;
            case NULL_TYPE:
                break;
            case STRING_TYPE:
            case BOOLEAN_TYPE:
            case LONG_TYPE:
            case DECIMAL_TYPE:
                break;
            default:
                throw new IllegalArgumentException("error, the type of the value \"" + getHRI().getResult() + "\" is unknown");

        }
        return result;
    }

    protected Object setVariableValue(State state, int resultType, Object result) {
        // Now the real work -- set the value of the variable in the symbol table.
        // Mostly this just traps if some how we get an unknown type, but this is the
        // right place to do it, before it gets in to the symbol table.

        switch (resultType) {

            case STEM_TYPE:
                if (!getVariableReference().endsWith(STEM_INDEX_MARKER)) {
                    throw new IllegalArgumentException("Error: Cannot set the stem \"" + getVariableReference() + "\" to a non-stem variable");
                }

                state.setValue(variableReference, result);
                break;
            case NULL_TYPE:
                // Can set any variable to null
                state.setValue(variableReference, QDLNull.getInstance());
                break;

            case STRING_TYPE:
            case BOOLEAN_TYPE:
            case LONG_TYPE:
            case DECIMAL_TYPE:
                if (getVariableReference().endsWith(STEM_INDEX_MARKER)) {
                    throw new IllegalArgumentException("Error: Cannot set the scalar value to a stem variable");
                }

                state.setValue(variableReference, result);
                break;
            default:
                throw new IllegalArgumentException("error, the type of the value \"" + getHRI().getResult() + "\" is unknown");

        }
        return result;
    }

    public Assignment() {
    }


    public Assignment(String variableReference, ExpressionNode node) {
        this.variableReference = variableReference;
        argument = node;
    }

    @Override
    public List<String> getSourceCode() {
        return sourceCode;
    }

    @Override
    public void setSourceCode(List<String> sourceCode) {
        this.sourceCode = sourceCode;
    }

    List<String> sourceCode = null;
}
