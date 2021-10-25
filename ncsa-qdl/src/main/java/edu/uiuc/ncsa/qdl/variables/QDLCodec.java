package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

/**
 * This will convert a string and encode or decode all characters that are not [a-z][A-Z]_. The encoding
 * is URL standard escaping in which the "%" is replaced by a "$" so that it forms a valid
 * variable.  E.g.,
 * <pre>
 *       ab&(*c -->  ab$26$28$2Ac
 * </pre>
 * Note that unlike certain forms of URL encoding, a blank is not turned in to a "+" but
 * encoded as "$20". Many utilities that claim to URL encode strings do not, but actually do
 * URL form encoding which permits that.
 * <p>Created by Jeff Gaynor<br>
 * on 3/9/20 at  6:13 AM
 */
public class QDLCodec {
    public String encode(String token) {
        if (token == null || token.isEmpty()) return token;
        String encoded = null;
        URLCodec codec = new URLCodec();
        try {
            //replace + to %20
            encoded = codec.encode(token).replace("+", "%20");
            encoded = encoded.replace("$", "%24");
            encoded = encoded.replace("*", "%2A");
            encoded = encoded.replace("-", "%2D");
            encoded = encoded.replace(".", "%2E");
            encoded = encoded.replace("%", "$");

        } catch (EncoderException e) {
            throw new QDLException("Error: Could not encode string:" + e.getMessage(), e);
        }
        return encoded;
    }

    public String decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) return encoded;
        URLCodec codec = new URLCodec();
        String token = null;
        try {
            token = encoded.replace("$", "%");

            token = codec.decode(token);
        } catch (DecoderException e) {
            throw new QDLException("invalid escape sequence for'" + encoded + "'" , e);
        }
        return token;
    }

    public static void main(String[] args) {
        try {
            String a = "abc$%^.*_-";
            QDLCodec c = new QDLCodec();
            String encoded = c.encode(a);
            String decoded = c.decode(encoded);
            System.out.println(a + " -> " + encoded);
            System.out.println(a + " =? " + (a.equals(decoded)) + ": " + decoded);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
