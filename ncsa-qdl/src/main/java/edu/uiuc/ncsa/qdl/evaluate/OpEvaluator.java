package edu.uiuc.ncsa.qdl.evaluate;


import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.TreeSet;

/**
 * Class charged with evaluating algebraic expressions.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:20 PM
 */
public class OpEvaluator extends AbstractFunctionEvaluator {

    public static final String ASSIGNMENT = ":=";
    public static final String POWER = "^";
    public static final String TILDE = "~";
    public static final String TIMES = "*";
    public static final String DIVIDE = "/";
    public static final String INTEGER_DIVIDE = "%";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String PLUS_PLUS = "++";
    public static final String MINUS_MINUS = "--";
    public static final String AND = "&&";
    public static final String OR = "||";
    public static final String EQUALS = "==";
    public static final String NOT_EQUAL = "!=";
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_EQUAL = "<=";
    public static final String LESS_THAN_EQUAL2 = "=<";
    public static final String MORE_THAN = ">";
    public static final String MORE_THAN_EQUAL = ">=";
    public static final String MORE_THAN_EQUAL2 = "=>";
    public static final String NOT = "!";
    public static final String DOT = ".";


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
    public static final int POWER_VALUE = 211;
    public static final int INTEGER_DIVIDE_VALUE = 212;
    public static final int TILDE_VALUE = 213;
    public static final int DOT_VALUE = 214;
    /**
     * All Math operators. These are used in function references.
     */
    public static String[] ALL_MATH_OPS = new String[]{
        POWER,
        TILDE,
        TIMES,
        DIVIDE,
        INTEGER_DIVIDE,
        PLUS,
        MINUS,
        AND,
        OR,
        EQUALS,
        NOT_EQUAL,
        LESS_THAN,
        LESS_THAN_EQUAL,
        LESS_THAN_EQUAL2,
        MORE_THAN,
        MORE_THAN_EQUAL,
        MORE_THAN_EQUAL2,
        NOT};

    public boolean isMathOperator(String x){
        for(String op: ALL_MATH_OPS){
            if(op.equals(x)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getFunctionNames() {
        return new String[0];
    }

    public int getNumericDigits() {
        return numericDigits;
    }

    public void setNumericDigits(int numericDigits) {
        this.numericDigits = numericDigits;
    }

    public static int numericDigits = 50; // default precision for decimals.

    /**
     * No listing for these yet since they are not the standard func() pattern, e.g. a < b.
     *
     * @return
     */
    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        return names;
    }

    /**
     * Given an operator, this will return the integer value associated with it for lookups later.
     * {@link ExpressionNode}s store the value, not the operator.
     *
     * @param oo
     * @return
     */
    public int getType(String oo) {
        switch (oo) {
            case ASSIGNMENT:
                return ASSIGNMENT_VALUE;
            case AND:
                return AND_VALUE;
            case EQUALS:
                return EQUALS_VALUE;
            case LESS_THAN:
                return LESS_THAN_VALUE;
            case LESS_THAN_EQUAL:
            case LESS_THAN_EQUAL2:
                return LESS_THAN_EQUAL_VALUE;
            case MINUS:
                return MINUS_VALUE;
            case MORE_THAN:
                return MORE_THAN_VALUE;
            case MORE_THAN_EQUAL:
            case MORE_THAN_EQUAL2:
                return MORE_THAN_EQUAL_VALUE;
            case NOT_EQUAL:
                return NOT_EQUAL_VALUE;
            case OR:
                return OR_VALUE;
            case PLUS:
                return PLUS_VALUE;
            case POWER:
                return POWER_VALUE;
            case TIMES:
                return TIMES_VALUE;
            case DIVIDE:
                return DIVIDE_VALUE;
            case INTEGER_DIVIDE:
                return INTEGER_DIVIDE_VALUE;
            case MINUS_MINUS:
                return MINUS_MINUS_VALUE;
            case NOT:
                return NOT_VALUE;
            case PLUS_PLUS:
                return PLUS_PLUS_VALUE;
            case TILDE:
                return TILDE_VALUE;
            case DOT:
                return DOT_VALUE;
        }
        return UNKNOWN_VALUE;
    }

    public void evaluate(Dyad dyad, State state) {
        switch (dyad.getOperatorType()) {
            case POWER_VALUE:
                doPower(dyad, state);
                return;
            case TIMES_VALUE:
                doDyadTimesOrDivide(dyad, state, true);
                return;
            case INTEGER_DIVIDE_VALUE:
                doDyadIntegerDivide(dyad, state);
                return;
            case DIVIDE_VALUE:
                doDyadTimesOrDivide(dyad, state, false);
                return;
            case PLUS_VALUE:
                doDyadPlus(dyad, state);
                return;
            case TILDE_VALUE:
                doTilde(dyad, state);
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

    protected void doTilde(Dyad dyad, State state) {
        Object obj0 = dyad.evalArg(0, state);
        Object obj1 = dyad.evalArg(1, state);
        if((obj0 instanceof QDLNull) || (obj1 instanceof QDLNull)){
            throw new IllegalArgumentException("Error: cannot do a union a null");
        }
        StemVariable stem0 = null;
        StemVariable stem1 = null;

        if(obj0 instanceof StemVariable){
            stem0 =  (StemVariable)obj0;
        }else{
            stem0 = new StemVariable();
            stem0.put(0L, obj0);
        }

        if(obj1 instanceof StemVariable){
            stem1 =  (StemVariable)obj1;
        }else{
            stem1 = new StemVariable();
            stem1.put(0L, obj1);
        }

        // NOTE this is done so we don't end up shlepping around references to things and modifying them
        // wihout warning.
  //       stem0 = (StemVariable) stem0.clone();
  //       stem1 = (StemVariable)stem1.clone();
        StemVariable newStem =  stem0.union(stem1);
        dyad.setResult(newStem);
        dyad.setResultType(Constant.STEM_TYPE);
        dyad.setEvaluated(true);
    }

    protected void doDyadIntegerDivide(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!areAllNumbers(objects)) {
                    throw new IllegalArgumentException("operation is not defined for  non-numeric types");
                }
                if (areAllLongs(objects)) {
                    Long leftLong = (Long) objects[0];
                    Long rightLong = (Long) objects[1];
                    r.result = (Long) objects[0] / (Long) objects[1];
                    r.resultType = Constant.LONG_TYPE;
                } else {
                    BigDecimal left = toBD(objects[0]);
                    BigDecimal right = toBD(objects[1]);
                    r.result = left.divide(right, getNumericDigits(), BigDecimal.ROUND_DOWN).longValue();
                    r.resultType = Constant.LONG_TYPE;
                }
                return r;
            }
        };
        process2(dyad, pointer, INTEGER_DIVIDE, state);
    }

    private void doPower(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!isLong(objects[1])) {
                    throw new IllegalArgumentException("Exponentiation requires the second argument be an integer");
                }
                if (areAllNumbers(objects)) {
                    if (areAllLongs(objects)) {
                        Double dd = Math.pow((Long) objects[0], (Long) objects[1]);
                        // wee bit of conversion since this only returns doubles, not longs
                        r.result = dd.longValue();
                        r.resultType = Constant.LONG_TYPE;
                    } else {
                        BigDecimal left = toBD(objects[0]);
                        int n = ((Long) objects[1]).intValue();
                        r.result = left.pow(n);
                        r.resultType = Constant.DECIMAL_TYPE;
                    }
                } else {
                    throw new IllegalArgumentException("Exponentiation requires a int or decimal be raised to an int power");
                }
                return r;
            }
        };
        process2(dyad, pointer, POWER, state);

    }


    protected void doDyadComparisonOperator(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                if (!areAllNumbers(objects)) {
                    throw new IllegalArgumentException("only numbers may be compared");
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
                            if (objects[0] == null) {
                                r.result = objects[1] == objects[0];
                            } else {
                                // edge case == null
                                if (objects[1] instanceof QDLNull) {
                                    r.result = objects[0] instanceof QDLNull;
                                } else {
                                    r.result = objects[0].equals(objects[1]);
                                }
                            }
                            break;
                        case NOT_EQUAL_VALUE:
                            if (objects[0] == null) {
                                r.result = objects[1] != objects[0];
                            } else {
                                r.result = !objects[0].equals(objects[1]);
                            }
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
                    throw new IllegalArgumentException("arguments must be boolean for logical operations");
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
                        throw new IllegalArgumentException("cannot perform " + MINUS + " on mixed argument types.");
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
                            Long leftLong = (Long) objects[0];
                            Long rightLong = (Long) objects[1];
                            if (leftLong % rightLong == 0) {
                                r.result = leftLong / rightLong;
                            } else {
                                BigDecimal left = new BigDecimal((Long) objects[0]);
                                BigDecimal right = new BigDecimal((Long) objects[1]);
                                try {
                                    BigDecimal out = left.divide(right, getNumericDigits(), RoundingMode.DOWN);
                                    r.result = out;
                                    r.resultType = Constant.DECIMAL_TYPE;

                                } catch (ArithmeticException x) {

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
                            r.result = left.divide(right, getNumericDigits(), BigDecimal.ROUND_DOWN);
                        }
                        r.resultType = Constant.DECIMAL_TYPE;
                    }
                } else {
                    throw new IllegalArgumentException("operation is not defined for  non-numeric types");
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
            case PLUS_VALUE:
                doMonadPlus(monad, state);
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
        if (!(monad.getArgument() instanceof VariableNode)) {
            throw new IllegalArgumentException("You can only " + (isPlusPlus ? "increment" : "decrement") + " a variable.");
        }
        VariableNode var = (VariableNode) monad.getArgument();
        Object obj = var.evaluate(state);
        boolean gotOne = false;
        Object resultValue = null;
        if (isLong(obj)) {
            gotOne = true;
            Long x = (Long) var.evaluate(state);
            if (isPlusPlus) {
                resultValue = x + 1L;
            } else {
                resultValue = x - 1L;
            }
            monad.setResultType(Constant.LONG_TYPE); // should be redundant

        }
        if (isBigDecimal(obj)) {
            gotOne = true;
            monad.setResultType(Constant.DECIMAL_TYPE); // should be redundant
            BigDecimal bd = (BigDecimal) obj;
            BigDecimal one = new BigDecimal("1.0");
            if (isPlusPlus) {
                resultValue = bd.add(one);
            } else {
                resultValue = bd.subtract(one);
            }
            monad.setResultType(Constant.DECIMAL_TYPE); // should be redundant
        }
        if (!gotOne) {
            throw new IllegalArgumentException("" + (isPlusPlus ? PLUS_PLUS : MINUS_MINUS) + " requires a number value");
        }
        if (monad.isPostFix()) {
            monad.setResult(obj); // so the returned result is NOT incremented for postfixes.
        } else {
            monad.setResult(resultValue); // so the returned result is the increment for prefixes
        }
        monad.setEvaluated(true);
        state.setValue(var.getVariableReference(), resultValue);
    }


    protected void doMonadNot(Monad monad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!isBoolean(objects[0])) {
                    throw new IllegalArgumentException("negation requires a strictly boolean argument not '" + objects[0] + "'");
                }
                r.result = !(Boolean) objects[0];
                r.resultType = Constant.BOOLEAN_TYPE;
                return r;
            }
        };
        process1(monad, pointer, NOT, state);
    }

    /**
     * This will evaluate the expression and take its opposite. This is because the parser does not differentiate
     * between -3^4 and (-3)^4, turing the first of each into a single monad. It works right if there
     * are parentheses, but this is possible a thronier issue to fix than we want now. Best to
     * ket it do this since algebraic operations work as expected.
     *
     * @param monad
     * @param state
     * @param sign
     */
    protected void doUnaryPlusMinus(Monad monad, State state, Long sign) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                switch (Constant.getType(objects[0])) {
                    case Constant.LONG_TYPE:
                        r.result = sign * (Long) objects[0];
                        r.resultType = Constant.LONG_TYPE;
                        break;
                    case Constant.DECIMAL_TYPE:
                        BigDecimal x = toBD(objects[0]);
                        r.result = sign < 0 ? x.negate() : x;
                        r.resultType = Constant.DECIMAL_TYPE;
                        break;
                    case Constant.STRING_TYPE:
                        if (sign > 0) {
                            r.result = objects[0];
                        } else {
                            r.result = "";
                        }
                        r.resultType = Constant.STRING_TYPE;
                        break;
                    default:
                        throw new IllegalArgumentException("You can only take the negative of a number or string");

                }
                return r;
            }
        };
        process1(monad, pointer, sign == 1 ? PLUS : MINUS, state);
    }

    protected void doMonadPlus(Monad monad, State state) {
        doUnaryPlusMinus(monad, state, 1L);
    }

    protected void doMonadMinus(Monad monad, State state) {
        doUnaryPlusMinus(monad, state, -1L);
    }

    public void evaluate(Nilad nilad, State state) {
        switch (nilad.getOperatorType()) {
            default:
                throw new NotImplementedException("Unknown niladic operator");
        }
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        return false;
    }
}
