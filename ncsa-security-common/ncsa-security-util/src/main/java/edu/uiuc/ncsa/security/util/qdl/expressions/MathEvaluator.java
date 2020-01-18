package edu.uiuc.ncsa.security.util.qdl.expressions;

import edu.uiuc.ncsa.security.util.qdl.util.StemVariable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  9:18 AM
 */
public class MathEvaluator extends StringEvaluator {
    public static final int MATH_FUNCTION_BASE_VALUE = 1000;
    public static String ABS_VALUE = "abs";
    public static final int ABS_VALUE_TYPE = 1 + MATH_FUNCTION_BASE_VALUE;

    public static String RANDOM = "random";
    public static final int RANDOM_TYPE = 2 + MATH_FUNCTION_BASE_VALUE;

    public static String RANDOM_STRING = "random_string";
    public static final int RANDOM_STRING_TYPE = 3 + MATH_FUNCTION_BASE_VALUE;

    public static String HASH = "hash";
    public static final int HASH_TYPE = 4 + MATH_FUNCTION_BASE_VALUE;

    public static String TO_HEX = "to_hex";
    public static final int TO_HEX_TYPE = 5 + MATH_FUNCTION_BASE_VALUE;

    public static String FROM_HEX = "from_hex";
    public static final int FROM_HEX_TYPE = 6 + MATH_FUNCTION_BASE_VALUE;

    public static String TIMESTAMP = "timestamp";
    public static final int TIMESTAMP_TYPE = 7 + MATH_FUNCTION_BASE_VALUE;

    public static String DECODE_B64 = "decode_b64";
    public static final int DECODE_B64_TYPE = 8 + MATH_FUNCTION_BASE_VALUE;

    public static String ENCODE_B64 = "encode_b64";
    public static final int ENCODE_B64_TYPE = 9 + MATH_FUNCTION_BASE_VALUE;


    @Override
    public int getType(String name) {
        int out = super.getType(name);
        if (out != UNKNOWN_VALUE) return out;

        if (name.equals(ABS_VALUE)) return ABS_VALUE_TYPE;
        if (name.equals(RANDOM)) return RANDOM_TYPE;
        if (name.equals(RANDOM_STRING)) return RANDOM_STRING_TYPE;
        if (name.equals(HASH)) return HASH_TYPE;
        if (name.equals(TO_HEX)) return TO_HEX_TYPE;
        if (name.equals(FROM_HEX)) return FROM_HEX_TYPE;
        if (name.equals(ENCODE_B64)) return ENCODE_B64_TYPE;
        if (name.equals(DECODE_B64)) return DECODE_B64_TYPE;
        if (name.equals(TIMESTAMP)) return TIMESTAMP_TYPE;
        return UNKNOWN_VALUE;
    }


    @Override
    public void evaluate(Polyad polyad) {
        switch (polyad.getOperatorType()) {
            case ABS_VALUE_TYPE:
                doAbs(polyad);
                return;
            case RANDOM_TYPE:
                doRandom(polyad);
                return;
            case RANDOM_STRING_TYPE:
                doRandomString(polyad);
                return;
            case HASH_TYPE:
                doHash(polyad);
                return;
            case TO_HEX_TYPE:
                toFromhex(polyad, true);
                return;
            case FROM_HEX_TYPE:
                toFromhex(polyad, false);
                return;
            case ENCODE_B64_TYPE:
                doB64(polyad, true);
                return;
            case DECODE_B64_TYPE:
                doB64(polyad, false);
                return;
            case TIMESTAMP_TYPE:
                return;
        }
        super.evaluate(polyad);
    }

    protected void doAbs(Polyad polyad) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
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
        process1(polyad, pointer, ABS_VALUE);
    }

    SecureRandom secureRandom = new SecureRandom();

    protected void doRandom(Polyad polyad) {
        if (polyad.getArgumments().size() == 0) {
            polyad.setResult(secureRandom.nextLong());
            polyad.setResultType(Constant.LONG_TYPE);
            polyad.setEvaluated(true);
            return;
        }
        Object result;
        int resultType = 0;

        // if the argument is a number return that many random numbers in a stem variable.
        Object arg = polyad.getArgumments().get(0).evaluate();
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

    protected void doRandomString(Polyad polyad) {
        int length = 16;
        if (polyad.getArgumments().size() == 1) {
            polyad.getArgumments().get(0).evaluate();
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

    protected void doHash(Polyad polyad) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
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
        process1(polyad, pointer, HASH);
    }

    protected void doB64(Polyad polyad, boolean isEncode) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
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
        process1(polyad, pointer, isEncode ? ENCODE_B64 : DECODE_B64);
    }

    protected void toFromhex(Polyad polyad, boolean toHex) {
        fPointer pointer = new fPointer() {
            @Override
            public fpResult process(Object... objects) {
                fpResult r = new fpResult();
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
        process1(polyad, pointer, toHex ? TO_HEX : FROM_HEX);
    }
}
