package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        return !getAuthHeader(request, "Basic").isEmpty();
    }

    public static boolean hasBearerHeader(HttpServletRequest request) {
        return !getAuthHeader(request, "Bearer").isEmpty();
    }

    /**
     * This is the header itself that is base 64 encoded. To get the parsed header, use {@link #getCredentialsFromHeaders(HttpServletRequest)}
     *
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
            throwException("Missing/unknown authorization method.");
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
        if (deepDebugOn) {
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

    protected static void throwException(String message) {
        throw new IllegalArgumentException(message);
    }

    public static JSONObject headerToJSON(HttpServletRequest httpServletRequest) {
        return headerToJSON(httpServletRequest, new ArrayList<>());
    }


    /**
     * Takes the request and converts it to JSON, normalizing the header names
     * to lower case. Any name on the filter list is omitted.
     * Note that the filter names are assumed to be lower case.
     * This puts single values as strings and aggregates  as arrays of strings. Used in OA4MP!
     *
     * @param httpServletRequest
     * @param filter
     * @return
     */
    public static JSONObject headerToJSON(HttpServletRequest httpServletRequest, List<String> filter) {
        Enumeration<String> names = httpServletRequest.getHeaderNames();
        JSONObject json = new JSONObject();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = httpServletRequest.getHeaders(name);
            JSONArray array = new JSONArray();
            while (values.hasMoreElements()) {
                array.add(values.nextElement());
            }
            // As per RFC 7230#3.2 header names are case-insensitive. Normalize to lower case
            // Next bit implicitly omits empty headers.
            name = name.toLowerCase();
            if (filter.isEmpty() || !filter.contains(name)) {
                if (array.size() == 1) {
                    json.put(name.toLowerCase(), array.get(0));
                }
                if (1 < array.size()) {
                    json.put(name.toLowerCase(), array);
                }
            }
        }
        return json;
    }

    /**
     * Gets the first parameter value for the given key
     *
     * @param request
     * @param key
     * @return
     */
    public static String getFirstParameterValue(HttpServletRequest request, String key) {
        Object obj = request.getParameter(key);

        if (obj == null) return null;
        if (!obj.getClass().isArray()) {
            return obj.toString();
        }
        // If not null and not a singleton, this is an array of strings. Return the zero-th
        String[] values = (String[]) obj;
        if (values.length == 0) return null;
        return values[0];
    }

    /**
     * Utility to extract all of the parameters from a request. Since the parameters are all
     * string arrays, this takes a little finagling. Generally we do not support multiple values
     * for parameters, so taking the first is reasonable.
     *
     * @param req
     * @return
     */
    public static Map<String, String> getFirstParameters(HttpServletRequest req) {
        HashMap<String, String> map = new HashMap<>();
        for (Object key : req.getParameterMap().keySet()) {
            map.put(key.toString(), getFirstParameterValue(req, key.toString()));
        }
        return map;
    }

    /**
     * This will get all the parameters for a given key. It is possible that there are delimiters for these, and it
     * the supplied delimiter is not null, these will be further split. E.g.
     * <pre>
     *     scope=openid&scope=profile&scope=org.oa4mp%3Auserinfo%20read%3A%2Fpublic%2Fbob&...
     * </pre>
     * Then invoking this with a delimiter of " " (a blank) yields the scope list of
     * <pre>
     *     [openid,profile,org.oa4mp:userinfo,read:/public/bob]
     * </pre>
     * Note that this returns faithfully what was passed, so if a value is repeated, that is returned too.
     *
     * @param req
     * @param key
     * @param delimiter
     * @return
     */
    public static List<String> getParameters(HttpServletRequest req, String key, String delimiter) {
        List<String> output = new ArrayList<>();
        String[] values = req.getParameterValues(key);
        if (values == null) {
            return new ArrayList<>();
        }
        //String[] values = new String[]{"openid","   profile   ", "  org.oa4mp:userinfo   read:/public/bob   "};
        boolean trivialDelimiter = delimiter == null || delimiter.isEmpty();
        for (String value : values) {
            if (!trivialDelimiter) {
                StringTokenizer st = new StringTokenizer(value, delimiter);
                while (st.hasMoreTokens()) {
                    output.add(st.nextToken().trim());
                }
            } else {
                output.add(value.trim());
            }
        }
        return output;
    }

    /**
     * Debug: Echo the request to standard out. This is a very low level debugging utility.
     *
     * @param httpRequestBase
     * @throws IOException
     */
    public static void echoRequest(HttpRequestBase httpRequestBase) throws IOException {
        System.out.println("\n----- Echo request -----");
        System.out.println(httpRequestBase);
        if (httpRequestBase instanceof HttpPost) {
            HttpPost httpPost = (HttpPost) httpRequestBase;
            System.out.println("    Content-Type: " + httpPost.getEntity().getContentType());
            System.out.println("Content-Encoding: " + httpPost.getEntity().getContentEncoding());
            System.out.println("  Content-Length: " + httpPost.getEntity().getContentLength());
            InputStream inputStream = httpPost.getEntity().getContent();
            // We must tread carefully here or the stream will be consumed and un-usable.
            // If it's a byte array, we can do this safely
            if (inputStream instanceof ByteArrayInputStream) {
                String content = new String(((ByteArrayInputStream) inputStream).readAllBytes(), "UTF-8");
                System.out.println("         Content:\n" + content);
            } else {
                System.out.println("         Content: (cannot read " + inputStream.getClass().getCanonicalName() + ")");
            }
        }

        System.out.println("----- End echo request -----\n");
    }

    /**
     * Debug: If the response was an error, echo it
     *
     * @param t
     */
    public static void echoErrorResponse(Throwable t) {
        System.out.println("\n----- Echo response -----");
        System.out.println("ERROR!");
        System.out.println(t.getMessage());
        System.out.println("----- End echo response -----\n");
    }

    // next couple of methods are for https://github.com/ncsa/security-lib/issues/59

    /**
     * Debug: echo the response. This means reading the stream, so return the content if any.
     * If there is {@link HttpStatus#SC_NO_CONTENT},return the empty string.
     *
     * @param entity     - may be null
     * @param statusLine - must not be null
     * @return
     * @throws IOException
     */
    public static String echoResponse(HttpEntity entity, StatusLine statusLine) throws IOException {
        System.out.println("\n----- Echo response -----");
        if (entity != null && entity.getContentType() != null) {
            System.out.println("Content Type: " + entity.getContentType().getValue());
        } else {
            System.out.println("Content Type: (none)");
        }
        if (statusLine.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
            System.out.println("----- End echo response -----\n");
            return "";
        }
        String x;
        if (entity == null) {
            x = "(no content)";
        } else {
            x = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        }
        System.out.println("Status: " + statusLine.getStatusCode());
        System.out.println("Raw Response: \n" + x);
        System.out.println("----- End echo response -----\n");
        return x;
    }
}