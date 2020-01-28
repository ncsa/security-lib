package edu.uiuc.ncsa.qdl.evaluate;


import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class the will evaluate and expression
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:20 PM
 */
public class OpEvaluator extends AbstractFunctionEvaluator {

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

    public int getNumericDigits() {
        return numericDigits;
    }

    public void setNumericDigits(int numericDigits) {
        this.numericDigits = numericDigits;
    }

    int numericDigits = 50; // default precision for decimals.

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

    public void evaluate(Dyad dyad, State state) {
        switch (dyad.getOperatorType()) {
            case TIMES_VALUE:
                doDyadTimesOrDivide(dyad, state, true);
                return;
            case DIVIDE_VALUE:
                doDyadTimesOrDivide(dyad, state, false);
                return;
            case PLUS_VALUE:
                doDyadPlus(dyad, state);
                return;
            case MINUS_VALUE:
                doDyadMinus(dyad, state);
                return;
            case AND_VALUE:
            case OR_VALUE:
                doDyadLogicalOperator(dyad, state);
                return;
            case EQUALS_VALUE:
            case NOT_EQUAL_VALUE:
                doDyadEqualsOperator(dyad, state);
                return;
            case LESS_THAN_VALUE:
            case MORE_THAN_VALUE:
            case LESS_THAN_EQUAL_VALUE:
            case MORE_THAN_EQUAL_VALUE:
                doDyadComparisonOperator(dyad, state);
                return;
            default:
                throw new NotImplementedException("Unknown dyadic operator");
        }
    }


    protected void doDyadComparisonOperator(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                if (!areAllNumbers(objects)) {
                    throw new IllegalArgumentException("Error: only numbers may be compared");
                }
                BigDecimal left = toBD(objects[0]);
                BigDecimal right = toBD(objects[1]);
                int leftToRight = left.compareTo(right);
                fpResult r = new fpResult();
                Boolean result = false;
                switch (dyad.getOperatorType()) {
                    case LESS_THAN_VALUE:
                        result = (leftToRight < 0);
                        break;
                    case LESS_THAN_EQUAL_VALUE:
                        result = (leftToRight < 0) || (leftToRight == 0);
                        break;
                    case MORE_THAN_VALUE:
                        result = (0 < leftToRight);
                        break;
                    case MORE_THAN_EQUAL_VALUE:
                        result = (0 == leftToRight) || (0 < leftToRight);
                        break;
                }
                r.result = result;
                r.resultType = Constant.BOOLEAN_TYPE;

                return r;
            }
        };
        String op = "";
        switch (dyad.getOperatorType()) {
            case LESS_THAN_VALUE:
                op = LESS_THAN;
                break;
            case LESS_THAN_EQUAL_VALUE:
                op = LESS_THAN_EQUAL;
                break;
            case MORE_THAN_VALUE:
                op = MORE_THAN;
                break;
            case MORE_THAN_EQUAL_VALUE:
                op = MORE_THAN_EQUAL;
                break;
        }
        process2(dyad, pointer, op, state);

    }

    protected void doDyadEqualsOperator(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAnyBigDecimals(objects)) {
                    BigDecimal left = toBD(objects[0]);
                    BigDecimal right = toBD(objects[1]);
                    switch (dyad.getOperatorType()) {
                        case EQUALS_VALUE:
                            r.result = bdEquals(left, right);
                            break;
                        case NOT_EQUAL_VALUE:
                            r.result = !bdEquals(left, right);
                            break;
                    } // end switch
                } else {
                    // just do object comparison
                    switch (dyad.getOperatorType()) {
                        case EQUALS_VALUE:
                            r.result = objects[0].equals(objects[1]);
                            break;
                        case NOT_EQUAL_VALUE:
                            r.result = !objects[0].equals(objects[1]);
                            break;
                    }//end switch
                }
                r.resultType = Constant.BOOLEAN_TYPE;
                return r;
            }
        };
        String op = (dyad.getOperatorType() == EQUALS_VALUE ? EQUALS : NOT_EQUAL);
        process2(dyad, pointer, op, state);

    }

    protected void doDyadLogicalOperator(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!areAllBoolean(objects)) {
                    throw new IllegalArgumentException("Error: arguments must be boolean for logical operations");
                }
                Boolean left = (Boolean) objects[0];
                Boolean right = (Boolean) objects[1];
                Boolean result = null;
                switch (dyad.getOperatorType()) {
                    case AND_VALUE:
                        r.result = left && right;
                        break;
                    case OR_VALUE:
                        r.result = left || right;
                        break;
                    case EQUALS_VALUE:
                        r.result = left == right;
                        break;
                    case NOT_EQUAL_VALUE:
                        r.result = left != right;
                        break;
                }
                r.resultType = Constant.BOOLEAN_TYPE;
                return r;

            }
        };
        String op = "";
        switch (dyad.getOperatorType()) {
            case AND_VALUE:
                op = AND;
                break;
            case OR_VALUE:
                op = OR;
                break;
            case EQUALS_VALUE:
                op = EQUALS;
                break;
            case NOT_EQUAL_VALUE:
                op = NOT_EQUAL;
                break;

        }
        process2(dyad, pointer, op, state);
    }

    protected void doDyadMinus(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllNumbers(objects)) {
                    if (areAllLongs(objects)) {
                        r.result = (Long) objects[0] - (Long) objects[1];
                        r.resultType = Constant.LONG_TYPE;
                    } else {
                        BigDecimal left = toBD(objects[0]);
                        BigDecimal right = toBD(objects[1]);
                        r.result = left.subtract(right);
                        r.resultType = Constant.DECIMAL_TYPE;
                    }
                } else {
                    if (!areAllStrings(objects)) {
                        throw new IllegalArgumentException("Error: cannot perform " + MINUS + " on mixed argument types.");
                    }
                    String lString = objects[0].toString();
                    String rString = objects[1].toString();
                    int ndx = lString.indexOf(rString);
                    while (0 <= ndx) {
                        lString = lString.substring(0, ndx) + lString.substring(ndx + rString.length());
                        ndx = lString.indexOf(rString);
                    }
                    r.result = lString;
                    r.resultType = Constant.STRING_TYPE;
                }
                return r;
            }
        };
        process2(dyad, pointer, MINUS, state);
    }

    protected void doDyadTimesOrDivide(Dyad dyad, State state, boolean doTimes) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllNumbers(objects)) {
                    if (areAllLongs(objects)) {
                        if (doTimes) {
                            r.result = (Long) objects[0] * (Long) objects[1];
                        } else {
                            // So dividing a long by a long might result in
                            // a decimal. This checks that and if so, return it,
                            // otherwise it returns a long.
                            Long leftLong = (Long)objects[0];
                            Long rightLong = (Long)objects[1];
                            if(leftLong % rightLong == 0){
                                r.result = (Long) objects[0] / (Long) objects[1];

                            }else{
                                BigDecimal left = new BigDecimal((Long)objects[0]);
                                BigDecimal right = new BigDecimal((Long)objects[1]);
                                try {
                                    BigDecimal out = left.divide(right, getNumericDigits(), RoundingMode.DOWN);
                                    r.result = out;
                                    r.resultType = Constant.DECIMAL_TYPE;

                                }catch (ArithmeticException x){

                                }
                            }
                        }
                        r.resultType = Constant.LONG_TYPE;
                    } else {
                        BigDecimal left = toBD(objects[0]);
                        BigDecimal right = toBD(objects[1]);
                        if (doTimes) {
                            r.result = left.multiply(right);
                        } else {
                            r.result = left.divide(right);
                        }
                        r.resultType = Constant.DECIMAL_TYPE;
                    }
                } else {
                    throw new IllegalArgumentException("Error: operation is not defined for  non-numeric types");
                }
                return r;
            }
        };
        process2(dyad, pointer, doTimes ? TIMES : DIVIDE, state);
    }

    protected void doDyadPlus(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllNumbers(objects)) {
                    if (areAllLongs(objects)) {
                        r.result = (Long) objects[0] + (Long) objects[1];
                        r.resultType = Constant.LONG_TYPE;
                    } else {
                        BigDecimal left = toBD(objects[0]);
                        BigDecimal right = toBD(objects[1]);
                        r.result = left.add(right);
                        r.resultType = Constant.DECIMAL_TYPE;
                    }
                } else {
                    r.result = objects[0].toString() + objects[1].toString();
                    r.resultType = Constant.STRING_TYPE;
                }
                return r;
            }
        };
        process2(dyad, pointer, PLUS, state);
    }


    public void evaluate(Monad monad, State state) {

        switch (monad.getOperatorType()) {
            case NOT_VALUE:
                doMonadNot(monad, state);
                return;
            case PLUS_PLUS_VALUE:
                doMonadIncOrDec(monad, state, true);
                return;
            case MINUS_MINUS_VALUE:
                doMonadIncOrDec(monad, state, false);
                return;
            case MINUS_VALUE:
                doMonadMinus(monad, state);
                return;
            default:
                throw new NotImplementedException("Unknown monadic operator");
        }

    }

    /**
     * NOTE that at this point this only works for single variables -- you can't apply
     * this to a stem.
     *
     * @param monad
     * @param state
     * @param isPlusPlus
     */
    protected void doMonadIncOrDec(Monad monad, State state, boolean isPlusPlus) {
        VariableNode var = (VariableNode) monad.getArgument();
        Object obj = var.evaluate(state);
        if (!isLong(obj)) {
            throw new IllegalArgumentException("Error: " + (isPlusPlus ? PLUS_PLUS : MINUS_MINUS) + " requires an integer value");
        }
        Long x = (Long) var.evaluate(state);
        Long result = null;
        if (isPlusPlus) {
            result = x + 1L;

        } else {
            result = x - 1L;

        }
        monad.setResultType(Constant.LONG_TYPE); // should be redundant
        if (monad.isPostFix()) {
            monad.setResult(x); // so the returned result is NOT incremented for postfixes.
        } else {
            monad.setResult(result); // so the returned result is the increment for prefixes
        }
        monad.setEvaluated(true);
        state.setValue(var.getVariableReference(), result);
    }


    protected void doMonadNot(Monad monad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!isBoolean(objects[0])) {
                    throw new IllegalArgumentException("Error: negation requires a strictly boolean argument not \"" + objects[0] + "\"");
                }
                r.result = !(Boolean) objects[0];
                r.resultType = Constant.BOOLEAN_TYPE;
                return r;
            }
        };
        process1(monad, pointer, NOT, state);
    }

    protected void doMonadMinus(Monad monad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!isNumber(objects[0]))
                    throw new IllegalArgumentException("Error: You can only take the negative of a number");
                if (isLong(objects[0])) {
                    r.result = -(Long) objects[0];
                    r.resultType = Constant.LONG_TYPE;
                } else {
                    BigDecimal x = toBD(objects[0]);
                    r.result = BigDecimal.ZERO.subtract(x);
                    r.resultType = Constant.DECIMAL_TYPE;
                }
                return r;
            }
        };
        process1(monad, pointer, MINUS, state);
    }

    public void evaluate(Nilad nilad, State state) {
        switch (nilad.getOperatorType()) {
            default:
                throw new NotImplementedException("Unknown monadic operator");
        }
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        return false;
    }
}
