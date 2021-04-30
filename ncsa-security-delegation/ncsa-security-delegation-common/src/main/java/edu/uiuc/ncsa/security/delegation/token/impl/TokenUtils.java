package edu.uiuc.ncsa.security.delegation.token.impl;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifierProvider;
import edu.uiuc.ncsa.security.core.util.Identifiers;
import edu.uiuc.ncsa.security.delegation.token.Token;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base32;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;
import static org.apache.commons.codec.binary.Base64.decodeBase64;

/**
 * Utilities for working with tokens.
 * <h2>version 1 tokens</h2>
 * these are of the form:
 * <pre>
 *     scheme + scheme specific part + //host / + component/  + uid/ + create timestamp
 * </pre>
 * E.g.
 * https://cilogon.org/oauth2/refreshToken/77e2b3dcc7934ffa455d7f49629bad61/1605827034976
 * <h2>Version 2</h2>
 * These are of the form:
 * scheme + scheme specific part + // + host / + version/ + component/  + uid/ + create timestamp/ + lifetime
 * <pre>
 * </pre>
 * E.g.
 * https://cilogon.org/oauth2/v2.0/refreshToken/77e2b3dcc7934ffa455d7f49629bad61/1605827034976/1000000
 * <p>Created by Jeff Gaynor<br>
 * on 11/24/20 at  9:37 AM
 */
public class TokenUtils {
    public static char trailingChar = '_';
    public static String padding32 = "" + trailingChar + trailingChar + trailingChar + trailingChar + trailingChar + trailingChar + trailingChar;
    static Base32 base32 = new Base32((byte) trailingChar); // replace standard trailing = with an underscore.

    public static String b64EncodeToken(Token token) {
        if (token == null) {
            return null;
        }
        return b64EncodeToken(token.getToken());
    }

    public static String b64EncodeToken(String token) {
        if (isTrivial(token)) {
            return "";
        }
        return org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(token.getBytes(StandardCharsets.UTF_8));
    }

    public static String b32EncodeToken(Token token) {
        if (token == null) {
            return null;
        }
        return b32EncodeToken(token.getToken());
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

    public static void main(String[] args) {
        IdentifierProvider ip = new IdentifierProvider<Identifier>(URI.create("https://cilogon.org/oauth2"), "my_token", true) {
        };
        Date now = new Date();
        String version = "2.0.0";
        long lifetime = 11 * 24 * 3600 * 1000L; // 11 days
        //Identifiers.setByteCount(8);
        String uid = Identifiers.getHexString();
        String host = "https://cilogon.org/oauth2/";
        String token = "my_token";
        System.out.println(ip.get());
        JSONObject json = new JSONObject();
        json.put("version", version);
        json.put("uid", uid);
        json.put("timestamp", now.getTime());
        json.put("lifetime", lifetime);
        System.out.println(json.toString(1));
        String jj = Base64.getEncoder().encodeToString(json.toString().getBytes());
        URI urij = URI.create(host + token + "/" + jj);
        System.out.println(urij);
        System.out.println("length = " + urij.toString().length());
        String q = host + token + "?version=" + version + "&uid=" + uid + "&ts=" + now.getTime() + "&lifetime=" + lifetime;
        URI uriQ = URI.create(q);
        print(uriQ);

        q = host + uid + "?version=" + version + "&token_type=" + token + "&ts=" + now.getTime() + "&lifetime=" + lifetime;
        uriQ = URI.create(q);
        print(uriQ);

        q = host + token + "/" + uid + "/v" + version + "/" + now.getTime() + "/" + lifetime;
        uriQ = URI.create(q);
        print(uriQ);

        q = host + token + "/" + uid + "/" + now.getTime() + "?version=" + version + "&lifetime=" + lifetime;
        uriQ = URI.create(q);
        print(uriQ);

        System.out.println(b32DecodeToken("NB2HI4DTHIXS6ZDFOYXGG2LMN5TW63RON5ZGOL3PMF2XI2BSF42TKYTGMQ4TGNRSMZRGKM3GGU2DMNBXHFRTCMBUGQ3DMYLCMRSGEP3UPFYGKPLSMVTHEZLTNBKG623FNYTHI4Z5GE3DCOJVGU3DCNJXHAYTIJTWMVZHG2LPNY6XMMROGATGY2LGMV2GS3LFHUZDKOJSGAYDAMBQGA"));
    }

    static void print(URI uriQ) {
        System.out.println(uriQ);
        System.out.println("length = " + uriQ.toString().length());
        System.out.println("is uri base32 = " + isBase32(uriQ.toString())); // always false, just multiple tests

        String a = b32EncodeToken(uriQ.toString());
        System.out.println("base32 to = " + a);
        System.out.println("   base32 length = " + a.length());
        System.out.println("       is base32 = " + isBase32(a));
        String decoded = b32DecodeToken(a);
        System.out.println("equal on decode? = " + decoded.equals(uriQ.toString()));

    }
}
