package edu.uiuc.ncsa.security.util.qdl.expressions;


import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;

/**
 * Class the will evaluate and expression
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:20 PM
 */
public class OpEvaluator implements EvaluatorInterface {

    public static String ASSIGNMENT = ":=";
    public static String TIMES = "*";
    public static String DIVIDE = "/";
    public static String PLUS = "+";
    public static String MINUS = "-";
    public static String PLUS_PLUS = "++";
    public static String MINUS_MINUS = "--";
    public static String AND = "&&";
    public static String OR = "||";
    public static String EQUALS = "==";
    public static String NOT_EQUAL = "!=";
    public static String LESS_THAN = "<";
    public static String LESS_THAN_EQUAL = "<=";
    public static String MORE_THAN = ">";
    public static String MORE_THAN_EQUAL = ">=";
    public static String NOT = "!";


    public static final int ASSIGNMENT_VALUE = 10;
    public static final int PLUS_VALUE = 100;
    public static final int MINUS_VALUE = 101;
    public static final int PLUS_PLUS_VALUE = 102;
    public static final int MINUS_MINUS_VALUE = 103;
    public static final int AND_VALUE = 200;
    public static final int OR_VALUE = 201;
    public static final int EQUALS_VALUE = 202;
    public static final int NOT_EQUAL_VALUE = 203;
    public static final int LESS_THAN_VALUE = 204;
    public static final int LESS_THAN_EQUAL_VALUE = 205;
    public static final int MORE_THAN_VALUE = 206;
    public static final int MORE_THAN_EQUAL_VALUE = 207;
    public static final int NOT_VALUE = 208;
    public static final int TIMES_VALUE = 209;
    public static final int DIVIDE_VALUE = 210;

    /**
     * Given an operator, this will return the integer value associated with it for lookups later.
     * {@link ExpressionNode}s store the value, not the operator.
     *
     * @param oo
     * @return
     */
    public int getType(String oo) {
        if (oo.equals(ASSIGNMENT)) return ASSIGNMENT_VALUE;
        // Dyadic operators
        if (oo.equals(AND)) return AND_VALUE;
        if (oo.equals(EQUALS)) return EQUALS_VALUE;
        if (oo.equals(LESS_THAN)) return LESS_THAN_VALUE;
        if (oo.equals(LESS_THAN_EQUAL)) return LESS_THAN_EQUAL_VALUE;
        if (oo.equals(MINUS)) return MINUS_VALUE;
        if (oo.equals(MORE_THAN)) return MORE_THAN_VALUE;
        if (oo.equals(MORE_THAN_EQUAL)) return MORE_THAN_EQUAL_VALUE;
        if (oo.equals(NOT_EQUAL)) return NOT_EQUAL_VALUE;
        if (oo.equals(OR)) return OR_VALUE;
        if (oo.equals(PLUS)) return PLUS_VALUE;
        if (oo.equals(TIMES)) return TIMES_VALUE;
        if (oo.equals(DIVIDE)) return DIVIDE_VALUE;
        // monadic operators.
        if (oo.equals(MINUS_MINUS)) return MINUS_MINUS_VALUE;
        if (oo.equals(NOT)) return NOT_VALUE;
        if (oo.equals(PLUS_PLUS)) return PLUS_PLUS_VALUE;
        return UNKNOWN_VALUE;
    }

    public void evaluate(Dyad dyad) {
        switch (dyad.getOperatorType()) {
            case TIMES_VALUE:
                doDyadTimesOrDivide(dyad, true);
                return;
            case DIVIDE_VALUE:
                doDyadTimesOrDivide(dyad, false);
                return;
            case PLUS_VALUE:
                doDyadPlus(dyad);
                return;
            case MINUS_VALUE:
                doDyadMinus(dyad);
                return;
            case AND_VALUE:
            case OR_VALUE:
                doDyadLogicalOperator(dyad);
                return;
            case EQUALS_VALUE:
            case NOT_EQUAL_VALUE:
                doDyadEqualsOperator(dyad);
                return;
            case LESS_THAN_VALUE:
            case MORE_THAN_VALUE:
            case LESS_THAN_EQUAL_VALUE:
            case MORE_THAN_EQUAL_VALUE:
                doDyadComparisonOperator(dyad);
                return;
            default:
                throw new NotImplementedException("Unknown dyadic operator");
        }
    }

    protected void doDyadComparisonOperator(Dyad dyad) {
        Long left = (Long) dyad.getLeftArgument().evaluate();
        Long right = (Long) dyad.getRightArgument().evaluate();
        Boolean result = null;
        switch (dyad.getOperatorType()) {
            case LESS_THAN_VALUE:
                result = left < right;
                break;
            case LESS_THAN_EQUAL_VALUE:
                result = left <= right;
                break;
            case MORE_THAN_VALUE:
                result = left > right;
                break;
            case MORE_THAN_EQUAL_VALUE:
                result = left >= right;
                break;
        }
        dyad.setResult(result);
        dyad.setEvaluated(true);
        dyad.setResultType(Constant.BOOLEAN_TYPE);

    }

    protected void doDyadEqualsOperator(Dyad dyad) {
        Object left = dyad.getLeftArgument().evaluate();
        Object right = dyad.getRightArgument().evaluate();
        Boolean result = null;
        switch (dyad.getOperatorType()) {
            case EQUALS_VALUE:
                result = left.equals(right);
                break;
            case NOT_EQUAL_VALUE:
                result = !left.equals(right);
                break;

        }
        dyad.setResult(result);
        dyad.setEvaluated(true);
        dyad.setResultType(Constant.BOOLEAN_TYPE);
    }

    protected void doDyadLogicalOperator(Dyad dyad) {
        Boolean left = (Boolean) dyad.getLeftArgument().evaluate();
        Boolean right = (Boolean) dyad.getRightArgument().evaluate();
        Boolean result = null;
        switch (dyad.getOperatorType()) {
            case AND_VALUE:
                result = left && right;
                break;
            case OR_VALUE:
                result = left || right;
                break;
            case EQUALS_VALUE:
                result = left == right;
                break;
            case NOT_EQUAL_VALUE:
                result = left != right;
                break;

        }
        dyad.setResult(result);
        dyad.setEvaluated(true);
        dyad.setResultType(Constant.BOOLEAN_TYPE);
    }

    protected void doDyadMinus(Dyad dyad) {
        Object left = dyad.getArgumments().get(0).evaluate();
        Object right = dyad.getArgumments().get(1).evaluate();
        Object result = null;
        if ((left instanceof Long) && (right instanceof Long)) {
            Long lLeft = (Long) left;
            Long lRight = (Long) right;
            result = lLeft - lRight;
            dyad.setResultType(Constant.LONG_TYPE);
        } else {
            // contract says to remove the string on the right from the one on the
            // left.
            String lString = left.toString();
            String rString = right.toString();
            int ndx = lString.indexOf(rString);
            while (0 <= ndx) {
                lString = lString.substring(0, ndx) + lString.substring(ndx + rString.length());
                ndx = lString.indexOf(rString);
            }
            result = lString;
            dyad.setResultType(Constant.STRING_TYPE);
        }
        dyad.setResult(result);
        dyad.setEvaluated(true);
    }

    protected void doDyadTimesOrDivide(Dyad dyad, boolean doTimes) {
        Object result = null;
        Object left = dyad.getArgumments().get(0).evaluate();
        Object right = dyad.getArgumments().get(1).evaluate();
        if ((left instanceof Long) && (right instanceof Long)) {
            Long lLeft = (Long) left;
            Long lRight = (Long) right;
            if (doTimes) {
                result = lLeft * lRight;
            } else {
                result = lLeft / lRight;
            }
        } else {
            throw new IllegalArgumentException("Error cannot multiply or divide non-numbers");
        }
        dyad.setResult(result);
        dyad.setResultType(Constant.LONG_TYPE);
        dyad.setEvaluated(true);
    }

    protected void doDyadPlus(Dyad dyad) {
        Object result = null;
        Object left = dyad.getArgumments().get(0).evaluate();
        Object right = dyad.getArgumments().get(1).evaluate();
        if ((left instanceof Long) && (right instanceof Long)) {
            Long lLeft = (Long) left;
            Long lRight = (Long) right;
            result = lLeft + lRight;
            dyad.setResultType(Constant.LONG_TYPE);
        } else {
            result = left.toString() + right.toString();
            dyad.setResultType(Constant.STRING_TYPE);
        }
        dyad.setResult(result);
        dyad.setEvaluated(true);
    }


    public void evaluate(Monad monad) {
        switch (monad.getOperatorType()) {
            case NOT_VALUE:
                doMonadNot(monad);
                return;
            case PLUS_PLUS_VALUE:
                doMonadIncOrDec(monad, true);
                return;
            case MINUS_MINUS_VALUE:
                doMonadIncOrDec(monad, false);
                return;
            case MINUS_VALUE:
                doMonadMinus(monad);
                return;
            default:
                throw new NotImplementedException("Unknown monadic operator");
        }

    }

    protected void doMonadIncOrDec(Monad monad, boolean isPlusPlus) {
        VariableNode var = (VariableNode) monad.getArgument();
        Long x = (Long) var.evaluate();
        Long result = null;
        if (isPlusPlus) {
            result = x + 1L;

        } else {
            result = x - 1L;

        }
        monad.setResultType(Constant.LONG_TYPE); // should be redundant
        SymbolTable st = var.getSymbolTable();
        if (monad.isPostFix()) {
            monad.setResult(x); // so the returned result is NOT incremented for postfixes.
        } else {
            monad.setResult(result); // so the returned result is the increment for prefixes
        }
        monad.setEvaluated(true);
        st.setLongValue(var.getVariableReference(), result);
    }


    protected void doMonadNot(Monad monad) {
        Boolean b = (Boolean) monad.getArgument().evaluate();
        monad.setResult(!b);
        monad.setResultType(Constant.BOOLEAN_TYPE);
        monad.setEvaluated(true);
    }

    protected void doMonadMinus(Monad monad) {
        Long value = (Long) monad.getArgument().evaluate();
        monad.setResult(-value);
        monad.setResultType(Constant.LONG_TYPE);
        monad.setEvaluated(true);
    }

    public void evaluate(Nilad nilad) {
        switch (nilad.getOperatorType()) {
            default:
                throw new NotImplementedException("Unknown monadic operator");
        }
    }


}
