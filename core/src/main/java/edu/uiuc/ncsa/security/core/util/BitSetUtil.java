package edu.uiuc.ncsa.security.core.util;

import java.util.BitSet;

/**
 * A utility for certain operations on {@link BitSet}.
 * <p>Created by Jeff Gaynor<br>
 * on 3/20/20 at  10:00 AM
 */
public class BitSetUtil {
    /**
     * Apply or between each element of this set. So if the elements are
     * <pre>
     * {a0,a1,a2,...,an}
     * </pre>
     * then the result is
     * <pre>
     *     a0 || a1 || a2 || ... || an
     * </pre>
     *
     * @return
     */
    public static boolean orCompress(BitSet bitSet) {
        return compress(bitSet, false);
    }

    /**
     * Ditto {@link #orCompress(BitSet)} except the returned value is
     * <pre>
     *     a0 &amp;&amp; a1 &amp;&amp; a2 &amp;&amp; ... &amp;&amp; an
     * </pre>
     *
     * @return
     */
    public static boolean andCompress(BitSet bitSet) {
        return compress(bitSet, true);
    }

    /**
     * Internal method for other compress operations.
     *
     * @param bitSet
     * @param doAnd
     * @return
     */
    protected static boolean compress(BitSet bitSet, boolean doAnd) {
        boolean ok = doAnd;
        for (int i = 0; i < bitSet.length(); i++) {
            ok = doAnd ? (ok && bitSet.get(i)) : (ok || bitSet.get(i));
        }
        return ok;
    }

    /**
     * Take a bit vector (representing the place values) and return the integer in base 2.
     * So if the bitset is {1,2,3,4,5} then this returns 0 + 2 + 4 + 8 + 16 + 32
     * @param bitSet
     * @return
     */
    public static int toInt(BitSet bitSet) {
        int rc = 0;
        int b = 1;

        for (int i = 0; i < bitSet.size(); i++) {
            if (bitSet.get(i)) {
                rc = rc + b;
            }
            b = b * 2; // exponentiation.
        }
        return rc;
    }
}
