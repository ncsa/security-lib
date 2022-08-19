package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  5:12 PM
 */
/* **************
   Used generally in OA4MP and QDL.
   ************* */
public class HeaderUtils {
    public static final String BASIC_HEADER = "Basic";
    public static final String BEARER_HEADER = "Bearer";
    static boolean deepDebugOn = false;

    /**
     * This gets the tokens from the authorization header. There are several types and it is possible to have several
     * values passed in, so this returns an array of string rather than a single value. A downside with passing
     * along several values this way is there is no way to disambiguate them, e.g. a client id from a client secret.
     * If there is no authorization header or there are no tokens of the stated type, the returned value is an
     * empty list.
     *
     * @param request
     * @param type    The type of token, e.g. "Bearer" or "Basic"
     * @return
     */
    public static List<String> getAuthHeader(HttpServletRequest request, String type) {
        if (deepDebugOn) {
            ServletDebugUtil.printAllParameters(HeaderUtils.class, request);
            ServletDebugUtil.trace(HeaderUtils.class, "getAuthHeader: Getting type \"" + type + "\"");
        }
        Enumeration enumeration = request.getHeaders("authorization");
        if (deepDebugOn) {
            ServletDebugUtil.trace(HeaderUtils.class, "getAuthHeader: Header enumeration = \"" + enumeration + "\"");
        }
        ArrayList<String> out = new ArrayList<>();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (deepDebugOn) {
                ServletDebugUtil.trace(HeaderUtils.class, "getAuthHeader: Processing header = \"" + obj + "\"");
            }
            if (obj != null) {
                String rawToken = obj.toString();
                if (rawToken == null || 0 == rawToken.length()) {
                    // if there is no bearer token in the authorization header, it must be a parameter in the request.
                    // do nothing. No value
                } else {
                    // This next check is making sure that the type of token requested was sent.
                    //
                    if (rawToken.startsWith(type)) { // note the single space after the type
                        rawToken = rawToken.substring(rawToken.indexOf(" ") + 1);
                        out.add(rawToken);
                    }
                }

            }
        }
        if (deepDebugOn) {
            ServletDebugUtil.trace(HeaderUtils.class, "getAuthHeader: Returning  = \"" + out + "\"");
        }
        return out;
    }

    public static boolean hasBasicHeader(HttpServletRequest request) {
        return getBasicHeader(request) != null;
    }

    /**
     * This is the header itself that is base 64 encoded. To get the parsed header, use {@link #getCredentialsFromHeaders(HttpServletRequest)}
     * @param request
     * @return
     */
    public static String getBasicHeader(HttpServletRequest request) {
        List<String> authHeaders = getAuthHeader(request, "Basic");
        if (deepDebugOn) {
            ServletDebugUtil.trace(HeaderUtils.class, "getBasicHeader: returned auth headers = \"" + authHeaders + "\"");
        }
        if (authHeaders.isEmpty()) {
            return null;
        }
        return authHeaders.get(0);

    }

    public static String getBearerAuthHeader(HttpServletRequest request) {
        List<String> authHeaders = getAuthHeader(request, "Bearer");
        if (authHeaders.isEmpty()) {
            return null;
        }
        return authHeaders.get(0);

    }

    public static int ID_INDEX = 0;
    public static int SECRET_INDEX = 1;

    public static String[] getCredentialsFromHeaders(HttpServletRequest request, String type) throws UnsupportedEncodingException {
        if (deepDebugOn) {
            ServletDebugUtil.trace(HeaderUtils.class, "getCredentialsFromHeaders: type = \"" + type + "\"");
        }
        type = type.trim();
        // assume the client id and secret are in the headers.
        String header64 = null;
        if (type.equals(BASIC_HEADER)) {
            header64 = getBasicHeader(request);
        }
        if (type.equals(BEARER_HEADER)) {
            header64 = getBearerAuthHeader(request);
        }
        if (header64 == null) {
            throw new IllegalArgumentException("Error: Unknown authorization method.");
        }
        String[] out = new String[2];

        // semantics are that this is base64.encode(URLEncode(id):URLEncode(secret))
        byte[] headerBytes = Base64.decodeBase64(header64);
        if (headerBytes == null || headerBytes.length == 0) {
            if (deepDebugOn) {
                ServletDebugUtil.trace(HeaderUtils.class, "doIt: no secret, throwing exception.");
                throwException("Missing secret");
            }
        }
        String header = new String(headerBytes);
        if (deepDebugOn) {
            ServletDebugUtil.trace(HeaderUtils.class, " received authz header of " + header);
        }
        int lastColonIndex = header.lastIndexOf(":");
        if (lastColonIndex == -1) {
            // then this is not in the correct format.
            //      DebugUtil.trace(this, "doIt: the authorization header is not in the right format, throwing exception.");
            throwException("the authorization header is not in the right format");

        }
        String id = URLDecoder.decode(header.substring(0, lastColonIndex), "UTF-8");
         out[ID_INDEX] = id;

         String rawSecret = URLDecoder.decode(header.substring(lastColonIndex + 1), "UTF-8");

         out[SECRET_INDEX] = rawSecret;
         if(deepDebugOn) {
             ServletDebugUtil.trace(HeaderUtils.class, "getCredentialsFromHeaders: returning  " + id + ", " + rawSecret);
         }
         return out;
    }

    public static String[] getCredentialsFromHeaders(HttpServletRequest request) throws UnsupportedEncodingException {
        return getCredentialsFromHeaders(request, "Basic"); // default
    }

    public static String getSecretFromHeaders(HttpServletRequest request) throws UnsupportedEncodingException {
        return getCredentialsFromHeaders(request)[SECRET_INDEX];
    }

    public static Identifier getIDFromHeaders(HttpServletRequest request) throws UnsupportedEncodingException {
        String[] creds = getCredentialsFromHeaders(request);
        if (creds == null || creds.length == 0) {
            return null;
        }
        return BasicIdentifier.newID(creds[ID_INDEX]);

    }
    protected static void throwException(String message){
        throw new IllegalArgumentException(message);
    }
}