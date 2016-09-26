package edu.uiuc.ncsa.security.core.util;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;

/**
 * Utilities for Identifiers, such as creating unique identifiers.
 * <p>Created by Jeff Gaynor<br>
 * on 4/10/12 at  11:56 AM
 */
public class Identifiers {

    public static int BYTE_COUNT = 16;

    protected static SecureRandom getRandom() {
        if (random == null) {
            random = new SecureRandom();
        }
        return random;
    }

    static SecureRandom random;

    public static synchronized String getHexString() {
        byte[] bytes = new byte[BYTE_COUNT];
        getRandom().nextBytes(bytes);
        BigInteger bi = new BigInteger(bytes);
        bi = bi.abs(); //make it always positive?? Doesn't that violate contract of secure random?
        return bi.toString(16); // 16 refers to the base.
    }

    public static URI uniqueIdentifier(String caput, String tail) {
        String h = "";
        if (caput != null) {
            h = caput.startsWith("/") ? "" : "/" + caput;
            h = h + (caput.endsWith("/") ? "" : "/");
        }
        if (tail!= null && 0 < tail.length() && !tail.startsWith("/")) {
            tail = "/" + tail;
        }

        return URI.create(caput + getHexString() + tail);
    }
}
