package edu.uiuc.ncsa.security.core.util;

import org.apache.commons.codec.binary.Base32;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

/**
 * Utility to do base 64 or base 32 encoding of strings.
 * Read the specs for <a href="https://datatracker.ietf.org/doc/html/rfc4648">base 32</a>
 * <p>
 *     This also provides methods to test if a given string is base 32 or base 64 encoded.
 * </p>
 * <p>Created by Jeff Gaynor<br>
 * on 10/21/21 at  4:06 PM
 */
public class TokenUtil {
    public static char trailingChar = '_';
    public static String padding32 = "" + trailingChar + trailingChar + trailingChar + trailingChar + trailingChar + trailingChar + trailingChar;
    protected static Base32 base32 = new Base32((byte) trailingChar); // replace standard trailing = with an underscore.
    public static String b64EncodeToken(String token) {
           if (isTrivial(token)) {
               return "";
           }
           return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(token.getBytes(StandardCharsets.UTF_8));
       }
    public static String b32EncodeToken(String token) {
        if (isTrivial(token)) {
            return "";
        }
        String x = base32.encodeToString(token.getBytes(StandardCharsets.UTF_8));
        // shave off padding.
        int index = x.indexOf(trailingChar);
        if (0 < index) {
            return x.substring(0, index);
        }
        return x;
    }
    public static String b64DecodeToken(String b64EncodedToken) {
           String out = new String(decodeBase64(b64EncodedToken));
           return out;
       }
    public static String b32DecodeToken(String b32EncodedToken) {
          b32EncodedToken = b32EncodedToken + padding32.substring(b32EncodedToken.length() % 8);
          String out = new String(base32.decode(b32EncodedToken));
          return out;
      }
    public static boolean isBase64(String x) {
        return org.apache.commons.codec.binary.Base64.isBase64(x);
    }

    static String b32_regex = "^[A-Z2-7]*+" + trailingChar + "*";
    static Pattern pattern = Pattern.compile(b32_regex);

    /**
     * NOTE we have tweaked this to use underscore for padding, not the standard equals sign since
     * we want nothing to be escaped.
     *
     * @param x
     * @return
     */
    public static boolean isBase32(String x) {
        return pattern.matcher(x.toUpperCase()).matches();
    }

}
