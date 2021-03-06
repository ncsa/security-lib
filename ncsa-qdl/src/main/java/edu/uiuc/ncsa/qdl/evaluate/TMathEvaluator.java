package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.evaluate.MathEvaluator.MATH_FQ;

/**
 * Class for transcendental functions, like log, exponentiation etc.
 * <p>Created by Jeff Gaynor<br>
 * on 3/18/21 at  6:27 AM
 */
public class TMathEvaluator extends AbstractFunctionEvaluator {
    public static final int TMATH_FUNCTION_BASE_VALUE = 7000;

    public static final String PI = "pi";
    public static final String PI2 = "π";
    public static final String FQ_PI = MATH_FQ + PI;
    public static final String FQ_PI2 = MATH_FQ + PI2;
    public static final int PI_TYPE = 1 + TMATH_FUNCTION_BASE_VALUE;


    public static final String COSINE = "cos";
    public static final String FQ_COSINE = MATH_FQ + COSINE;
    public static final int COSINE_TYPE = 2 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_COSINE = "acos";
    public static final String FQ_ARC_COSINE = MATH_FQ + ARC_COSINE;
    public static final int ARC_COSINE_TYPE = 3 + TMATH_FUNCTION_BASE_VALUE;

    public static final String SINE = "sin";
    public static final String FQ_SINE = MATH_FQ + SINE;
    public static final int SINE_TYPE = 4 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_SINE = "asin";
    public static final String FQ_ARC_SINE = MATH_FQ + ARC_SINE;
    public static final int ARC_SINE_TYPE = 5 + TMATH_FUNCTION_BASE_VALUE;

    public static final String TANGENT = "tan";
    public static final String FQ_TANGENT = MATH_FQ + TANGENT;
    public static final int TANGENT_TYPE = 6 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_TANGENT = "atan";
    public static final String FQ_ARC_TANGENT = MATH_FQ + ARC_TANGENT;
    public static final int ARC_TANGENT_TYPE = 7 + TMATH_FUNCTION_BASE_VALUE;

    public static final String SINH = "sinh";
    public static final String FQ_SINH = MATH_FQ + SINH;
    public static final int SINH_TYPE = 8 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_SINH = "asinh";
    public static final String FQ_ARC_SINH = MATH_FQ + ARC_SINH;
    public static final int ARC_SINH_TYPE = 9 + TMATH_FUNCTION_BASE_VALUE;


    public static final String COSH = "cosh";
    public static final String FQ_COSH = MATH_FQ + COSH;
    public static final int COSH_TYPE = 10 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_COSH = "acosh";
    public static final String FQ_ARC_COSH = MATH_FQ + ARC_COSH;
    public static final int ARC_COSH_TYPE = 11 + TMATH_FUNCTION_BASE_VALUE;

    public static final String TANH = "tanh";
    public static final String FQ_TANH = MATH_FQ + TANH;
    public static final int TANH_TYPE = 12 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_TANH = "atanh";
    public static final String FQ_ARC_TANH = MATH_FQ + ARC_TANH;
    public static final int ARC_TANH_TYPE = 14 + TMATH_FUNCTION_BASE_VALUE;

    public static final String LOG_E = "ln";
    public static final String FQ_LOG_E = MATH_FQ + LOG_E;
    public static final int LOG_E_TYPE = 15 + TMATH_FUNCTION_BASE_VALUE;

    public static final String LOG_10 = "log";
    public static final String FQ_LOG_10 = MATH_FQ + LOG_10;
    public static final int LOG_10__TYPE = 16 + TMATH_FUNCTION_BASE_VALUE;

    public static final String EXP = "exp";
    public static final String FQ_EXP = MATH_FQ + EXP;
    public static final int EXP_TYPE = 17 + TMATH_FUNCTION_BASE_VALUE;

    public static final String N_ROOT = "nroot";
    public static final String FQ_N_ROOT = MATH_FQ + N_ROOT;
    public static final int N_ROOT_TYPE = 18 + TMATH_FUNCTION_BASE_VALUE;

    public static String FUNC_NAMES[] = new String[]{
            COSINE, SINE, TANGENT, LOG_10, LOG_E, EXP, SINH, COSH, TANH,
            ARC_COSINE, ARC_SINE, ARC_TANGENT, ARC_COSH, ARC_SINH, ARC_TANH, PI, PI2,
            N_ROOT
    };
    public static String FQ_FUNC_NAMES[] = new String[]{
            FQ_COSINE, FQ_SINE, FQ_TANGENT, FQ_LOG_10, FQ_LOG_E, FQ_EXP,
            FQ_SINH, FQ_COSH, FQ_TANH,
            FQ_ARC_COSINE, FQ_ARC_SINE, FQ_ARC_TANGENT, FQ_ARC_COSH, FQ_ARC_SINH, FQ_ARC_TANH, FQ_PI, FQ_PI2,
            FQ_N_ROOT
    };

    @Override
    public String[] getFunctionNames() {
        return FUNC_NAMES;
    }

    public static BigDecimal getPi(MathContext mathContext) {
        if (pi == null) {
            pi = ch.obermuhlner.math.big.BigDecimalMath.pi(mathContext);
        }
        return pi;
    }

    /**
     * Pi can be set to null if the precision changes, triggering a recompute.
     * However, it cannot be actually gotten without a context.
     *
     * @param newPI
     */
    public static void setPi(BigDecimal newPI) {
        pi = newPI;
    }

    static BigDecimal pi;

    public static BigDecimal getNaturalLogBase(MathContext mathContext) {
        if (naturalLogBase == null) {
            naturalLogBase = ch.obermuhlner.math.big.BigDecimalMath.exp(BigDecimal.ONE, mathContext);
        }
        return naturalLogBase;
    }

    public static void setNaturalLogBase(BigDecimal naturalLogBase) {
        TMathEvaluator.naturalLogBase = naturalLogBase;
    }

    static BigDecimal naturalLogBase;

    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case SINE:
            case FQ_SINE:
                doTranscendentalMath(polyad, SINE, state);
                return true;
            case ARC_SINE:
            case FQ_ARC_SINE:
                doTranscendentalMath(polyad, ARC_SINE, state);
                return true;
            case COSINE:
            case FQ_COSINE:
                doTranscendentalMath(polyad, COSINE, state);
                return true;
            case ARC_COSINE:
            case FQ_ARC_COSINE:
                doTranscendentalMath(polyad, ARC_COSINE, state);
                return true;
            case TANGENT:
            case FQ_TANGENT:
                doTranscendentalMath(polyad, TANGENT, state);
                return true;
            case ARC_TANGENT:
            case FQ_ARC_TANGENT:
                doTranscendentalMath(polyad, ARC_TANGENT, state);
                return true;
            case SINH:
            case FQ_SINH:
                doTranscendentalMath(polyad, SINH, state);
                return true;
            case ARC_SINH:
            case FQ_ARC_SINH:
                doTranscendentalMath(polyad, ARC_SINH, state);
                return true;
            case COSH:
            case FQ_COSH:
                doTranscendentalMath(polyad, COSH, state);
                return true;
            case ARC_COSH:
            case FQ_ARC_COSH:
                doTranscendentalMath(polyad, ARC_COSH, state);
                return true;
            case TANH:
            case FQ_TANH:
                doTranscendentalMath(polyad, TANH, state);
                return true;
            case ARC_TANH:
            case FQ_ARC_TANH:
                doTranscendentalMath(polyad, ARC_TANH, state);
                return true;
            case LOG_10:
            case FQ_LOG_10:
                doTranscendentalMath(polyad, LOG_10, state);
                return true;
            case LOG_E:
            case FQ_LOG_E:
                doTranscendentalMath(polyad, LOG_E, state);
                return true;
            case EXP:
            case FQ_EXP:
                doTranscendentalMath(polyad, EXP, true, state);
                return true;
            case PI:
            case PI2:
            case FQ_PI:
            case FQ_PI2:
                computePi(polyad, state);
                return true;
            case N_ROOT:
            case FQ_N_ROOT:
                doNRoot(polyad, state);
                return true;
        }
        return false;
    }

    private void doNRoot(Polyad polyad, State state) {

        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                Object arg1 = objects[0];
                Object arg2 = objects[1];
                if (isBoolean(arg1) || isString(arg1) || arg1 == QDLNull.getInstance()) {
                    throw new IllegalArgumentException(N_ROOT + " requires a numeric argument as its base");
                }
                if (isBoolean(arg2) || isString(arg2) || arg2 == QDLNull.getInstance()) {
                    throw new IllegalArgumentException(N_ROOT + " requires a numeric argument as its exponent");
                }

                BigDecimal base = null;
                BigDecimal exponent = null;
                boolean isExponentEven = false;
                boolean isBaseNonNegative = false;
                if (isBigDecimal(arg1)) {
                    base = (BigDecimal) arg1;
                    isBaseNonNegative = 0 <= base.signum();
                }
                if (isLong(arg1)) {
                    base = new BigDecimal((Long) arg1);
                    isBaseNonNegative = 0 <= ((Long) arg1);
                }


                if (isBigDecimal(arg2)) {
                    throw new IllegalArgumentException(N_ROOT + " requires an integer second argument");
                }
                if (isLong(arg2)) {
                    if (0 == (Long) arg2) {
                        throw new IllegalArgumentException(N_ROOT + " cannot extract the zero-th root");
                    }
                    exponent = new BigDecimal((Long) arg2);
                    isExponentEven = (((Long) arg2) % 2) == 0;
                }

                if (areAllLongs(objects)) {
                    r.result = ((Long) objects[0]) % ((Long) objects[1]);
                    r.resultType = Constant.LONG_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                if (!isBaseNonNegative && isExponentEven) {
                    throw new IllegalArgumentException(N_ROOT + " requires a positive base if the exponent is even.");
                }
                MathContext mathContext = state.getOpEvaluator().getMathContext();
                BigDecimal result = ch.obermuhlner.math.big.BigDecimalMath.root(base.abs(mathContext), exponent, state.getOpEvaluator().getMathContext());
                if (!isBaseNonNegative) {
                    result = result.negate(mathContext);
                }
                r.result = result;
                r.resultType = Constant.DECIMAL_TYPE;
                return r;
            }
        };
        process2(polyad, pointer, N_ROOT, state);


        // Do scalar case.
    }

    private void computePi(Polyad polyad, State state) {
        if (1 < polyad.getArgCount()) {
            throw new IllegalArgumentException(PI + " takes at most one argument");
        }
        Object exponent;
        if (polyad.getArgCount() == 0) {
            // implicit assumption that the exponent is 1.
            polyad.setResult(getPi(state.getOpEvaluator().getMathContext()));
        } else {
            exponent = polyad.evalArg(0, state);

            MathContext mathContext = OpEvaluator.getMathContext();
            BigDecimal rr = null;
            if (isLong(exponent)) {
                rr = ch.obermuhlner.math.big.BigDecimalMath.pow(getPi(mathContext), (Long) exponent, mathContext);
            }
            if (isBigDecimal(exponent)) {
                rr = ch.obermuhlner.math.big.BigDecimalMath.pow(getPi(mathContext), (BigDecimal) exponent, mathContext);
            }
            if (rr == null) {
                throw new IllegalArgumentException("Error: argument must be a number");
            }
            polyad.setResult(rr);

        }
        polyad.setResultType(Constant.DECIMAL_TYPE);
        polyad.setEvaluated(true);
    }

    @Override
    public int getType(String name) {
        switch (name) {
            case COSINE:
            case FQ_COSINE:
                return COSINE_TYPE;
            case ARC_COSINE:
            case FQ_ARC_COSINE:
                return ARC_COSINE_TYPE;
            case SINE:
            case FQ_SINE:
                return SINE_TYPE;
            case ARC_SINE:
            case FQ_ARC_SINE:
                return ARC_SINE_TYPE;
            case TANGENT:
            case FQ_TANGENT:
                return TANGENT_TYPE;
            case ARC_TANGENT:
            case FQ_ARC_TANGENT:
                return ARC_TANGENT_TYPE;
            case LOG_10:
            case FQ_LOG_10:
                return LOG_10__TYPE;
            case LOG_E:
            case FQ_LOG_E:
                return LOG_E_TYPE;
            case EXP:
            case FQ_EXP:
                return EXP_TYPE;
            case SINH:
            case FQ_SINH:
                return SINH_TYPE;
            case ARC_SINH:
            case FQ_ARC_SINH:
                return ARC_SINH_TYPE;
            case COSH:
            case FQ_COSH:
                return COSH_TYPE;
            case ARC_COSH:
            case FQ_ARC_COSH:
                return ARC_COSH_TYPE;
            case TANH:
            case FQ_TANH:
                return TANH_TYPE;
            case ARC_TANH:
            case FQ_ARC_TANH:
                return ARC_TANH_TYPE;
            case N_ROOT:
            case FQ_N_ROOT:
                return N_ROOT_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }

    @Override
    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        String[] fnames = listFQ ? FQ_FUNC_NAMES : FUNC_NAMES;

        for (String key : fnames) {
            names.add(key + "()");
        }
        return names;
    }

    private BigDecimal evaluateBD(BigDecimal x, MathContext mathContext, String op) {
        return evaluateBD(x, mathContext, false, op);
    }

    private BigDecimal evaluateBD(BigDecimal x, MathContext mathContext, boolean doPowers, String op) {
        BigDecimal bd = null;
        switch (op) {
            case COSINE:
                bd = ch.obermuhlner.math.big.BigDecimalMath.cos(x, mathContext);
                break;
            case ARC_COSINE:
                bd = ch.obermuhlner.math.big.BigDecimalMath.acos(x, mathContext);
                break;
            case SINE:
                bd = ch.obermuhlner.math.big.BigDecimalMath.sin(x, mathContext);
                break;
            case ARC_SINE:
                bd = ch.obermuhlner.math.big.BigDecimalMath.asin(x, mathContext);
                break;
            case TANGENT:
                bd = ch.obermuhlner.math.big.BigDecimalMath.tan(x, mathContext);
                break;
            case ARC_TANGENT:
                bd = ch.obermuhlner.math.big.BigDecimalMath.atan(x, mathContext);
                break;
            case LOG_10:
                bd = ch.obermuhlner.math.big.BigDecimalMath.log10(x, mathContext);
                break;
            case LOG_E:
                bd = ch.obermuhlner.math.big.BigDecimalMath.log(x, mathContext);
                break;
            case EXP:
                bd = ch.obermuhlner.math.big.BigDecimalMath.exp(x, mathContext);
                break;
            case SINH:
                bd = ch.obermuhlner.math.big.BigDecimalMath.sinh(x, mathContext);
                break;
            case ARC_SINH:
                bd = ch.obermuhlner.math.big.BigDecimalMath.asinh(x, mathContext);
                break;
            case COSH:
                bd = ch.obermuhlner.math.big.BigDecimalMath.cosh(x, mathContext);
                break;
            case ARC_COSH:
                bd = ch.obermuhlner.math.big.BigDecimalMath.acosh(x, mathContext);
                break;
            case TANH:
                bd = ch.obermuhlner.math.big.BigDecimalMath.tanh(x, mathContext);
                break;
            case ARC_TANH:
                bd = ch.obermuhlner.math.big.BigDecimalMath.atanh(x, mathContext);
                break;
            default:
                throw new UndefinedFunctionException("The function " + op + " is undefined");
        }
        // later may want to start setting scale for Big decimals...
        return bd;
    }

    private void doTranscendentalMath(Polyad polyad, String op, State state) {
        doTranscendentalMath(polyad, op, false, state);
    }

    private void doTranscendentalMath(Polyad polyad, String op, boolean doPowers, State state) {
        if (doPowers && polyad.getArgCount() == 0) {
            // do  exp() as if exp(1)
            polyad.setResult(getNaturalLogBase(OpEvaluator.getMathContext()));
            polyad.setResultType(Constant.DECIMAL_TYPE);
            polyad.setEvaluated(true);
            return;
        }

        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                BigDecimal bd = null;
                Object ob = objects[0];
                if (ob instanceof Long) {
                    bd = new BigDecimal((Long) ob);
                }
                if (ob instanceof BigDecimal) {
                    bd = (BigDecimal) ob;
                }

                if (bd == null) {
                    throw new IllegalArgumentException("error, " + op + "() got '"+ ob + "', requires a number");
                }

                r.result = evaluateBD(bd, OpEvaluator.getMathContext(), op);
                r.resultType = Constant.DECIMAL_TYPE;
                return r;
            }
        };
        process1(polyad, pointer, op, state);
    }
}
