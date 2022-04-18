package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Class for transcendental functions, like log, exponentiation etc.
 * <p>Created by Jeff Gaynor<br>
 * on 3/18/21 at  6:27 AM
 */
public class TMathEvaluator extends AbstractFunctionEvaluator {
    public static final String TMATH_NAMESPACE = "tmath";

    @Override
    public String getNamespace() {
        return TMATH_NAMESPACE;
    }

    public static final int TMATH_FUNCTION_BASE_VALUE = 7000;

    public static final String PI = "pi";
    public static final String PI2 = "π";
    public static final int PI_TYPE = 1 + TMATH_FUNCTION_BASE_VALUE;


    public static final String COSINE = "cos";
    public static final int COSINE_TYPE = 2 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_COSINE = "acos";
    public static final int ARC_COSINE_TYPE = 3 + TMATH_FUNCTION_BASE_VALUE;

    public static final String SINE = "sin";
    public static final int SINE_TYPE = 4 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_SINE = "asin";
    public static final int ARC_SINE_TYPE = 5 + TMATH_FUNCTION_BASE_VALUE;

    public static final String TANGENT = "tan";
    public static final int TANGENT_TYPE = 6 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_TANGENT = "atan";
    public static final int ARC_TANGENT_TYPE = 7 + TMATH_FUNCTION_BASE_VALUE;

    public static final String SINH = "sinh";
    public static final int SINH_TYPE = 8 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_SINH = "asinh";
    public static final int ARC_SINH_TYPE = 9 + TMATH_FUNCTION_BASE_VALUE;


    public static final String COSH = "cosh";
    public static final int COSH_TYPE = 10 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_COSH = "acosh";
    public static final int ARC_COSH_TYPE = 11 + TMATH_FUNCTION_BASE_VALUE;

    public static final String TANH = "tanh";
    public static final int TANH_TYPE = 12 + TMATH_FUNCTION_BASE_VALUE;

    public static final String ARC_TANH = "atanh";
    public static final int ARC_TANH_TYPE = 14 + TMATH_FUNCTION_BASE_VALUE;

    public static final String LOG_E = "ln";
    public static final int LOG_E_TYPE = 15 + TMATH_FUNCTION_BASE_VALUE;

    public static final String LOG_10 = "log";
    public static final int LOG_10__TYPE = 16 + TMATH_FUNCTION_BASE_VALUE;

    public static final String EXP = "exp";
    public static final int EXP_TYPE = 17 + TMATH_FUNCTION_BASE_VALUE;

    public static final String N_ROOT = "nroot";
    public static final int N_ROOT_TYPE = 18 + TMATH_FUNCTION_BASE_VALUE;

    public static final String FLOOR = "floor";
    public static final int FLOOR_TYPE = 19 + TMATH_FUNCTION_BASE_VALUE;

    public static final String CEILING = "ceiling";
    public static final int CEILING_TYPE = 20 + TMATH_FUNCTION_BASE_VALUE;


    @Override
    public String[] getFunctionNames() {
        if (fNames == null) {
            fNames = new String[]{
                    COSINE, SINE, TANGENT, LOG_10, LOG_E, EXP, SINH, COSH, TANH,
                    ARC_COSINE, ARC_SINE, ARC_TANGENT, ARC_COSH, ARC_SINH, ARC_TANH, PI, PI2,
                    N_ROOT, FLOOR, CEILING
            };
        }
        return fNames;
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
        try {
            return evaluate2(polyad, state);
        } catch (QDLException q) {
            throw q;
        } catch (Throwable t) {
            QDLStatementExecutionException qq = new QDLStatementExecutionException(t, polyad);
            throw qq;
        }
    }

    public boolean evaluate2(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case SINE:
                doTranscendentalMath(polyad, SINE, state);
                return true;
            case ARC_SINE:
                doTranscendentalMath(polyad, ARC_SINE, state);
                return true;
            case COSINE:
                doTranscendentalMath(polyad, COSINE, state);
                return true;
            case ARC_COSINE:
                doTranscendentalMath(polyad, ARC_COSINE, state);
                return true;
            case TANGENT:
                doTranscendentalMath(polyad, TANGENT, state);
                return true;
            case ARC_TANGENT:
                doTranscendentalMath(polyad, ARC_TANGENT, state);
                return true;
            case SINH:
                doTranscendentalMath(polyad, SINH, state);
                return true;
            case ARC_SINH:
                doTranscendentalMath(polyad, ARC_SINH, state);
                return true;
            case COSH:
                doTranscendentalMath(polyad, COSH, state);
                return true;
            case ARC_COSH:
                doTranscendentalMath(polyad, ARC_COSH, state);
                return true;
            case TANH:
                doTranscendentalMath(polyad, TANH, state);
                return true;
            case ARC_TANH:
                doTranscendentalMath(polyad, ARC_TANH, state);
                return true;
            case LOG_10:
                doTranscendentalMath(polyad, LOG_10, state);
                return true;
            case LOG_E:
                doTranscendentalMath(polyad, LOG_E, state);
                return true;
            case EXP:
                doTranscendentalMath(polyad, EXP, true, state);
                return true;
            case PI:
            case PI2:
                computePi(polyad, state);
                return true;
            case N_ROOT:
                doNRoot(polyad, state);
                return true;
            case FLOOR:
                doFloor(polyad, state);
                return true;
            case CEILING:
                doCeiling(polyad, state);
                return true;
        }
        return false;
    }

    private void doCeiling(Polyad polyad, State state) {
        doFloorOrCeiling(polyad, state, false);
    }

    private void doFloor(Polyad polyad, State state) {
        doFloorOrCeiling(polyad, state, true);
    }

    private void doFloorOrCeiling(Polyad polyad, State state, boolean isFloor) {

        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException((isFloor ? FLOOR : CEILING) + " requires at least 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException((isFloor ? FLOOR : CEILING) + " requires at most 1 argument");
        }

        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                BigDecimal bd = null;
                Object ob = objects[0];
                if (ob instanceof Long) {
                    r.result = ob;
                    r.resultType = Constant.LONG_TYPE;
                    return r;
                }

                if (ob instanceof BigDecimal) {
                    bd = (BigDecimal) ob;
                } else {
                    throw new IllegalArgumentException((isFloor ? FLOOR : CEILING) + " is only defined for numbers");
                }
                if (isFloor) {
                    r.result = bd.setScale(0, RoundingMode.FLOOR);
                } else {
                    r.result = bd.setScale(0, RoundingMode.CEILING);
                }
                r.resultType = Constant.DECIMAL_TYPE;
                return r;
            }
        };
        process1(polyad, pointer, isFloor ? FLOOR : CEILING, state);

    }

    private void doNRoot(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{2});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 2) {
            throw new MissingArgException(N_ROOT + " requires 2 arguments");
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(N_ROOT + " requires at most 2 arguments");
        }

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
                    r.resultType = polyad.getArgAt(0).getResultType();
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
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{0, 1});
            polyad.setEvaluated(true);
            return;
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(PI + " requires at most 1 argument");
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
                throw new BadArgException("argument must be a number");
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
                return COSINE_TYPE;
            case ARC_COSINE:
                return ARC_COSINE_TYPE;
            case SINE:
                return SINE_TYPE;
            case ARC_SINE:
                return ARC_SINE_TYPE;
            case TANGENT:
                return TANGENT_TYPE;
            case ARC_TANGENT:
                return ARC_TANGENT_TYPE;
            case LOG_10:
                return LOG_10__TYPE;
            case LOG_E:
                return LOG_E_TYPE;
            case EXP:
                return EXP_TYPE;
            case SINH:
                return SINH_TYPE;
            case ARC_SINH:
                return ARC_SINH_TYPE;
            case COSH:
                return COSH_TYPE;
            case ARC_COSH:
                return ARC_COSH_TYPE;
            case TANH:
                return TANH_TYPE;
            case ARC_TANH:
                return ARC_TANH_TYPE;
            case N_ROOT:
                return N_ROOT_TYPE;
            case FLOOR:
                return FLOOR_TYPE;
            case CEILING:
                return CEILING_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
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
                try {
                    bd = ch.obermuhlner.math.big.BigDecimalMath.tan(x, mathContext);
                } catch (ArithmeticException ax) {
                    throw new IllegalArgumentException("you do not have enough precision to compute " + TANGENT + ". Please increase " + MathEvaluator.NUMERIC_DIGITS);
                }
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
                try {
                    bd = ch.obermuhlner.math.big.BigDecimalMath.tanh(x, mathContext);
                } catch (ArithmeticException ax) {
                    throw new IllegalArgumentException("you do not have enough precision to compute " + TANH + ". Please increase " + MathEvaluator.NUMERIC_DIGITS);
                }
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
        // The next test hits all but the case of exp()
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{1});
            polyad.setEvaluated(true);
            return;
        }
        if (polyad.getArgCount() < 1) {
            throw new MissingArgException(op + " requires 1 argument");
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(op + " requires at most 1 argument");
        }

        doTranscendentalMath(polyad, op, false, state);
    }

    private void doTranscendentalMath(Polyad polyad, String op, boolean doPowers, State state) {
        if (doPowers) {
            if (polyad.isSizeQuery()) {
                polyad.setResult(new int[]{0, 1});
                polyad.setEvaluated(true);
                return;
            }
        }
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
                    throw new IllegalArgumentException("error, " + op + "() got '" + ob + "', requires a number");
                }

                r.result = evaluateBD(bd, OpEvaluator.getMathContext(), op);
                r.resultType = Constant.DECIMAL_TYPE;
                return r;
            }
        };
        process1(polyad, pointer, op, state);
    }
}
