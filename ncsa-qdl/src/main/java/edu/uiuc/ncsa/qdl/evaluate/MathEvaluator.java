package edu.uiuc.ncsa.qdl.evaluate;

import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class MathEvaluator extends AbstractFunctionEvaluator {
    public static final int MATH_FUNCTION_BASE_VALUE = 1000;
    public static final String ABS_VALUE = "abs";
    public static final int ABS_VALUE_TYPE = 1 + MATH_FUNCTION_BASE_VALUE;

    public static final String RANDOM = "random";
    public static final int RANDOM_TYPE = 2 + MATH_FUNCTION_BASE_VALUE;

    public static final String RANDOM_STRING = "random_string";
    public static final int RANDOM_STRING_TYPE = 3 + MATH_FUNCTION_BASE_VALUE;

    public static final String HASH = "hash";
    public static final int HASH_TYPE = 4 + MATH_FUNCTION_BASE_VALUE;

    public static final String TO_HEX = "to_hex";
    public static final int TO_HEX_TYPE = 5 + MATH_FUNCTION_BASE_VALUE;

    public static final String FROM_HEX = "from_hex";
    public static final int FROM_HEX_TYPE = 6 + MATH_FUNCTION_BASE_VALUE;

    public static final String DATE_MS = "date_ms";
    public static final int DATE_MS_TYPE = 7 + MATH_FUNCTION_BASE_VALUE;


    public static final String DECODE_B64 = "decode_b64";
    public static final int DECODE_B64_TYPE = 8 + MATH_FUNCTION_BASE_VALUE;

    public static final String ENCODE_B64 = "encode_b64";
    public static final int ENCODE_B64_TYPE = 9 + MATH_FUNCTION_BASE_VALUE;

    public static final String MOD = "mod";
    public static final int MOD_TYPE = 10 + MATH_FUNCTION_BASE_VALUE;

    public static final String DATE_ISO = "date_iso";
    public static final int DATE_ISO_TYPE = 11 + MATH_FUNCTION_BASE_VALUE;
    public static String FUNC_NAMES[] = new String[]{ABS_VALUE,RANDOM,RANDOM_STRING,HASH,TO_HEX,
            FROM_HEX,DATE_MS,DATE_ISO,DECODE_B64,ENCODE_B64,MOD,DATE_ISO};
    public TreeSet<String> listFunctions() {
          TreeSet<String> names = new TreeSet<>();
          for (String key : FUNC_NAMES) {
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
                case ABS_VALUE:
                    return ABS_VALUE_TYPE;
                case RANDOM:
                    return RANDOM_TYPE;
                case RANDOM_STRING:
                    return RANDOM_STRING_TYPE;
                case HASH:
                    return HASH_TYPE;
                case TO_HEX:
                    return TO_HEX_TYPE;
                case FROM_HEX:
                    return FROM_HEX_TYPE;
                case ENCODE_B64:
                    return ENCODE_B64_TYPE;
                case DECODE_B64:
                    return DECODE_B64_TYPE;
                case DATE_MS:
                    return DATE_MS_TYPE;
                case DATE_ISO:
                    return DATE_ISO_TYPE;
                case MOD:
                    return MOD_TYPE;
            }
        return EvaluatorInterface.UNKNOWN_VALUE;
    }


    @Override
    public boolean evaluate(Polyad polyad, State state) {
        switch (polyad.getName()) {
            case ABS_VALUE:
                doAbs(polyad, state);
                return true;
            case RANDOM:
                doRandom(polyad, state);
                return true;
            case RANDOM_STRING:
                doRandomString(polyad, state);
                return true;
            case HASH:
                doHash(polyad, state);
                return true;
            case TO_HEX:
                toFromhex(polyad, state, true);
                return true;
            case FROM_HEX:
                toFromhex(polyad, state, false);
                return true;
            case ENCODE_B64:
                doB64(polyad, state, true);
                return true;
            case DECODE_B64:
                doB64(polyad, state, false);
                return true;
            case DATE_MS:
                doDates(polyad, state, true);
                return true;
            case DATE_ISO:
                doDates(polyad, state, false);
                return true;
            case MOD:
                doModulus(polyad, state);
                return true;
        }
        return false;
    }

    protected void doAbs(Polyad polyad, State state) {
        AbstractFunctionEvaluator.fPointer pointer = new AbstractFunctionEvaluator.fPointer() {
            @Override
            public AbstractFunctionEvaluator.fpResult process(Object... objects) {
                AbstractFunctionEvaluator.fpResult r = new AbstractFunctionEvaluator.fpResult();
                if (objects[0] instanceof Long) {
                    r.result = Math.abs((Long) objects[0]);
                    r.resultType = Constant.LONG_TYPE;
                } else {
                    r.result = objects[0];
                    r.resultType = polyad.getArgumments().get(0).getResultType();
                }
                return r;
            }
        };
        process1(polyad, pointer, ABS_VALUE, state);
    }

    SecureRandom secureRandom = new SecureRandom();

    protected void doRandom(Polyad polyad, State state) {
        if (polyad.getArgumments().size() == 0) {
            polyad.setResult(secureRandom.nextLong());
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        Object result;
        int resultType = 0;

        // if the argument is a number return that many random numbers in a stem variable.
        Object arg = polyad.evalArg(0,state);;
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

    protected void doRandomString(Polyad polyad, State state) {
        int length = 16;
        if (polyad.getArgumments().size() == 1) {
            polyad.evalArg(0,state);;
            Object obj = polyad.getArgumments().get(0).getResult();
            if (obj instanceof Long) {
                length = ((Long) obj).intValue();
            }
        }
        byte[] ba = new byte[length];
        secureRandom.nextBytes(ba);
        // now we need to make a string.
        BigInteger bigInteger = new BigInteger(ba);
        bigInteger = bigInteger.abs();
        polyad.setResult(bigInteger.toString(16).toUpperCase());
        polyad.setResultType(Constant.STRING_TYPE);
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
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
                    r.resultType = polyad.getArgumments().get(0).getResultType();
                }
                return r;
            }
        };
        process2(polyad, pointer, MOD, state);
    }

    protected void doDates(Polyad polyad, State state, boolean isInMillis) {
        if (polyad.getArgumments().size() == 0) {
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
                            r.resultType = polyad.getArgumments().get(0).getResultType();
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
                        r.resultType = polyad.getArgumments().get(0).getResultType();
                    }
                }
                return r;

            }
        };
        process1(polyad, pointer, isInMillis ? DATE_MS : DATE_ISO, state);

    }
}
