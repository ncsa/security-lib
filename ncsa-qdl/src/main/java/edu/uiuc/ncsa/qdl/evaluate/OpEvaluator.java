package edu.uiuc.ncsa.qdl.evaluate;


import edu.uiuc.ncsa.qdl.exceptions.BadArgException;
import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.exceptions.QDLExceptionWithTrace;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.types.Types;
import edu.uiuc.ncsa.qdl.variables.*;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.types.Types.NULL;
import static edu.uiuc.ncsa.qdl.variables.Constant.*;

/**
 * Class charged with evaluating algebraic expressions.
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:20 PM
 */
public class OpEvaluator extends AbstractEvaluator {
    // reference for unicode and other characters: https://en.wikipedia.org/wiki/Mathematical_operators_and_symbols_in_Unicode
    public static final String AND = "&&";
    public static final String AND2 = "∧"; // unicode 2227
    public static final String ASSIGNMENT = ":=";
    public static final String CEILING = "⌈";  // unicode 2308
    public static final String DIVIDE = "/";
    public static final String DIVIDE2 = "÷"; // unicode f7
    public static final String EQUALS = "==";
    public static final String EQUALS2 = "≡";  // unicode 2261
    public static final String FLOOR = "⌊";  // unicode 230a
    public static final String INTEGER_DIVIDE = "%";
    public static final String SYMMETRIC_DIFFERENCE = "∆"; // unicode 2206
    public static final String LESS_THAN = "<";
    public static final String LESS_THAN_EQUAL = "<=";
    public static final String LESS_THAN_EQUAL2 = "=<";
    public static final String LESS_THAN_EQUAL3 = "≤"; // unicode 2264
    public static final String MINUS = "-";
    public static final String MINUS2 = "¯";  // unicode 00af
    public static final String MINUS_MINUS = "--";
    public static final String MORE_THAN = ">";
    public static final String MORE_THAN_EQUAL = ">=";
    public static final String MORE_THAN_EQUAL2 = "=>";
    public static final String MORE_THAN_EQUAL3 = "≥"; // unicode 2265
    public static final String NOT = "!";
    public static final String NOT2 = "¬"; // unicode ac
    public static final String NOT_EQUAL = "!=";
    public static final String NOT_EQUAL2 = "≠"; // unicode 2260
    public static final String OR = "||";
    public static final String OR2 = "∨"; // unicode 2228
    public static final String PLUS = "+";
    public static final String PLUS2 = "⁺"; // unciode 207a unary plus
    public static final String PLUS_PLUS = "++";
    public static final String POWER = "^";
    public static final String UNION = "∪"; //unicode 2229
    public static final String UNION_2 = "\\/";
    public static final String INTERSECTION = "∩"; //unicode 222a
    public static final String INTERSECTION_2 = "/\\";
    public static final String TILDE = "~";
    public static final String TILDE_STILE = "~|";
    public static final String TILDE_STILE2 = "≁"; // unicode 2241
    public static final String TIMES = "*";
    public static final String TIMES2 = "×"; // unicode d7
    public static final String DOT = ".";
    public static final String REGEX_MATCH = "=~";
    public static final String REGEX_MATCH2 = "≈";
    public static final String TO_SET = "⊢";
    public static final String TO_SET2 = "|>";
    public static final String EPSILON = "∈";
    public static final String EPSILON_NOT = "∉";
    public static final String IS_A = "<<";


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
    public static final int TILDE_STILE_VALUE = 215;
    public static final int REGEX_MATCH_VALUE = 216;
    public static final int UNION_VALUE = 217;
    public static final int INTERSECTION_VALUE = 218;
    public static final int FLOOR_VALUE = 219;
    public static final int CEILING_VALUE = 220;
    public static final int TO_SET_VALUE = 221;
    public static final int EPSILON_VALUE = 222;
    public static final int EPSILON_NOT_VALUE = 223;
    public static final int IS_A_VALUE = 224;

    /**
     * All Math operators. These are used in function references.
     */
    public static String[] ALL_MATH_OPS = new String[]{
            EPSILON, EPSILON_NOT, IS_A,
            TO_SET, TO_SET2,
            FLOOR, CEILING,
            UNION, UNION_2, INTERSECTION, INTERSECTION_2,
            POWER,
            TILDE, TILDE_STILE, TILDE_STILE2,
            TIMES,
            TIMES2,
            DIVIDE,
            DIVIDE2,
            INTEGER_DIVIDE, SYMMETRIC_DIFFERENCE,
            PLUS, PLUS2,
            MINUS, MINUS2,
            AND, AND2,
            OR,
            OR2,
            EQUALS,
            EQUALS2,
            NOT_EQUAL,
            NOT_EQUAL2,
            LESS_THAN,
            LESS_THAN_EQUAL,
            LESS_THAN_EQUAL2,
            MORE_THAN,
            MORE_THAN_EQUAL,
            MORE_THAN_EQUAL2,
            MORE_THAN_EQUAL3,
            NOT, NOT2,
            REGEX_MATCH, REGEX_MATCH2};

    public static ArrayList<String> ALL_MONADS = new ArrayList<>(Arrays.asList(new String[]{
            NOT, NOT2, MINUS, MINUS2, PLUS, PLUS2, TILDE, PLUS_PLUS, MINUS_MINUS
    }));
    public static ArrayList<String> ONLY_MONADS = new ArrayList<>(Arrays.asList(new String[]{
            NOT, NOT2, PLUS_PLUS, MINUS_MINUS, FLOOR, CEILING, TO_SET, TO_SET2
    }));
    int[] monadOnlyArg = new int[]{1};
    int[] dyadOnlyArg = new int[]{2};
    int[] monadAndDyadArg = new int[]{1, 2};

    /**
     * Return the arg counts for an operator.
     *
     * @param name
     * @return
     */
    public int[] getArgCount(String name) {
        // special case floor and ceiling since the parser intercepts them and replaces them with
        // their function.
        if (ONLY_MONADS.contains(name)) {
            return monadOnlyArg;
        }
        if (!ALL_MONADS.contains(name)) {
            return dyadOnlyArg;
        }
        return monadAndDyadArg;
    }

    @Override
    public String getNamespace() {
        throw new NotImplementedException("namespaces for operators not supported");
    }

    public boolean isMathOperator(String x) {
        for (String op : ALL_MATH_OPS) {
            if (op.equals(x)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String[] getFunctionNames() {
        return new String[0];
    }

    static MathContext mathContext;

    public static MathContext getMathContext() {
        if (mathContext == null) {
            mathContext = new MathContext(getNumericDigits(), RoundingMode.HALF_EVEN);
        }
        return mathContext;
    }

    public static int getNumericDigits() {
        return numericDigits;
    }

    public static void setNumericDigits(int newNumericDigits) {
        numericDigits = newNumericDigits;
        mathContext = new MathContext(numericDigits);
        TMathEvaluator.setPi(null); // zero it out, force recompute at new precision
        TMathEvaluator.setNaturalLogBase(null);
    }

    public static int numericDigits = 15; // default precision for decimals.

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
            case IS_A:
                return IS_A_VALUE;
            case EPSILON:
                return EPSILON_VALUE;
            case EPSILON_NOT:
                return EPSILON_NOT_VALUE;
            case TO_SET:
            case TO_SET2:
                return TO_SET_VALUE;
            case FLOOR:
                return FLOOR_VALUE;
            case CEILING:
                return CEILING_VALUE;
            case UNION:
            case UNION_2:
                return UNION_VALUE;
            case INTERSECTION:
            case INTERSECTION_2:
                return INTERSECTION_VALUE;
            case ASSIGNMENT:
                return ASSIGNMENT_VALUE;
            case AND:
            case AND2:
                return AND_VALUE;
            case EQUALS:
            case EQUALS2:
                return EQUALS_VALUE;
            case LESS_THAN:
                return LESS_THAN_VALUE;
            case LESS_THAN_EQUAL:
            case LESS_THAN_EQUAL2:
            case LESS_THAN_EQUAL3:
                return LESS_THAN_EQUAL_VALUE;
            case MINUS:
            case MINUS2:
                return MINUS_VALUE;
            case MORE_THAN:
                return MORE_THAN_VALUE;
            case MORE_THAN_EQUAL:
            case MORE_THAN_EQUAL2:
            case MORE_THAN_EQUAL3:
                return MORE_THAN_EQUAL_VALUE;
            case NOT_EQUAL:
            case NOT_EQUAL2:
                return NOT_EQUAL_VALUE;
            case OR:
            case OR2:
                return OR_VALUE;
            case PLUS:
            case PLUS2:
                return PLUS_VALUE;
            case POWER:
                return POWER_VALUE;
            case TIMES:
            case TIMES2:
                return TIMES_VALUE;
            case DIVIDE:
            case DIVIDE2:
                return DIVIDE_VALUE;
            case INTEGER_DIVIDE:
            case SYMMETRIC_DIFFERENCE:
                return INTEGER_DIVIDE_VALUE;
            case MINUS_MINUS:
                return MINUS_MINUS_VALUE;
            case NOT:
            case NOT2:
                return NOT_VALUE;
            case PLUS_PLUS:
                return PLUS_PLUS_VALUE;
            case TILDE_STILE:
                return TILDE_STILE_VALUE;
            case TILDE:
                return TILDE_VALUE;
            case DOT:
                return DOT_VALUE;
            case REGEX_MATCH:
            case REGEX_MATCH2:
                return REGEX_MATCH_VALUE;
        }
        return UNKNOWN_VALUE;
    }

    public void evaluate(Dyad dyad, State state) {
        try {
            evaluate2(dyad, state);
        } catch (QDLException q) {
            throw q;
        } catch (Throwable t) {
            QDLExceptionWithTrace qq = new QDLExceptionWithTrace(t, dyad);
            throw qq;
        }
    }

    public void evaluate2(Dyad dyad, State state) {
        switch (dyad.getOperatorType()) {
            case IS_A_VALUE:
                doIsA(dyad, state);
                return;
            case EPSILON_VALUE:
                doMembership(dyad, state, true);
                return;
            case EPSILON_NOT_VALUE:
                doMembership(dyad, state, false);
                return;
            case UNION_VALUE:
            case INTERSECTION_VALUE:
                doSetUnionOrInteresection(dyad, state);
                return;
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
            case TILDE_STILE_VALUE:
                doJoin(dyad, state);
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
            case REGEX_MATCH_VALUE:
                doRegexMatch(dyad, state);
                return;
            default:
                throw new NotImplementedException("Unknown dyadic operator " + dyad.getOperatorType());
        }
    }

    private void doIsA(Dyad dyad, State state) {
      doIsA1(dyad, state);
    }
    private void doIsA1(Dyad dyad, State state) {
         Object lhs = dyad.evalArg(0, state);

        if (!(dyad.getRightArgument() instanceof VariableNode)) {
            List<String> source =dyad.getRightArgument().getSourceCode();
            String text;
            if(source.size()==1){
                text = source.get(0);
            }else{
                text = source.toString();
            }
            throw new BadArgException("unknown type '" + text + "'", dyad.getRightArgument());
        }
        Object obj2 = dyad.evalArg(1,state); // If the variable resolves to something, blow up
        if(obj2 != null){
            throw new BadArgException("you must supply a type, not a value ", dyad.getRightArgument());
        }
        String typeName = ((VariableNode)dyad.getRightArgument()).getVariableReference();
        boolean x = false;
        switch (typeName) {
            case NULL:
                x = lhs instanceof QDLNull;
                break;
            case Types.BOOLEAN:
                x = lhs instanceof Boolean;
                break;
            case Types.STRING:
                x = lhs instanceof String;
                break;
            case Types.NUMBER:
                x = (lhs instanceof Long) || (lhs instanceof BigDecimal);
                break;
            case Types.INTEGER:
                x = lhs instanceof Long;
                break;
            case Types.DECIMAL:
                x = lhs instanceof BigDecimal;
                break;
            case Types.STEM:
                x = lhs instanceof QDLStem;
                break;
            case Types.LIST:
                x = (lhs instanceof QDLStem) && ((QDLStem) lhs).isList();
                break;
            case Types.SET:
                x = lhs instanceof QDLSet;
                break;
            default:
                throw new BadArgException("unkown type", dyad.getRightArgument());
        }
        dyad.setResult( x ? Boolean.TRUE : Boolean.FALSE);
        dyad.setResultType(BOOLEAN_TYPE);
        dyad.setEvaluated(true);

    }

    private void doMembership(Dyad dyad, State state, boolean isMember) {
        Polyad polyad;
        polyad = new Polyad(StemEvaluator.HAS_VALUE);
        polyad.setTokenPosition(dyad.getTokenPosition());
        polyad.setSourceCode(dyad.getSourceCode());
        polyad.addArgument(dyad.getLeftArgument());
        polyad.addArgument(dyad.getRightArgument());
        if (!isMember) {
            Monad monad = new Monad(OpEvaluator.NOT_VALUE, false);
            monad.setArgument(polyad);
            monad.setTokenPosition(polyad.getTokenPosition());
            monad.setSourceCode(polyad.getSourceCode());
            state.getOpEvaluator().evaluate(monad, state);
            dyad.setResult(monad.getResult());
            dyad.setResultType(monad.getResultType());
            dyad.setEvaluated(monad.isEvaluated());
            return;
        }
        state.getMetaEvaluator().evaluate(polyad, state);
        dyad.setResult(polyad.getResult());
        dyad.setResultType(polyad.getResultType());
        dyad.setEvaluated(polyad.isEvaluated());
    }

    // '[a-zA-Z]{3}' =~ 'aBc'

    /**
     * Contract is expression regex ≈ expression returns true if it matches expression as a string.
     *
     * @param dyad
     * @param state
     */
    protected void doRegexMatch(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllSets(objects)) {
                    throw new QDLExceptionWithTrace(REGEX_MATCH + " not defined on sets.", dyad.getLeftArgument());
                }

                if (!isString(objects[0])) {
                    throw new QDLExceptionWithTrace(REGEX_MATCH + " requires a regular expression as its left argument", dyad.getLeftArgument());
                }
                String regex = objects[0].toString();
                String ob = objects[1].toString();
                r.result = new Boolean(ob.matches(regex));
                r.resultType = BOOLEAN_TYPE;
                return r;
            }
        };
        process2(dyad, pointer, REGEX_MATCH, state);
    }

    private void doJoin(Dyad dyad, State state) {
        Polyad joinPolyad = new Polyad(StemEvaluator.JOIN);
        joinPolyad.getArguments().add(dyad.getLeftArgument());
        joinPolyad.getArguments().add(dyad.getRightArgument());
        joinPolyad.getArguments().add(new ConstantNode(-1L, LONG_TYPE));
        state.getMetaEvaluator().evaluate(joinPolyad, state);
        dyad.setResult(joinPolyad.getResult());
        dyad.setResultType(joinPolyad.getResultType());
        dyad.setEvaluated(true);
    }

    protected void doTilde(Dyad dyad, State state) {
        Object obj1 = dyad.evalArg(1, state);
        if (isSet(obj1)) {
            QDLSet set = (QDLSet) obj1;
            QDLStem outStem = new QDLStem();
            // special case. If this is a unary ~, then the first argument is
            // ignored.
            if (dyad.isUnary()) {
                dyad.setResult(set.toStem());
                dyad.setResultType(STEM_TYPE);
                dyad.setEvaluated(true);
                return;

            }
            // so they are trying to append the list to an existing stem.
            // In that case it becomes another entry
            Object obj0 = dyad.evalArg(0, state);
            if ((obj0 instanceof QDLNull)) {
                throw new QDLExceptionWithTrace("cannot do a union a null", dyad.getLeftArgument());

            }
            if ((obj1 instanceof QDLNull)) {
                throw new QDLExceptionWithTrace("cannot do a union a null", dyad.getRightArgument());
            }
            QDLStem stem0;
            if (obj0 instanceof QDLStem) {
                stem0 = (QDLStem) obj0;
            } else {
                stem0 = new QDLStem();
                stem0.put(0L, obj0);
            }
            outStem = outStem.union(stem0); // copy over elements
            long index = -1L;
            SparseEntry sparseEntry;
            if (!outStem.getQDLList().isEmpty()) {
                sparseEntry = outStem.getQDLList().last();
                index = sparseEntry.index;
            }
            SparseEntry newEntry = new SparseEntry(index + 1, set);

            outStem.getQDLList().add(newEntry);
            dyad.setResult(outStem);
            dyad.setResultType(STEM_TYPE);
            dyad.setEvaluated(true);
            return;
        }
        Object obj0 = dyad.evalArg(0, state);
        if ((obj0 instanceof QDLNull)) {
            throw new QDLExceptionWithTrace("cannot do union on a null", dyad.getLeftArgument());

        }
        if ((obj0 instanceof QDLNull)) {
            throw new QDLExceptionWithTrace("cannot do union on a null", dyad.getRightArgument());
        }

        QDLStem stem0 = null;
        QDLStem stem1 = null;

        if (obj0 instanceof QDLStem) {
            stem0 = (QDLStem) obj0;
        } else {
            stem0 = new QDLStem();
            stem0.put(0L, obj0);
        }


        if (obj1 instanceof QDLStem) {
            stem1 = (QDLStem) obj1;
        } else {
            stem1 = new QDLStem();
            stem1.put(0L, obj1);
        }
        // NOTE this is done so we don't end up shlepping around references to things and modifying them
        // without warning.
        //       stem0 = (StemVariable) stem0.clone();
        //       stem1 = (StemVariable)stem1.clone();
        QDLStem newStem = stem0.union(stem1);
        dyad.setResult(newStem);
        dyad.setResultType(STEM_TYPE);
        dyad.setEvaluated(true);
    }

    protected void doDyadIntegerDivide(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllSets(objects)) {
                    QDLSet leftSet = (QDLSet) objects[0];
                    QDLSet rightSet = (QDLSet) objects[1];
                    r.result = leftSet.symmetricDifference(rightSet);
                    r.resultType = SET_TYPE;
                    return r;
                }
                if (!areAllNumbers(objects)) {
                    throw new QDLExceptionWithTrace("division is not defined for  non-numeric types", dyad.getLeftArgument());
                }
                if (areAllLongs(objects)) {
                    r.result = (Long) objects[0] / (Long) objects[1];
                    r.resultType = LONG_TYPE;
                } else {
                    BigDecimal left = toBD(objects[0]);
                    BigDecimal right = toBD(objects[1]);
                    BigDecimal rr = null;
                    try {
                        rr = left.divideToIntegralValue(right, OpEvaluator.getMathContext());
                    } catch (ArithmeticException ax0) {
                        throw new QDLExceptionWithTrace("Insufficient precision to divide. Please increase " + MathEvaluator.NUMERIC_DIGITS, dyad.getRightArgument());
                    }
                    try {
                        r.result = rr.longValueExact();
                        r.resultType = LONG_TYPE;
                        return r;
                    } catch (ArithmeticException ax) {

                    }
                    r.result = rr;
                    r.resultType = DECIMAL_TYPE;
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
                if (areAllSets(objects)) {
                    throw new QDLExceptionWithTrace(POWER + " not defined for sets.", dyad);
                }
                if (areAllNumbers(objects)) {
                    boolean doBigD = isBigDecimal(objects[0]) || isBigDecimal(objects[1]);
                    BigDecimal left = toBD(objects[0]);
                    BigDecimal result;
                    if (isLong(objects[1])) {
                        result = ch.obermuhlner.math.big.BigDecimalMath.pow(left, (Long) objects[1], getMathContext());
                    } else {
                        result = ch.obermuhlner.math.big.BigDecimalMath.pow(left, (BigDecimal) objects[1], getMathContext());
                    }
                    if (!doBigD) {
                        try {
                            r.result = result.longValueExact();
                            r.resultType = LONG_TYPE;
                            return r;
                        } catch (ArithmeticException ax) {
                        }
                    }
                    r.result = result;
                    r.resultType = DECIMAL_TYPE;

                } else {
                    throw new QDLExceptionWithTrace("Exponentiation requires a int or decimal be raised to an int power", dyad.getLeftArgument());
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
                fpResult r = new fpResult();
                if (areAllSets(objects)) {
                    QDLSet leftSet = (QDLSet) objects[0];
                    QDLSet rightSet = (QDLSet) objects[1];
                    switch (dyad.getOperatorType()) {
                        case LESS_THAN_VALUE:
                            r.result = leftSet.isSubsetOf(rightSet) && (leftSet.size() != rightSet.size());
                            r.resultType = BOOLEAN_TYPE;
                            break;
                        case LESS_THAN_EQUAL_VALUE:
                            r.result = leftSet.isSubsetOf(rightSet);
                            r.resultType = BOOLEAN_TYPE;
                            break;
                        case MORE_THAN_VALUE:
                            r.result = rightSet.isSubsetOf(leftSet) && (leftSet.size() != rightSet.size());
                            r.resultType = BOOLEAN_TYPE;
                            break;
                        case MORE_THAN_EQUAL_VALUE:
                            r.result = rightSet.isSubsetOf(leftSet);
                            r.resultType = BOOLEAN_TYPE;
                            break;
                    }
                    return r;
                }
                if (areAllStrings(objects)) {
                    String left = (String) objects[0];
                    String right = (String) objects[1];
                    r.resultType = BOOLEAN_TYPE;

                    switch (dyad.getOperatorType()) {
                        case LESS_THAN_VALUE:
                            r.result = -1 < right.indexOf(left) && left.length() < right.length();
                            break;
                        case LESS_THAN_EQUAL_VALUE:
                            r.result = -1 < right.indexOf(left);
                            break;
                        case MORE_THAN_VALUE:
                            r.result = -1 < left.indexOf(right) && right.length() < left.length();
                            break;
                        case MORE_THAN_EQUAL_VALUE:
                            r.result = -1 < left.indexOf(right);
                            break;
                    }
                    return r;
                }
                if (!areAllNumbers(objects)) {
                    throw new QDLExceptionWithTrace("only numbers may be compared", dyad.getLeftArgument());
                }
                BigDecimal left = toBD(objects[0]);
                BigDecimal right = toBD(objects[1]);
                int leftToRight = left.compareTo(right);
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
                r.resultType = BOOLEAN_TYPE;

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

                if (areAllSets(objects)) {
                    QDLSet leftSet = (QDLSet) objects[0];
                    QDLSet rightSet = (QDLSet) objects[1];
                    switch (dyad.getOperatorType()) {
                        case EQUALS_VALUE:
                            r.result = leftSet.isEqualTo(rightSet);
                            break;
                        case NOT_EQUAL_VALUE:
                            r.result = !leftSet.isEqualTo(rightSet);
                            break;
                    }

                    r.resultType = BOOLEAN_TYPE;
                    return r;
                }
                if (isSet(objects[0])) {
                    QDLSet leftSet = (QDLSet) objects[0];
                    r.result = leftSet.contains(objects[1]);
                    r.resultType = BOOLEAN_TYPE;
                    return r;
                }
                if (isSet(objects[1])) {
                    QDLSet set = (QDLSet) objects[1];
                    r.result = set.contains(objects[0]);
                    r.resultType = BOOLEAN_TYPE;
                    return r;
                }

                if (areAnyBigDecimals(objects)) {
                    BigDecimal left;
                    BigDecimal right;
                    try {
                        left = toBD(objects[0]);
                        right = toBD(objects[1]);
                    } catch (IllegalArgumentException iax) {
                        // means that something cannot be converted to a big decimal
                        switch (dyad.getOperatorType()) {
                            case EQUALS_VALUE:
                                r.result = Boolean.FALSE;
                                break;
                            case NOT_EQUAL_VALUE:
                                r.result = Boolean.TRUE;
                                break;
                        }
                        r.resultType = BOOLEAN_TYPE;
                        return r;
                    }
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
                r.resultType = BOOLEAN_TYPE;
                return r;
            }
        };
        String op = (dyad.getOperatorType() == EQUALS_VALUE ? EQUALS : NOT_EQUAL);
        process2(dyad, pointer, op, state);

    }

    protected void doSetUnionOrInteresection(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (!areAllSets(objects)) {
                    throw new QDLExceptionWithTrace("Set operations require only sets", dyad.getLeftArgument());
                }
                QDLSet leftSet = (QDLSet) objects[0];
                QDLSet rightSet = (QDLSet) objects[1];
                switch (dyad.getOperatorType()) {
                    case INTERSECTION_VALUE:
                        r.result = leftSet.intersection(rightSet);
                        r.resultType = SET_TYPE;
                        break;
                    case UNION_VALUE:
                        r.result = leftSet.union(rightSet);
                        r.resultType = SET_TYPE;
                        break;
                }
                return r;


            }
        };
        // Figure out the operator from the type to pass along
        String op = "";
        switch (dyad.getOperatorType()) {
            case UNION_VALUE:
                op = UNION;
                break;
            case INTERSECTION_VALUE:
                op = INTERSECTION;
                break;
        }
        process2(dyad, pointer, op, state);

    }

    protected void doDyadLogicalOperator(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                if (areAllSets(objects)) {
                    QDLSet leftSet = (QDLSet) objects[0];
                    QDLSet rightSet = (QDLSet) objects[1];
                    switch (dyad.getOperatorType()) {
                        case EQUALS_VALUE:
                            r.result = leftSet.isEqualTo(rightSet);
                            r.resultType = BOOLEAN_TYPE;
                            break;
                        case NOT_EQUAL_VALUE:
                            r.result = !leftSet.isEqualTo(rightSet);
                            r.resultType = BOOLEAN_TYPE;
                            break;
                    }
                    return r;

                }
                if (!areAllBoolean(objects)) {
                    throw new QDLExceptionWithTrace("arguments must be boolean for logical operations", dyad.getLeftArgument());
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
                r.resultType = BOOLEAN_TYPE;
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
                if (areAllSets(objects)) {
                    throw new QDLExceptionWithTrace(MINUS + " not defined on sets. Did you mean difference (" + DIVIDE + ")?", dyad);
                }
                if (areAllNumbers(objects)) {
                    if (areAllLongs(objects)) {
                        try {
                            r.result = Math.subtractExact((Long) objects[0], (Long) objects[1]);
                            r.resultType = LONG_TYPE;
                            return r;
                        } catch (ArithmeticException arithmeticException) {
                            // fall through to big decimal case
                        }
                    }

                    BigDecimal left = toBD(objects[0]);
                    BigDecimal right = toBD(objects[1]);
                    r.result = left.subtract(right);
                    r.resultType = DECIMAL_TYPE;
                } else {
                    if (!areAllStrings(objects)) {
                        throw new QDLExceptionWithTrace("cannot perform " + MINUS + " on mixed argument types.", dyad.getLeftArgument());
                    }
                    String lString = objects[0].toString();
                    String rString = objects[1].toString();
                    int ndx = lString.indexOf(rString);
                    while (0 <= ndx) {
                        lString = lString.substring(0, ndx) + lString.substring(ndx + rString.length());
                        ndx = lString.indexOf(rString);
                    }
                    r.result = lString;
                    r.resultType = STRING_TYPE;
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
                if (areAllSets(objects)) {
                    if (doTimes) {
                        throw new QDLExceptionWithTrace(TIMES + " not defined on sets.", dyad.getLeftArgument());
                    }
                    QDLSet leftSet = (QDLSet) objects[0];
                    QDLSet rightSet = (QDLSet) objects[1];
                    r.result = leftSet.difference(rightSet);
                    r.resultType = SET_TYPE;
                    return r;
                }
                if (areAllNumbers(objects)) {
                    if (doTimes) {
                        if (areAllLongs(objects)) {
                            try {
                                r.result = Math.multiplyExact((Long) objects[0], (Long) objects[1]);
                                r.resultType = LONG_TYPE;
                                return r;
                            } catch (ArithmeticException arithmeticException) {
                                // fall through to BD case
                            }
                        }
                        BigDecimal left = toBD(objects[0]);
                        BigDecimal right = toBD(objects[1]);
                        BigDecimal rr = left.multiply(right);
                        try {
                            r.result = rr.longValueExact();
                            r.resultType = LONG_TYPE;

                        } catch (ArithmeticException arithmeticException) {
                            r.result = rr;
                            r.resultType = DECIMAL_TYPE;
                        }
                        return r;
                    } else {
                        BigDecimal left = toBD(objects[0]);
                        BigDecimal right = toBD(objects[1]);
                        BigDecimal res = left.divide(right, getNumericDigits(), BigDecimal.ROUND_DOWN);
                        if (MathEvaluator.isIntegerValue(res)) {
                            // try to turn it into an integer
                            try {
                                r.result = res.longValueExact();
                                r.resultType = LONG_TYPE;
                                return r;
                            } catch (ArithmeticException arithmeticException) {
                                // so it cannot eb turned into a long value for whatever reason
                            }
                        }
                        r.result = res;
                    }

                    r.resultType = DECIMAL_TYPE;
                    return r;

                } else {
                    long count = 0;
                    String arg = "";
                    String tempOutput = "";
                    boolean gotOne = false;
                    if (doTimes && isLong(objects[0]) && isString(objects[1])) {
                        count = (Long) objects[0];
                        arg = (String) objects[1];
                        if (count < 0) {
                            throw new QDLExceptionWithTrace("multiplication is undefined for strings and  negative integers", dyad.getLeftArgument());
                        }
                        gotOne = 0 <= count;
                    }
                    if (doTimes && isLong(objects[1]) && isString(objects[0])) {
                        arg = (String) objects[0];
                        count = (Long) objects[1];
                        if (count < 0) {
                            throw new QDLExceptionWithTrace("multiplication is undefined for strings and  negative integers", dyad.getRightArgument());
                        }
                        gotOne = 0 <= count;
                    }

                    if (gotOne) {
                        r.resultType = STRING_TYPE;
                        if (count == 0) {
                            r.result = "";
                            return r;
                        }
                        for (long i = 0; i < count; i++) {
                            tempOutput = tempOutput + arg;
                        }
                        r.result = tempOutput;
                        return r;
                    }
                    throw new QDLExceptionWithTrace((doTimes ? "multiplication" : "division") + " is undefined for  non-numeric types", dyad);
                }
            }
        };
        try {
            process2(dyad, pointer, doTimes ? TIMES : DIVIDE, state);
        } catch (ArithmeticException ax) {
            if (ax.getMessage().equals("/ by zero")) {
                ax = new ArithmeticException("divide by zero");
            }
            throw ax;
        }
    }


    // maximum long value is 9223372036854775807
    // almost max is 9223372036854775806

    /**
     * For dyadic plus.
     *
     * @param dyad
     * @param state
     */
    protected void doDyadPlus(Dyad dyad, State state) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                // At this point, only scalars should ever get passed here as arguments.
                fpResult r = new fpResult();
                if (areAllSets(objects)) {
                    throw new QDLExceptionWithTrace(PLUS + " not defined on sets. Did you mean union (" + AND + ")?", dyad);
                }
                if (areAllNumbers(objects)) {
                    if (areAllLongs(objects)) {
                        try {
                            r.result = Math.addExact((Long) objects[0], (Long) objects[1]);
                            r.resultType = LONG_TYPE;
                            return r;
                        } catch (ArithmeticException arithmeticException) {
                            // fall through
                        }
                    }

                    BigDecimal left = toBD(objects[0]);
                    BigDecimal right = toBD(objects[1]);
                    r.result = left.add(right);
                    r.resultType = DECIMAL_TYPE;
                    return r;
                }

                if (!isStem(objects[1])) {
                    r.result = objects[0].toString() + objects[1].toString();
                    r.resultType = STRING_TYPE;
                    return r;
                }
                // This is a stem
                throw new QDLExceptionWithTrace("stem encountered in scalar operation", dyad);
            }
        };
        process2(dyad, pointer, PLUS, state);
    }


    public void evaluate(Monad monad, State state) {
        try {
            evaluate2(monad, state);
        } catch (QDLException q) {
            throw q;
        } catch (Throwable t) {
            QDLExceptionWithTrace qq = new QDLExceptionWithTrace(t, monad);
            throw qq;
        }
    }

    public void evaluate2(Monad monad, State state) {

        switch (monad.getOperatorType()) {
            case TO_SET_VALUE:
                doToSet(monad, state);
                return;

            case CEILING_VALUE:
                doFloorOrCeiling(monad, state, false);
                return;
            case FLOOR_VALUE:
                doFloorOrCeiling(monad, state, true);
                return;

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

    private void doToSet(Monad monad, State state) {
        Object r = monad.getArgument().evaluate(state);
        switch (Constant.getType(r)) {
            case BOOLEAN_TYPE:
            case STRING_TYPE:
            case DECIMAL_TYPE:
            case LONG_TYPE:
            case NULL_TYPE:
                QDLSet set = new QDLSet();
                set.add(r);
                monad.setEvaluated(true);
                monad.setResultType(SET_TYPE);
                monad.setResult(set);
                return;
            case SET_TYPE:
                monad.setEvaluated(true);
                monad.setResultType(SET_TYPE);
                monad.setResult(r);
                return;
            case STEM_TYPE:
                Polyad p = new Polyad(StemEvaluator.UNIQUE_VALUES);
                p.setArguments(monad.getArguments());
                p.evaluate(state);
                QDLStem stemVariable = (QDLStem) p.getResult(); // as per contract
                set = new QDLSet();
                set.addAll(stemVariable.getQDLList().values());

                monad.setResult(set);
                monad.setResultType(SET_TYPE);
                monad.setEvaluated(true);
                return;
        }
        throw new QDLExceptionWithTrace("unknown type", monad.getArgument());
    }

    private void doFloorOrCeiling(Monad monad, State state, boolean isFloor) {
        Polyad polyad;
        if (isFloor) {
            polyad = new Polyad(TMathEvaluator.FLOOR);
        } else {
            polyad = new Polyad(TMathEvaluator.CEILING);
        }
        polyad.addArgument(monad.getArgument());
        polyad.setSourceCode(monad.getSourceCode());
        polyad.setTokenPosition(monad.getTokenPosition());
        state.getMetaEvaluator().evaluate(polyad, state);
        monad.setResult(polyad.getResult());
        monad.setResultType(polyad.getResultType());
        monad.setEvaluated(polyad.isEvaluated());

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
            throw new QDLExceptionWithTrace("You can only " + (isPlusPlus ? "increment" : "decrement") + " a variable.", monad.getArgument());
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
            monad.setResultType(LONG_TYPE); // should be redundant

        }
        if (isBigDecimal(obj)) {
            gotOne = true;
            monad.setResultType(DECIMAL_TYPE); // should be redundant
            BigDecimal bd = (BigDecimal) obj;
            BigDecimal one = new BigDecimal("1.0");
            if (isPlusPlus) {
                resultValue = bd.add(one);
            } else {
                resultValue = bd.subtract(one);
            }
            monad.setResultType(DECIMAL_TYPE); // should be redundant
        }
        if (!gotOne) {
            throw new QDLExceptionWithTrace("" + (isPlusPlus ? PLUS_PLUS : MINUS_MINUS) + " requires a number value", monad.getArgument());
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
                    throw new QDLExceptionWithTrace("negation requires a strictly boolean argument not '" + objects[0] + "'", monad.getArgument());
                }
                r.result = !(Boolean) objects[0];
                r.resultType = BOOLEAN_TYPE;
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
                    case LONG_TYPE:
                        r.result = sign * (Long) objects[0];
                        r.resultType = LONG_TYPE;
                        break;
                    case DECIMAL_TYPE:
                        BigDecimal x = toBD(objects[0]);
                        r.result = sign < 0 ? x.negate() : x;
                        r.resultType = DECIMAL_TYPE;
                        break;
                    case STRING_TYPE:
                        if (sign > 0) {
                            r.result = objects[0];
                        } else {
                            r.result = "";
                        }
                        r.resultType = STRING_TYPE;
                        break;
                    default:
                        throw new QDLExceptionWithTrace("You can only take the negative of a number or string", monad.getArgument());

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

    public int[] getArgCount(Monad monad) {
        evaluate(monad, null);
        return (int[]) monad.getResult();
    }

    public int[] getArgCount(Dyad dyad) {
        evaluate(dyad, null);
        return (int[]) dyad.getResult();
    }

}
