package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.*;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import static ch.obermuhlner.math.big.BigDecimalMath.*;

/**
 * Class for transcendental functions, like log, exponentiation etc.
 * <p>Created by Jeff Gaynor<br>
 * on 3/18/21 at  6:27 AM
 */
public class TMathEvaluator extends AbstractEvaluator {
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

    public static final String GCD = "gcd";
    public static final int GCD_TYPE = 21 + TMATH_FUNCTION_BASE_VALUE;

    public static final String LCM = "lcm";
    public static final int LCM_TYPE = 22 + TMATH_FUNCTION_BASE_VALUE;


    @Override
    public String[] getFunctionNames() {
        if (fNames == null) {
            fNames = new String[]{
                    COSINE, SINE, TANGENT, LOG_10, LOG_E, EXP, SINH, COSH, TANH, GCD, LCM,
                    ARC_COSINE, ARC_SINE, ARC_TANGENT, ARC_COSH, ARC_SINH, ARC_TANH, PI, PI2,
                    N_ROOT, FLOOR, CEILING
            };
        }
        return fNames;
    }

    public static BigDecimal getPi(MathContext mathContext) {
        if (pi == null) {
            pi = pi(mathContext);
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
            naturalLogBase = exp(BigDecimal.ONE, mathContext);
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
            QDLExceptionWithTrace qq = new QDLExceptionWithTrace(t, polyad);
            throw qq;
        }
    }

    public boolean evaluate2(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case LCM:
                doLCM(polyad, state);
                return true;
            case GCD:
                doGCD(polyad, state);
                return true;
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

    private void doLCM(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{2});
            polyad.setEvaluated(true);
            return;
        }

        if (polyad.getArgCount() < 2) {
            throw new MissingArgException(LCM + " requires an argument", polyad);
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(LCM + " requires at most 1 argument", polyad.getArgAt(2));
        }

        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                BigInteger bi0 = gcdToBigInteger(polyad, objects[0]);
                BigInteger bi1 = gcdToBigInteger(polyad, objects[1]);
                // The LCM of two integers x,y is x*y/gcd(x,y)
                BigInteger prod = bi0.multiply(bi1);
                BigInteger rr = prod.divide(bi0.gcd(bi1));

                try {
                    Long ll = rr.longValueExact();
                    r.result = ll;
                    r.resultType = Constant.LONG_TYPE;
                } catch (ArithmeticException ax) {
                    // too big
                    BigDecimal bigDecimal = new BigDecimal(rr);
                    r.result = bigDecimal;
                    r.resultType = Constant.DECIMAL_TYPE;
                }
                return r;
            }
        };
        process2(polyad, pointer, LCM, state);
    }

    private void doGCD(Polyad polyad, State state) {
        if (polyad.isSizeQuery()) {
            polyad.setResult(new int[]{2});
            polyad.setEvaluated(true);
            return;
        }

        if (polyad.getArgCount() < 2) {
            throw new MissingArgException(GCD + " requires an argument", polyad);
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(GCD + " requires at most 1 argument", polyad.getArgAt(2));
        }

        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
                BigInteger bi0 = gcdToBigInteger(polyad, objects[0]);
                BigInteger bi1 = gcdToBigInteger(polyad, objects[1]);
                BigInteger rr = bi0.gcd(bi1);
                try {
                    Long ll = rr.longValueExact();
                    r.result = ll;
                    r.resultType = Constant.LONG_TYPE;
                } catch (ArithmeticException ax) {
                    // too big
                    BigDecimal bigInteger = new BigDecimal(rr);
                    r.result = bigInteger;
                    r.resultType = Constant.DECIMAL_TYPE;
                }
                return r;
            }
        };
        process2(polyad, pointer, GCD, state);
    }

    private BigInteger gcdToBigInteger(Polyad polyad, Object arg0) {
        BigInteger bi0 = null;
        boolean arg0OK = false;
        if (arg0 instanceof Long) {
            bi0 = new BigInteger(Long.toString((Long) arg0));
            arg0OK = true;
        }
        if (arg0 instanceof BigDecimal) {
            try {
                bi0 = ((BigDecimal) arg0).toBigIntegerExact();
            } catch (ArithmeticException ex) {
                throw new BadArgException("the first argument must be an integer.", polyad.getArgAt(0));
            }
            arg0OK = true;
        }
        if (!arg0OK) {
            throw new BadArgException("the first argument must be an integer.", polyad.getArgAt(0));
        }
        return bi0;
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
            throw new MissingArgException((isFloor ? FLOOR : CEILING) + " requires at least 1 argument", polyad);
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException((isFloor ? FLOOR : CEILING) + " requires at most 1 argument", polyad.getArgAt(1));
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
                    throw new BadArgException((isFloor ? FLOOR : CEILING) + " is only defined for numbers", polyad.getArgAt(0));
                }
                BigDecimal rr = null;
                if (isFloor) {
                    rr = bd.setScale(0, RoundingMode.FLOOR);
                } else {
                    rr = bd.setScale(0, RoundingMode.CEILING);
                }
                try {
                    r.result = rr.longValueExact();
                    r.resultType = Constant.LONG_TYPE;
                } catch (ArithmeticException ax) {
                    r.result = rr;
                    r.resultType = Constant.DECIMAL_TYPE;
                }
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
            throw new MissingArgException(N_ROOT + " requires 2 arguments", polyad.getArgCount() == 1 ? polyad.getArgAt(0) : polyad);
        }

        if (2 < polyad.getArgCount()) {
            throw new ExtraArgException(N_ROOT + " requires at most 2 arguments", polyad.getArgAt(2));
        }

        AbstractEvaluator.fPointer pointer = new AbstractEvaluator.fPointer() {
            @Override
            public AbstractEvaluator.fpResult process(Object... objects) {
                AbstractEvaluator.fpResult r = new AbstractEvaluator.fpResult();
                Object arg1 = objects[0];
                Object arg2 = objects[1];
                if (isBoolean(arg1) || isString(arg1) || arg1 == QDLNull.getInstance()) {
                    throw new QDLExceptionWithTrace(N_ROOT + " requires a numeric argument as its base", polyad.getArgAt(0));
                }
                if (isBoolean(arg2) || isString(arg2) || arg2 == QDLNull.getInstance()) {
                    throw new QDLExceptionWithTrace(N_ROOT + " requires a numeric argument as its exponent", polyad.getArgAt(1));
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
                    throw new BadArgException(N_ROOT + " requires an integer second argument", polyad.getArgAt(1));
                }
                if (isLong(arg2)) {
                    if (0 == (Long) arg2) {
                        throw new BadArgException(N_ROOT + " cannot extract the zero-th root", polyad.getArgAt(1));
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
                    throw new BadArgException(N_ROOT + " requires a positive base if the exponent is even.", polyad.getArgAt(0));
                }
                MathContext mathContext = state.getOpEvaluator().getMathContext();
                BigDecimal abs = base.abs(mathContext);
                BigDecimal result;
                if (-1 == abs.compareTo(new BigDecimal("1000000"))) {
                    result = root(base.abs(mathContext), exponent, state.getOpEvaluator().getMathContext());
                } else {
                    // Issue is that the BigMath root function has dismally slow  convergence
                    // for larger values -- as in hours per value (!!)
                    // so compute it in an alternate way: nroot(x, n) == exp(ln(|x|)/n)*sgn(x)
                    result = exp(log(abs, mathContext).divide(exponent, mathContext), mathContext);
                }
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
            throw new ExtraArgException(PI + " requires at most 1 argument", polyad.getArgAt(1));
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
                rr = pow(getPi(mathContext), (Long) exponent, mathContext);
            }
            if (isBigDecimal(exponent)) {
                rr = pow(getPi(mathContext), (BigDecimal) exponent, mathContext);
            }
            if (rr == null) {
                throw new BadArgException("argument must be a number", polyad.getArgAt(0));
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
            case GCD:
                return GCD_TYPE;
            case LCM:
                return LCM_TYPE;
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
                bd = cos(x, mathContext);
                break;
            case ARC_COSINE:
                bd = acos(x, mathContext);
                break;
            case SINE:
                bd = sin(x, mathContext);
                break;
            case ARC_SINE:
                bd = asin(x, mathContext);
                break;
            case TANGENT:
                try {
                    bd = tan(x, mathContext);
                } catch (ArithmeticException ax) {
                    throw new IllegalArgumentException("you do not have enough precision to compute " + TANGENT + ". Please increase " + MathEvaluator.NUMERIC_DIGITS);
                }
                break;
            case ARC_TANGENT:
                bd = atan(x, mathContext);
                break;
            case LOG_10:
                bd = log10(x, mathContext);
                break;
            case LOG_E:
                bd = log(x, mathContext);
                break;
            case EXP:
                bd = exp(x, mathContext);
                break;
            case SINH:
                bd = sinh(x, mathContext);
                break;
            case ARC_SINH:
                bd = asinh(x, mathContext);
                break;
            case COSH:
                bd = cosh(x, mathContext);
                break;
            case ARC_COSH:
                bd = acosh(x, mathContext);
                break;
            case TANH:
                try {
                    bd = tanh(x, mathContext);
                } catch (ArithmeticException ax) {
                    throw new IllegalArgumentException("you do not have enough precision to compute " + TANH + ". Please increase " + MathEvaluator.NUMERIC_DIGITS);
                }
                break;
            case ARC_TANH:
                bd = atanh(x, mathContext);
                break;
            default:
                throw new UndefinedFunctionException("The function " + op + " is undefined", null);
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
            throw new MissingArgException(op + " requires 1 argument", polyad);
        }

        if (1 < polyad.getArgCount()) {
            throw new ExtraArgException(op + " requires at most 1 argument", polyad.getArgAt(1));
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
                    throw new BadArgException("error, " + op + "() got '" + ob + "', requires a number", polyad.getArgAt(0));
                }

                r.result = evaluateBD(bd, OpEvaluator.getMathContext(), op);
                r.resultType = Constant.DECIMAL_TYPE;
                return r;
            }
        };
        try {
            process1(polyad, pointer, op, state);
        } catch (UndefinedFunctionException udx) {
            udx.setStatement(polyad);
            throw udx;
        }
    }
}
