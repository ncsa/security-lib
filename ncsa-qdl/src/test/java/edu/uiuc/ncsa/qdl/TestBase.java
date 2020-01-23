package edu.uiuc.ncsa.qdl;

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Random;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Nov 27, 2010 at  2:08:07 PM
 */
abstract public class TestBase extends TestCase {
    /*
    This is actually just a copy of the test base class in security-util, but it cuts out
    a dependency.
     */
    public static int randomStringLength = 8; // default length for random strings
    public int count = 5; // on tests with loops, this sets max reps.

    public static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    static Random random;

    public static String getRandomString(int length) {
        // so approximate how long the result will be and add in (at most) 2 characters.
        byte[] bytes = new byte[(int) (Math.round(Math.ceil(length * .75)) + 1)];
        getRandom().nextBytes(bytes);
        // Have to be careful to use only URL safe encoding or random errors can start occurring,
        // especially if using these to make other urls!
        return Base64.encodeBase64URLSafeString(bytes).substring(0, length);
    }

    public static String getRandomString() {
        return getRandomString(randomStringLength);
    }


    public static URI createToken(String infix) {
        return URI.create("ncsa:cilogon:test:" + infix + "/" + getRandomString(12) + "/" + System.currentTimeMillis());
    }
    protected boolean testNumberEquals(Object arg1, Object arg2){
        if((arg1 instanceof Long) && (arg2 instanceof Long)){
            return arg1.equals(arg2);
        }
        BigDecimal left;
        BigDecimal right;
        if(arg1 instanceof Long){
             left = new BigDecimal((Long)arg1);
        }else{
            left = (BigDecimal)arg1;
        }
        if(arg2 instanceof Long){
             right = new BigDecimal((Long)arg2);
        }else{
            right = (BigDecimal)arg2;
        }

        BigDecimal result = left.subtract(right);
        return result.compareTo(BigDecimal.ZERO) == 0;
    }
}
