package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.exceptions.UndefinedFunctionException;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.security.SecureRandom;
import java.util.Date;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class MathEvaluator extends AbstractFunctionEvaluator {
    public static final String MATH_NAMESPACE = "math";
    public static final String MATH_FQ = MATH_NAMESPACE + ImportManager.NS_DELIMITER;
    public static final int MATH_FUNCTION_BASE_VALUE = 1000;
    public static final String ABS_VALUE = "abs";
    public static final String FQ_ABS_VALUE = MATH_FQ + ABS_VALUE;
    public static final int ABS_VALUE_TYPE = 1 + MATH_FUNCTION_BASE_VALUE;

    public static final String RANDOM = "random";
    public static final String FQ_RANDOM = MATH_FQ + RANDOM;
    public static final int RANDOM_TYPE = 2 + MATH_FUNCTION_BASE_VALUE;

    public static final String RANDOM_STRING = "random_string";
    public static final String FQ_RANDOM_STRING = MATH_FQ + RANDOM_STRING;
    public static final int RANDOM_STRING_TYPE = 3 + MATH_FUNCTION_BASE_VALUE;

    public static final String HASH = "hash";
    public static final String FQ_HASH = MATH_FQ + HASH;
    public static final int HASH_TYPE = 4 + MATH_FUNCTION_BASE_VALUE;

    public static final String TO_HEX = "to_hex";
    public static final String FQ_TO_HEX = MATH_FQ + TO_HEX;
    public static final int TO_HEX_TYPE = 5 + MATH_FUNCTION_BASE_VALUE;

    public static final String FROM_HEX = "from_hex";
    public static final String FQ_FROM_HEX = MATH_FQ + FROM_HEX;
    public static final int FROM_HEX_TYPE = 6 + MATH_FUNCTION_BASE_VALUE;

    public static final String DATE_MS = "date_ms";
    public static final String FQ_DATE_MS = MATH_FQ + DATE_MS;
    public static final int DATE_MS_TYPE = 7 + MATH_FUNCTION_BASE_VALUE;


    public static final String DECODE_B64 = "decode_b64";
    public static final String FQ_DECODE_B64 = MATH_FQ + DECODE_B64;
    public static final int DECODE_B64_TYPE = 8 + MATH_FUNCTION_BASE_VALUE;

    public static final String ENCODE_B64 = "encode_b64";
    public static final String FQ_ENCODE_B64 = MATH_FQ + ENCODE_B64;
    public static final int ENCODE_B64_TYPE = 9 + MATH_FUNCTION_BASE_VALUE;

    public static final String MOD = "mod";
    public static final String FQ_MOD = MATH_FQ + MOD;
    public static final int MOD_TYPE = 10 + MATH_FUNCTION_BASE_VALUE;

    public static final String DATE_ISO = "date_iso";
    public static final String FQ_DATE_ISO = MATH_FQ + DATE_ISO;
    public static final int DATE_ISO_TYPE = 11 + MATH_FUNCTION_BASE_VALUE;

    public static final String NUMERIC_DIGITS = "numeric_digits";
    public static final String FQ_NUMERIC_DIGITS = MATH_FQ + NUMERIC_DIGITS;
    public static final int NUMERIC_DIGITS_TYPE = 12 + MATH_FUNCTION_BASE_VALUE;

    public static final String IDENTITY_FUNCTION = "i";
    public static final String LONG_IDENTITY_FUNCTION = "identity";
    public static final String FQ_IDENTITY_FUNCTION = MATH_FQ + IDENTITY_FUNCTION;
    public static final String FQ_LONG_IDENTITY_FUNCTION = MATH_FQ + "identity";
    public static final int IDENTITY_FUNCTION_TYPE = 14 + MATH_FUNCTION_BASE_VALUE;

    public static final String COSINE = "cos";
    public static final String FQ_COSINE = MATH_FQ + COSINE;
    public static final int COSINE_TYPE = 15 + MATH_FUNCTION_BASE_VALUE;

    public static final String SINE = "sin";
    public static final String FQ_SINE = MATH_FQ + SINE;
    public static final int SINE_TYPE = 16 + MATH_FUNCTION_BASE_VALUE;

    public static final String TANGENT = "tan";
    public static final String FQ_TANGENT = MATH_FQ + TANGENT;
    public static final int TANGENT_TYPE = 17 + MATH_FUNCTION_BASE_VALUE;

    public static final String LOG_E = "ln";
    public static final String FQ_LOG_E = MATH_FQ + LOG_E;
    public static final int LOG_E_TYPE = 17 + MATH_FUNCTION_BASE_VALUE;

    public static final String LOG_10 = "log";
    public static final String FQ_LOG_10 = MATH_FQ + LOG_10;
    public static final int LOG_10__TYPE = 18 + MATH_FUNCTION_BASE_VALUE;

    public static final String EXP = "exp";
    public static final String FQ_EXP = MATH_FQ + EXP;
    public static final int EXP_TYPE = 19 + MATH_FUNCTION_BASE_VALUE;

    public static final String COSH = "cosh";
    public static final String FQ_COSH = MATH_FQ + COSH;
    public static final int COSH_TYPE = 20 + MATH_FUNCTION_BASE_VALUE;

    public static final String SINH = "sinh";
    public static final String FQ_SINH = MATH_FQ + SINH;
    public static final int SINH_TYPE = 21 + MATH_FUNCTION_BASE_VALUE;

    public static final String TANH = "tanh";
    public static final String FQ_TANH = MATH_FQ + TANH;
    public static final int TANH_TYPE = 22 + MATH_FUNCTION_BASE_VALUE;

    public static String FUNC_NAMES[] = new String[]{
            COSINE, SINE, TANGENT, LOG_10, LOG_E, EXP, SINH, COSH, TANH,
            IDENTITY_FUNCTION,
            LONG_IDENTITY_FUNCTION,
            ABS_VALUE,
            RANDOM,
            RANDOM_STRING,
            HASH,
            TO_HEX,
            FROM_HEX,
            DATE_MS,
            DATE_ISO,
            NUMERIC_DIGITS,
            DECODE_B64,
            ENCODE_B64,
            MOD,
            DATE_ISO};

    public static String FQ_FUNC_NAMES[] = new String[]{
            FQ_COSINE, FQ_SINE, FQ_TANGENT, FQ_LOG_10, FQ_LOG_E, FQ_EXP,
            FQ_SINH, FQ_COSH, FQ_TANH,
            FQ_IDENTITY_FUNCTION,
            FQ_LONG_IDENTITY_FUNCTION,
            FQ_ABS_VALUE,
            FQ_RANDOM,
            FQ_RANDOM_STRING,
            FQ_HASH,
            FQ_TO_HEX,
            FQ_FROM_HEX,
            FQ_DATE_MS,
            FQ_DATE_ISO,
            FQ_NUMERIC_DIGITS,
            FQ_DECODE_B64,
            FQ_ENCODE_B64,
            FQ_MOD,
            FQ_DATE_ISO};

    public TreeSet<String> listFunctions(boolean listFQ) {
        TreeSet<String> names = new TreeSet<>();
        String[] fnames = listFQ ? FQ_FUNC_NAMES : FUNC_NAMES;

        for (String key : fnames) {
            names.add(key + "()");
        }
        return names;
    }

    @Override
    public String[] getFunctionNames() {
        return FUNC_NAMES;
    }

    @Override
    public int getType(String name) {
        switch (name) {
            case COSINE:
            case FQ_COSINE:
                return COSINE_TYPE;
            case SINE:
            case FQ_SINE:
                return SINE_TYPE;
            case TANGENT:
            case FQ_TANGENT:
                return TANGENT_TYPE;
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
            case COSH:
            case FQ_COSH:
                return COSH_TYPE;
            case TANH:
            case FQ_TANH:
                return TANH_TYPE;
            case IDENTITY_FUNCTION:
            case LONG_IDENTITY_FUNCTION:
            case FQ_IDENTITY_FUNCTION:
            case FQ_LONG_IDENTITY_FUNCTION:
                return IDENTITY_FUNCTION_TYPE;
            case ABS_VALUE:
            case FQ_ABS_VALUE:
                return ABS_VALUE_TYPE;
            case RANDOM:
            case FQ_RANDOM:
                return RANDOM_TYPE;
            case RANDOM_STRING:
            case FQ_RANDOM_STRING:
                return RANDOM_STRING_TYPE;
            case HASH:
            case FQ_HASH:
                return HASH_TYPE;
            case TO_HEX:
            case FQ_TO_HEX:
                return TO_HEX_TYPE;
            case NUMERIC_DIGITS:
            case FQ_NUMERIC_DIGITS:
                return NUMERIC_DIGITS_TYPE;
            case FROM_HEX:
            case FQ_FROM_HEX:
                return FROM_HEX_TYPE;
            case ENCODE_B64:
            case FQ_ENCODE_B64:
                return ENCODE_B64_TYPE;
            case DECODE_B64:
            case FQ_DECODE_B64:
                return DECODE_B64_TYPE;
            case DATE_MS:
            case FQ_DATE_MS:
                return DATE_MS_TYPE;
            case DATE_ISO:
            case FQ_DATE_ISO:
                return DATE_ISO_TYPE;
            case MOD:
            case FQ_MOD:
                return MOD_TYPE;
        }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {

        switch (polyad.getName()) {
            case COSINE:
            case FQ_COSINE:
                doTranscendentalMath(polyad, COSINE, state);
                return true;
            case SINE:
            case FQ_SINE:
                doTranscendentalMath(polyad, SINE, state);
                return true;
            case TANGENT:
            case FQ_TANGENT:
                doTranscendentalMath(polyad, TANGENT, state);
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
                doTranscendentalMath(polyad, EXP, state);
                return true;
            case SINH:
            case FQ_SINH:
                doTranscendentalMath(polyad, SINH, state);
                return true;
            case COSH:
            case FQ_COSH:
                doTranscendentalMath(polyad, COSH, state);
                return true;
            case TANH:
            case FQ_TANH:
                doTranscendentalMath(polyad, TANH, state);
                return true;
            case FQ_IDENTITY_FUNCTION:
            case FQ_LONG_IDENTITY_FUNCTION:
            case IDENTITY_FUNCTION:
            case LONG_IDENTITY_FUNCTION:
                doIdentityFunction(polyad, state);
                return true;
            case ABS_VALUE:
            case FQ_ABS_VALUE:
                doAbs(polyad, state);
                return true;
            case RANDOM:
            case FQ_RANDOM:
                doRandom(polyad, state);
                return true;
            case RANDOM_STRING:
            case FQ_RANDOM_STRING:
                doRandomString(polyad, state);
                return true;
            case HASH:
            case FQ_HASH:
                doHash(polyad, state);
                return true;
            case TO_HEX:
            case FQ_TO_HEX:
                toFromhex(polyad, state, true);
                return true;
            case FROM_HEX:
            case FQ_FROM_HEX:
                toFromhex(polyad, state, false);
                return true;
            case NUMERIC_DIGITS:
            case FQ_NUMERIC_DIGITS:
                doNumericDigits(polyad, state);
                return true;
            case ENCODE_B64:
            case FQ_ENCODE_B64:
                doB64(polyad, state, true);
                return true;
            case DECODE_B64:
            case FQ_DECODE_B64:
                doB64(polyad, state, false);
                return true;
            case DATE_MS:
            case FQ_DATE_MS:
                doDates(polyad, state, true);
                return true;
            case DATE_ISO:
            case FQ_DATE_ISO:
                doDates(polyad, state, false);
                return true;
            case MOD:
            case FQ_MOD:
                doModulus(polyad, state);
                return true;
        }
        return false;
    }

    private BigDecimal evaluateBD(BigDecimal x, MathContext mathContext, String op) {

        switch (op) {
            case COSINE:
                return ch.obermuhlner.math.big.BigDecimalMath.cos(x, mathContext);
            case SINE:
                return ch.obermuhlner.math.big.BigDecimalMath.sin(x, mathContext);
            case TANGENT:
                return ch.obermuhlner.math.big.BigDecimalMath.tan(x, mathContext);
            case LOG_10:
                return ch.obermuhlner.math.big.BigDecimalMath.log10(x, mathContext);
            case LOG_E:
                return ch.obermuhlner.math.big.BigDecimalMath.log(x, mathContext);
            case EXP:
                return ch.obermuhlner.math.big.BigDecimalMath.exp(x, mathContext);
            case SINH:
                return ch.obermuhlner.math.big.BigDecimalMath.sinh(x, mathContext);
            case COSH:
                return ch.obermuhlner.math.big.BigDecimalMath.cosh(x, mathContext);
            case TANH:
                return ch.obermuhlner.math.big.BigDecimalMath.tanh(x, mathContext);

        }

        throw new UndefinedFunctionException("The function " + op + " is undefined");
    }

    private void doTranscendentalMath(Polyad polyad, String op, State state) {
        Object ob = polyad.evalArg(0, state);
        MathContext mc = state.getOpEvaluator().getMathContext();
        if (ob instanceof Long) {
            BigDecimal x = new BigDecimal((Long) ob);
            BigDecimal result = evaluateBD(x,mc, op);
            polyad.setEvaluated(true);
            polyad.setResult(result);
            polyad.setResultType(Constant.DECIMAL_TYPE);
            return;
        }
        if (ob instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) polyad.evalArg(0, state);
            BigDecimal result = evaluateBD(bd, mc, op);
            polyad.setEvaluated(true);
            polyad.setResult(result);
            polyad.setResultType(Constant.DECIMAL_TYPE);
            return;
        }

        if (ob instanceof StemVariable) {
            StemVariable outStem = new StemVariable();
            StemVariable stemVariable = (StemVariable) ob;
            for (String key : stemVariable.keySet()) {
                Object x = stemVariable.get(key);
                BigDecimal x0 = null;
                if (x instanceof Long) {
                    x0 = new BigDecimal((Long) x);
                }
                if (x instanceof BigDecimal) {
                    x0 = (BigDecimal) x;
                }
                if (x == null) {
                    throw new IllegalArgumentException("error \"" + x + "\" is not a number");
                }
                BigDecimal result = evaluateBD(x0, mc, op);
                outStem.put(key, result);

            }
            polyad.setEvaluated(true);
            polyad.setResult(outStem);
            polyad.setResultType(Constant.STEM_TYPE);
            return;
        }
        throw new IllegalArgumentException("Error: Unknown type.");
    }

    /**
     * The identity function returns its argument. Simple as that.
     *
     * @param polyad
     * @param state
     */
    protected void doIdentityFunction(Polyad polyad, State state) {
        if (polyad.getArgCount() != 1) {
            throw new IllegalArgumentException("error: " + IDENTITY_FUNCTION + " requires a single argument");
        }
        polyad.evalArg(0, state);
        polyad.setResult(polyad.getArguments().get(0).getResult());
        polyad.setResultType(polyad.getArguments().get(0).getResultType());
        polyad.setEvaluated(true);

    }

    protected void doAbs(Polyad polyad, State state) {
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                // If a long or decimal, take the absolute value. If anything else (e.g. a string) return argument.
                switch (Constant.getType(objects[0])) {
                    case Constant.LONG_TYPE:
                        r.result = Math.abs((Long) objects[0]);
                        r.resultType = Constant.LONG_TYPE;
                        break;
                    case Constant.DECIMAL_TYPE:
                        BigDecimal bd = (BigDecimal) objects[0];
                        r.result = bd.abs();
                        r.resultType = Constant.DECIMAL_TYPE;
                        break;
                    default:
                        r.result = objects[0];
                        r.resultType = polyad.getArguments().get(0).getResultType();
                }

                return r;
            }
        };
        process1(polyad, pointer, ABS_VALUE, state);
    }

    SecureRandom secureRandom = new SecureRandom();

    protected void doRandom(Polyad polyad, State state) {
        if (polyad.getArgCount() == 0) {
            polyad.setResult(secureRandom.nextLong());
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        Object result;
        int resultType = 0;

        // if the argument is a number return that many random numbers in a stem variable.
        Object arg = polyad.evalArg(0, state);
        ;
        if (arg instanceof Long) {
            int size = ((Long) arg).intValue();
            StemVariable stemVariable = new StemVariable();
            for (int i = 0; i < size; i++) {
                stemVariable.put(Integer.toString(i), secureRandom.nextLong());
            }
            result = stemVariable;
            resultType = Constant.STEM_TYPE;
        } else {
            // unknown type is ignored.
            result = secureRandom.nextLong();
            resultType = Constant.LONG_TYPE;
        }
        polyad.setResultType(resultType);
        polyad.setResult(result);
        polyad.setEvaluated(true);

    }

    protected void doNumericDigits(Polyad polyad, State state) {
        Long oldND = new Long((long) state.getOpEvaluator().getNumericDigits());
        polyad.setResult(oldND);
        polyad.setResultType(Constant.LONG_TYPE);

        if (polyad.getArgCount() == 0) {
            polyad.setEvaluated(true);
        } else {
            Object arg1 = polyad.evalArg(0, state);
            if (!isLong(arg1)) {
                throw new IllegalArgumentException("the supplied arguments was not an integer");
            }
            Long newND = (Long) arg1;
            state.getOpEvaluator().setNumericDigits(newND.intValue());
            polyad.setEvaluated(true);
        }
        return;
    }

    protected void doRandomString(Polyad polyad, State state) {
        int length = 16;

        if (0 < polyad.getArgCount()) {
            polyad.evalArg(0, state);
            Object obj = polyad.getArguments().get(0).getResult();
            if (obj instanceof Long) {
                length = ((Long) obj).intValue();
            } else {
                throw new IllegalArgumentException("The first argument must be an integer.");

            }
        }
        // Second optional argument is number of strings.
        int returnCount = 1;
        if (polyad.getArgCount() == 2) {
            polyad.evalArg(1, state);
            Object obj = polyad.getArguments().get(1).getResult();
            if (!isLong(obj)) {
                throw new IllegalArgumentException("The second argument must be an integer.");
            }

            if (obj instanceof Long) {
                returnCount = ((Long) obj).intValue();
                if (returnCount <= 0) {
                    returnCount = 1;
                }
            }
        }


        byte[] ba = new byte[length];
        if (returnCount == 1) {
            secureRandom.nextBytes(ba);
            String rc = Base64.encodeBase64URLSafeString(ba);
            polyad.setResult(rc);
            polyad.setResultType(Constant.STRING_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        // so more than one string needs to be returned.

        StemVariable stem = new StemVariable();
        for (int i = 0; i < returnCount; i++) {
            secureRandom.nextBytes(ba);
            String rc = Base64.encodeBase64URLSafeString(ba);
            stem.put((long) i, rc);
        }
        polyad.setResult(stem);
        polyad.setResultType(Constant.STEM_TYPE);
        polyad.setEvaluated(true);
        return;
    }

    protected void doHash(Polyad polyad, State state) {
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                if (objects[0] instanceof String) {
                    r.result = DigestUtils.sha1Hex(objects[0].toString());
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, HASH, state);
    }

    protected void doB64(Polyad polyad, State state, boolean isEncode) {
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                Long ll = null;

                if (objects[0] instanceof String) {
                    if (isEncode) {
                        r.result = Base64.encodeBase64URLSafeString(objects[0].toString().getBytes());
                    } else {
                        r.result = new String(Base64.decodeBase64(objects[0].toString()));
                    }
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, isEncode ? ENCODE_B64 : DECODE_B64, state);
    }

    protected void toFromhex(Polyad polyad, State state, boolean toHex) {
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                Long ll = null;

                if (objects[0] instanceof String) {
                    if (toHex) {
                        r.result = Hex.encodeHexString(objects[0].toString().getBytes());
                    } else {
                        try {
                            byte[] decoded = Hex.decodeHex(objects[0].toString().toCharArray());
                            r.result = new String(decoded);
                        } catch (Throwable t) {
                            r.result = "(error)";
                        }
                    }
                    r.resultType = Constant.STRING_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, toHex ? TO_HEX : FROM_HEX, state);
    }

    /**
     * Compute the modulus of two numbers, i.e. the remainder after division
     *
     * @param polyad
     */
    protected void doModulus(Polyad polyad, State state) {
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                if (areAllLongs(objects)) {
                    r.result = ((Long) objects[0]) % ((Long) objects[1]);
                    r.resultType = Constant.LONG_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArguments().get(0).getResultType();
                }
                return r;
            }
        };
        process2(polyad, pointer, MOD, state);
    }

    protected void doDates(Polyad polyad, State state, boolean isInMillis) {
        if (polyad.getArgCount() == 0) {
            // A niladic case. return the right date type.
            if (isInMillis) {
                Long now = new Date().getTime();
                polyad.setResult(now);
                polyad.setResultType(Constant.LONG_TYPE);
                polyad.setEvaluated(true);
            } else {
                Date d = new Date();
                String now = Iso8601.date2String(d);
                polyad.setResult(now);
                polyad.setResultType(Constant.STRING_TYPE);
                polyad.setEvaluated(true);

            }
            return;
        }
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                if (isInMillis) {
                    if (isLong(objects[0])) {
                        // do nothing. hand it back.
                        r.resultType = Constant.LONG_TYPE;
                        r.result = objects[0];
                    } else {
                        // assume its and ISO 8601 date and should lbe converted to millis
                        try {
                            Long ts = Iso8601.string2Date(objects[0].toString()).getTimeInMillis();
                            r.resultType = Constant.LONG_TYPE;
                            r.result = ts;
                        } catch (Throwable t) {
                            r.result = objects[0];
                            r.resultType = polyad.getArguments().get(0).getResultType();
                        }
                    }
                } else {
                    // work with ISO 8601 dates
                    if (isLong(objects[0])) {
                        String now = Iso8601.date2String((Long) objects[0]);
                        r.result = now;
                        r.resultType = Constant.STRING_TYPE;
                        return r;
                    } else {
                        r.result = objects[0];
                        r.resultType = polyad.getArguments().get(0).getResultType();
                    }
                }
                return r;

            }
        };
        process1(polyad, pointer, isInMillis ? DATE_MS : DATE_ISO, state);

    }
}
