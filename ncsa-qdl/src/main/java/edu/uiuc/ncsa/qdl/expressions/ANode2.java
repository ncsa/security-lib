package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.statements.StatementWithResultInterface;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import static edu.uiuc.ncsa.qdl.variables.Constant.*;
import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/21 at  5:10 AM
 */
public class ANode2 extends ExpressionImpl {
    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    String op;

    public StatementWithResultInterface getLeftArg() {
        if (!hasLeftArg()) {
            return null;
        }
        return getArguments().get(0);
    }

    public void setLeftArg(StatementWithResultInterface leftArg) {
        if (getArguments().size() == 0) {
            getArguments().add(leftArg);
        } else {
            getArguments().set(0, leftArg);
        }
    }

    public StatementWithResultInterface getRightArg() {
        if (!hasRightArg()) {
            return null;
        }
        return getArguments().get(1);
    }

    public void setRightArg(StatementWithResultInterface rightArg) {
        if (getArguments().size() == 1) {

            getArguments().add(rightArg);
        } else {
            getArguments().set(1, rightArg);
        }
    }

    public boolean hasLeftArg() {
        return getArguments() != null && (!getArguments().isEmpty());
    }

    public boolean hasRightArg() {
        return getArguments() != null && (2 <= getArguments().size());
    }

    @Override
    public Object evaluate(State state) {
        //     return oldAssign(state);
        return newAssign(state);
    }

    public Object newAssign(State state) {
        Dyad d = null;

        if (getAssignmentType() != leftAssignmentType && getAssignmentType() != rightAssignmentType) {
            // Do other assignments like +=
            d = new Dyad(getAssignmentType());
            if (getLeftArg() instanceof ANode2) {

                d.setLeftArgument(((ANode2) getLeftArg()).getRightArg());
            } else {
                d.setLeftArgument(getLeftArg());
            }

            d.setRightArgument(getRightArg());
            d.evaluate(state);
            setResult(d.getResult());
            setResultType(d.getResultType());
        } else {
            // regular assignment, evaluate RHS. That is result.
            getRightArg().evaluate(state);
            setResult(getRightArg().getResult());
            setResultType(getRightArg().getResultType());
        }
        setEvaluated(true);
        ANode2 lastAnode = this;
        StatementWithResultInterface realLeftArg = getLeftArg();
        boolean chained = false;
        while (realLeftArg instanceof ANode2) {
            ANode2 rla = (ANode2)realLeftArg;
            ANode2 xNode = new ANode2();
            xNode.setLeftArg(rla.getRightArg());
            xNode.setRightArg(lastAnode.getRightArg());
            xNode.setOp(lastAnode.getOp());
            xNode.evaluate(state);
            lastAnode = rla;
            realLeftArg = rla.getLeftArg();
            chained = true;
        }
        if(chained){
            // Since this was chained, there is every reason to suspect that the
            // value from earlier has been altered. Update it.
            lastAnode.evaluate(state);
            setResult(lastAnode.getResult());
            setResultType(lastAnode.getResultType());
            setEvaluated(true);
        }
        if(realLeftArg instanceof ParenthesizedExpression){
            realLeftArg = ((ParenthesizedExpression)realLeftArg).getExpression();
        }
        if (realLeftArg instanceof VariableNode) {
            state.setValue(((VariableNode)realLeftArg).getVariableReference(), getResult());
            return getResult();
        }
        if (realLeftArg instanceof ConstantNode) {
            throw new IllegalArgumentException("error: cannot assign value to constant \"" + getLeftArg().getResult() + "\"");
        }
        // So all we have in the LHS is an ESN2
        if (realLeftArg instanceof ESN2) {
            ((ESN2) realLeftArg).set(state, getResult());
            return getResult();
        }
        throw new IllegalArgumentException("unknown node type");
    }

    public Object oldAssign(State state) {
        Dyad d = null;

        if (getAssignmentType() != leftAssignmentType && getAssignmentType() != rightAssignmentType) {
            d = new Dyad(getAssignmentType());
            if (getLeftArg() instanceof ANode2) {

                d.setLeftArgument(((ANode2) getLeftArg()).getRightArg());
            } else {
                d.setLeftArgument(getLeftArg());
            }

            d.setRightArgument(getRightArg());
            d.evaluate(state);
            setResult(d.getResult());
            setResultType(d.getResultType());
        } else {
            getRightArg().evaluate(state);
            setResult(getRightArg().getResult());
            setResultType(getRightArg().getResultType());
        }
        setEvaluated(true);
        boolean wasSet = false;
        if (getLeftArg() instanceof ANode2) {
            ANode2 lANode = (ANode2) getLeftArg();
            lANode.getRightArg().evaluate(state);
            if (lANode.getRightArg() instanceof VariableNode) {
                VariableNode vNode = (VariableNode) lANode.getRightArg();
                setVariableValue(state, vNode.getVariableReference(), getResultType(), getResult());
                wasSet = true;
            }
            if (lANode.getRightArg() instanceof ExpressionStemNode) {
                setExpValue(state, (ExpressionStemNode) lANode.getRightArg(), getRightArg().getResultType(), getRightArg().getResult());
                wasSet = true;
            }
            // cannot evaluate the variable node at this point since if it does not exist,
            // it raises an exception.
            if (!getLeftArg().isEvaluated() && (!(getLeftArg() instanceof VariableNode))) {
                getLeftArg().evaluate(state); // this chains to the next.
            }
        } else {
            //          StemUtility.doNodeSurgery((ExpressionStemNode) getLeftArg(), state);

            // cannot evaluate the variable node at this point since if it does not exist,
            // it raises an exception.
            if (getLeftArg() instanceof ESN2) {
                ((ESN2) getLeftArg()).set(state, getRightArg());
                wasSet = true;
            }
            if (getLeftArg() instanceof ExpressionStemNode) {
                setExpValue(state, (ExpressionStemNode) getLeftArg(), getResultType(), getResult());
                wasSet = true;
            }
            if (!getLeftArg().isEvaluated() && (!(getLeftArg() instanceof VariableNode))) {
                getLeftArg().evaluate(state); // this chains to the next.
            }

            if (getLeftArg() instanceof VariableNode) {
                setVariableValue(state, ((VariableNode) getLeftArg()).getVariableReference(), getResultType(), getResult());
                wasSet = true;
            }

        }
        if (!wasSet) {
            throw new IllegalArgumentException("error: could not determine node type to assign value");
        }

        return getResult();
    }


    @Override
    public StatementWithResultInterface makeCopy() {
        ANode2 aNode2 = new ANode2();
        aNode2.setOp(getOp());
        aNode2.setLeftArg(getLeftArg().makeCopy());
        aNode2.setRightArg(getRightArg().makeCopy());
        return aNode2;
    }

    public static final int leftAssignmentType = -100;
    public static final int rightAssignmentType = -200;

    public int getAssignmentType() {
        switch (op) {
            case "+=":
                return OpEvaluator.PLUS_VALUE;
            case "-=":
                return OpEvaluator.MINUS_VALUE;
            case "×=":
            case "*=":
                return OpEvaluator.TIMES_VALUE;
            case "÷=":
            case "/=":
                return OpEvaluator.DIVIDE_VALUE;
            case "%=":
                return OpEvaluator.INTEGER_DIVIDE_VALUE;
            case "^=":
                return OpEvaluator.POWER_VALUE;
            case ":=":
            case "≔":
                return leftAssignmentType;
            case "=:":
            case "≕":
                return rightAssignmentType;
        }
        return OpEvaluator.UNKNOWN_VALUE;
    }

    protected Object setExpValue(State state, ExpressionStemNode esn, int resultType, Object result) {
        return esn.setValue(state, result);
/*
        Object target = getLeftArg().evaluate(state);
        getLeftArg().setEvaluated(true);
        getLeftArg().setResultType(Constant.getType(target));

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
                throw new IllegalArgumentException("error, the type of the value \"" + result + "\" is unknown");

        }
        return result;
*/
    }

    protected Object setVariableValue(State state, String variableReference, int resultType, Object result) {
        // Now the real work -- set the value of the variable in the symbol table.
        // Mostly this just throws an exception if some how we get an unknown type, but this is the
        // right place to do it, before it gets in to the symbol table.

        switch (resultType) {

            case STEM_TYPE:
                if (!variableReference.endsWith(STEM_INDEX_MARKER)) {
                    throw new IllegalArgumentException("Error: Cannot set the stem \"" + variableReference + "\" to a non-stem variable");
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
                if (variableReference.endsWith(STEM_INDEX_MARKER)) {
                    throw new IllegalArgumentException("Error: Cannot set the scalar value \"" + result + "\"to a stem variable \"" + variableReference + "\"");
                }

                state.setValue(variableReference, result);
                break;
            default:
                throw new IllegalArgumentException("error, the type of the value \"" + result + "\" is unknown");

        }
        return result;
    }

    @Override
    public String toString() {
        return "ANode2{" +
                "op='" + op + '\'' +
                ", left=" + getLeftArg() +
                ", right=" + getRightArg() +
                '}';
    }
}
