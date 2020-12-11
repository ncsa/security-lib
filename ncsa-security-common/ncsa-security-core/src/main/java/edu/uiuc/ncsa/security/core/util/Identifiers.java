package edu.uiuc.ncsa.security.core.util;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities for Identifiers, such as creating unique identifiers.
 * <p>Created by Jeff Gaynor<br>
 * on 4/10/12 at  11:56 AM
 */
public class Identifiers {

    public static int DEFAULT_BYTE_COUNT = 16; // 16*8 = 128 bits length of identifiers
    public static int byteCount = -1; // illegal value to show it hasn't been set.


    public static int getByteCount() {
        if (byteCount <= 0) {
            byteCount = DEFAULT_BYTE_COUNT;
        }
        return byteCount;
    }

    public static void setByteCount(int newByteCount) {
        byteCount = newByteCount;
    }

    protected static SecureRandom getRandom() {
        if (random == null) {
            random = new SecureRandom();
        }
        return random;
    }

    static SecureRandom random;

    public static synchronized String getHexString() {
        byte[] bytes = new byte[getByteCount()];
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
        if (tail != null && 0 < tail.length() && !tail.startsWith("/")) {
            tail = "/" + tail;
        }

        return URI.create(caput + getHexString() + tail);
    }

    /**
     * Creates a new token of the form
     * <pre>
     *     caput/id?param1=&param2=...
     * </pre>
     *
     * @param caput
     * @param component
     * @param version
     * @param lifetime
     * @return
     */
    public static URI uniqueIdentifier(String caput,
                                       String component,
                                       String version,
                                       long lifetime
    ) {
        String r = caput +(caput.endsWith("/")?"":"/")+ getHexString();
        // Now add parameters in *specific* order
        String p = null;
        Date now = new Date();
        for (int i = 0; i < COUNT; i++) {
            switch (i) {
                case LIFETIME_INDEX:
                    if (0 < lifetime) {
                        // First bit is a test to make sure we don't get extra &'s in query string.
                        p = (p == null ? "" : p + "&") + LIFETIME_TAG + "=" + lifetime;
                    }
                    break;
                case TIMESTAMP_INDEX:
                    p = (p == null ? "" : p + "&") + TIMESTAMP_TAG + "=" + now.getTime();
                    break;
                case VERSION_INDEX:
                    if (!StringUtils.isTrivial(version)) {
                        p = (p == null ? "" : p + "&") + VERSION_TAG + "=" + version;
                    }
                    break;
                case TOKEN_TYPE_INDEX:
                    p = (p == null ? "" : p + "&") + TOKEN_TYPE_TAG + "=" + component;
                    break;
            }
        }

        r = r + "?" + p;
        return URI.create(r);

    }

    /**
     * Take a URI and turn the query string in to a set of parameters. These are returned as strings
     * so they may need some further processing.
     *
     * @param uri
     * @return
     */
    public static Map<String, String> getParameters(URI uri) {
        Map<String, String> out = new HashMap<>();

        String[] components = uri.getQuery().split("&");
        for (String x : components) {
            String[] y = x.split("=");
            if (y.length == 2) {
                out.put(y[0], y[1]);
            }
        }
        return out;
    }

    public static void main(String[] args) {
        System.out.println(uniqueIdentifier("https://foo.bar/oauth2/", "accessToken", VERSION_2_0_TAG, 1000000L));
    }

    public static final int TOKEN_TYPE_INDEX = 0;
    public static final int TIMESTAMP_INDEX = 1;
    public static final int VERSION_INDEX = 2;
    public static final int LIFETIME_INDEX = 3;
    public static final int COUNT = 4; // number of components. May change with version someday

    public static final String TOKEN_TYPE_TAG = "type";
    public static final String TIMESTAMP_TAG = "ts";
    public static final String VERSION_TAG = "version";
    public static final String LIFETIME_TAG = "lifetime";
    public static final String VERSION_2_0_TAG = "v2.0";
}
