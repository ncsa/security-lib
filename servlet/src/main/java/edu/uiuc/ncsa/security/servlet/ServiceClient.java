package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.ConnectionException;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import edu.uiuc.ncsa.security.util.ssl.VerifyingHTTPClientFactory;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * This class is a client that talks to a server. It manages a client pool and does a get based on
 * pairs of strings, which are assumed to be of the form {{key1,value1},{key2,value2},...}
 * <p>Created by Jeff Gaynor<br>
 * on 5/21/12 at  1:43 PM
 */
public class ServiceClient {
    SSLConfiguration sslConfiguration;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    boolean verbose = false;

    public ServiceClient(URI address, SSLConfiguration sslConfiguration) {
        this.address = address;
        this.sslConfiguration = sslConfiguration;
    }

    URI address;

    /**
     * Basic default service client that uses the java keystore only.
     *
     * @param address
     */
    public ServiceClient(URI address) {
        this.address = address;
        SSLConfiguration sslConfiguration1 = new SSLConfiguration();
        sslConfiguration1.setUseDefaultJavaTrustStore(true);
        this.sslConfiguration = sslConfiguration1;
    }

    public URI host(URI... x) {
        if (0 < x.length) address = x[0];
        return address;
    }


    protected Pool<HttpClient> clientPool = new Pool<HttpClient>() {

        VerifyingHTTPClientFactory f;

        public VerifyingHTTPClientFactory getF() {
            if (f == null) {
                f = new VerifyingHTTPClientFactory(new MyLoggingFacade(getClass().getSimpleName()), sslConfiguration);
            }
            return f;
        }

        @Override
        public HttpClient create() {
            try {
                HttpClient x = getF().getClient(address.getHost()); // otherwise the client has the *entire* address.
                totalCreated++;
                return x;
            } catch (IOException | NoSuchAlgorithmException | KeyStoreException e) {
                throw new GeneralException("Error getting https-aware client");
            }
        }

        @Override
        public void destroy(HttpClient HttpClient) {
            // stateless so nothing to do really.
            // Don't call this, but call doDestroy that actually does the bookkeeping.
         /*   inUse--;
            totalDestroyed++;*/
        }
    };

    public static String ENCODING = "UTF-8";

    public static String encode(String x) throws UnsupportedEncodingException {
        if (x == null) return "";
        String xx = URLEncoder.encode(x, ENCODING);
        return xx;
    }

    public static String decode(String x) throws UnsupportedEncodingException {
        return URLDecoder.decode(x, ENCODING);
    }

    public static String convertToStringRequest(String host, Map m) {
        int i = 0;
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        // Dynamically create the entries, then turn them in to an array (which is required for a library later).
        for (Object o : m.keySet()) {
            Object v = m.get(o);
            if (v != null) {
                if (v instanceof List) {
                    // If its a list, repeatedly add it with the same key
                    List<String> list = (List<String>) v;
                    for (String xx : list) {
                        keyList.add(o.toString());
                        valueList.add(xx);
                        //strings[i][0] = o.toString();
                        //strings[i++][1] = xx;
                    }
                } else {
                    keyList.add(o.toString());
                    valueList.add(v.toString());
                }
            }
        }
        int size = keyList.size();

        String[][] strings = new String[size][2];
        for (int j = 0; j < size; j++) {
            strings[j][0] = keyList.get(j);
            strings[j][1] = valueList.get(j);
        }

        return convertToStringRequest(host, strings);
    }

    public String doGet(Map m, String id, String secret) {
        Map headers = getHeaders(m);
        HttpGet httpGet = new HttpGet(convertToStringRequest(host().toString(), m));
        addHeaders(httpGet, headers); // Fix https://github.com/ncsa/security-lib/issues/43
        return doRequest(httpGet, id, secret);
        //return doGet(convertToStringRequest(host().toString(), m), id, secret);
    }

    public String doGet(Map m) {
        Map headers = getHeaders(m);
        HttpGet httpGet = new HttpGet(convertToStringRequest(host().toString(), m));
        addHeaders(httpGet, headers); //Fix https://github.com/ncsa/security-lib/issues/43
        return doRequest(httpGet);
        //return doGet(convertToStringRequest(host().toString(), m));
    }

    /**
     * Does a POST using only the map. This is useful if there are other authentication methods
     * used that are encoded as parameters (e.g. RFC 7523).
     *
     * @param m
     * @return
     */
    public String doPost(Map m) {
        HttpPost post = new HttpPost(host().toString());
        addHeadersFromParameters(post, m);
        List<NameValuePair> params = getNameValuePairs(m);
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException("error encoding form \"" + e.getMessage() + "\"", e);
        }
        return doRequest(post);
    }

    public static String convertToStringRequest(String host, String[][] args) {
        //String getString = host().toString();
        String getString = host;
        boolean firstPass = true;
        if (args != null && args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].length != 0) {
                    try {
                        // We have to encode the string to UTF-8 since we are doing an http GET.
                        // The HTML spec says non-ASCII characters must be escaped some way, but
                        // is not specific, so we have to do this.
                        // Other than this case,
                        // we should not be decoding anything since UTF-8 is the encoding set in the response.
                        getString = getString + (firstPass ? "?" : "&") + args[i][0] + "=" + encode(args[i][1]);
                        if (firstPass) firstPass = false;
                    } catch (UnsupportedEncodingException e) {
                        throw new GeneralException("Error encoding argument", e);
                    }
                }
            }
        }
        return getString;
    }

    /**
     * Do an HTTP POST to the endpoint sending along basic authorization and any parameters.
     * This returns a string, so do process the result.
     *
     * @param parameters
     * @param id
     * @param secret
     * @return
     */
    public String doPost(Map<String, Object> parameters, String id, String secret) {
        HttpPost post = new HttpPost(host().toString());
        addHeadersFromParameters(post, parameters); // Fix https://github.com/ncsa/security-lib/issues/43
        List<NameValuePair> params = getNameValuePairs(parameters);
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException("error encoding form \"" + e.getMessage() + "\"", e);
        }

        return doRequest(post, id, secret);
    }

    /**
     * Removes the headers from the parameter request for the map. This is destructive in the
     * sense that the headers are no longer in the parameter map, so it is a one-time use call.
     * @param parameters
     * @return
     */
    protected Map getHeaders(Map parameters) {
        if (parameters.containsKey(HEADER_KEY)) {
            Map<String, String> headers = (Map<String, String>) parameters.get(HEADER_KEY);
            parameters.remove(HEADER_KEY);
            return headers;
        }
        return new HashMap();
    }

    /**
     * Peels off the headers (if any) from the parameter map and adds them to the request. This returns the header map.
     * The parameter map will not contain the headers once this call completes.
     * @param httpRequestBase
     * @param parameters
     * @return
     */
    protected Map addHeadersFromParameters(HttpRequestBase httpRequestBase, Map parameters) {
        return addHeaders(httpRequestBase, getHeaders(parameters));
    }

    /**
     * Adds the headers from an existing header map.
     * @param httpRequestBase
     * @param headers
     * @return
     */
    protected Map addHeaders(HttpRequestBase httpRequestBase, Map<String,String> headers) {
        if(headers != null) {return headers;}
        for (String key : headers.keySet()) {
            httpRequestBase.addHeader(key, headers.get(key));
        }
        return headers;
    }

    /**
     * Do post using a bearer token
     *
     *
     * @param bearerToken
     * @return
     */
    public String doPost(Map<String, Object> parameters, String bearerToken) {
        HttpPost post = new HttpPost(host().toString());
        addHeadersFromParameters(post, parameters); //Fix https://github.com/ncsa/security-lib/issues/43
        List<NameValuePair> params = getNameValuePairs(parameters);

        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException("error encoding form \"" + e.getMessage() + "\"", e);
        }

        return doBearerRequest(post, bearerToken);
    }

    private List<NameValuePair> getNameValuePairs(Map<String, Object> parameters) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String key : parameters.keySet()) {
            if (parameters.get(key) != null) {

                if (parameters.get(key) instanceof Collection) {
                    for (Object obj : (Collection) parameters.get(key)) {
                        params.add(new BasicNameValuePair(key, obj.toString()));
                    }
                } else {
                    params.add(new BasicNameValuePair(key, parameters.get(key).toString()));
                }
            }
        }
        return params;
    }


    /**
     * This will set the basic authorization in the headers for the request.
     *
     * @param httpRequestBase
     * @param id
     * @param secret
     * @return
     */
    protected String doRequest(HttpRequestBase httpRequestBase, String id, String secret) {
        String creds;
        try {
            creds = URLEncoder.encode(id, "UTF-8") + ":" + URLEncoder.encode(secret, "UTF-8");
        } catch (UnsupportedEncodingException usx) {
            throw new NFWException("unsupported encoded for UTF-8");
        }
        creds = Base64.encodeBase64String(creds.getBytes());
        while (creds.endsWith("=")) {
            // shave off any trailing = from the encoding. 
            creds = creds.substring(0, creds.length() - 1);
        }
        DebugUtil.trace(this, "Doing request with basic authz " + creds);
        httpRequestBase.setHeader("Authorization", "Basic " + creds);
        return doRequest(httpRequestBase);
    }

    /**
     * If the token is not already base 64 or 32 encoded, option flag to do so.
     *
     * @param httpRequestBase
     * @param token           -- the bearer token
     * @param base64Encode
     * @return
     */
    protected String doBearerRequest(HttpRequestBase httpRequestBase, String token, boolean base64Encode) {
        if (base64Encode) {
            String creds = Base64.encodeBase64String(token.getBytes());
            while (creds.endsWith("=")) {
                // shave off any trailing = from the encoding.
                creds = creds.substring(0, creds.length() - 1);
            }
            token = creds; // replace token, rock on
        }
        DebugUtil.trace(this, "Doing request with bearer token " + token);
        httpRequestBase.setHeader("Authorization", "Bearer " + token);
        return doRequest(httpRequestBase);
    }

    /**
     * Process the request, but use a bearer token (the access token which should eb suitably encoded)
     *
     * @param httpRequestBase
     * @param token
     * @return
     */
    protected String doBearerRequest(HttpRequestBase httpRequestBase, String token) {
        return doBearerRequest(httpRequestBase, token, false);
    }

    public static boolean ECHO_REQUEST = false;
    public static boolean ECHO_RESPONSE = false;

    /**
     * Do the request. The response will be the response of the server if there was a success.
     * Otherwise, the response will be (a) constructed if not JSON or (b) the JSON if the server response
     * is a JSON payload. It is becoming de facto standard to return JSON as part of the error, so we should
     * just send that along.
     *
     * @param httpRequestBase
     * @return
     */
    protected String doRequest(HttpRequestBase httpRequestBase) {
        HttpClient client = clientPool.pop();
        HttpResponse response = null;
        try {
            if (ECHO_REQUEST) {
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
            response = client.execute(httpRequestBase);
            clientPool.push(client);  // put it back as soon as done.
        } catch (Throwable t) {
            if (ECHO_RESPONSE) {
                System.out.println("\n----- Echo response -----");
                System.out.println("ERROR!");
                System.out.println(t.getMessage());
                System.out.println("----- End echo response -----\n");
            }
            // Fix https://github.com/ncsa/security-lib/issues/37
            clientPool.doDestroy(client); // if it failed, get rid of connection
            ServletDebugUtil.trace(this, "Error  invoking execute for client", t);
            if (ServletDebugUtil.isEnabled()) {
                t.printStackTrace();
            }
            throw new ConnectionException("Error invoking client:" + t.getMessage(), t);
        }
        if (ECHO_RESPONSE) {
            System.out.println("\n----- Echo response -----");
        }
        try {
            if (response.getEntity() != null && response.getEntity().getContentType() != null) {
                if (ECHO_RESPONSE) {
                    System.out.println("Content Type: " + response.getEntity().getContentType().getValue());
                }
                ServletDebugUtil.trace(this, "Raw response, content type:" + response.getEntity().getContentType());
            } else {
                if (ECHO_RESPONSE) {
                    System.out.println("Content Type: (none)");
                }
                ServletDebugUtil.trace(this, "No response entity or no content type.");
            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                return "";
            }

            HttpEntity entity1 = response.getEntity();
            String x = EntityUtils.toString(entity1, StandardCharsets.UTF_8);
            if (ECHO_RESPONSE) {
                System.out.println("Status: " + response.getStatusLine().getStatusCode());
                System.out.println("Raw Response: \n" + x);
                System.out.println("----- End echo response -----\n");
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // If there was a proper error thrown on the server then we should be able to parse the contents of the
                // response.
                String err = URLDecoder.decode(x, "UTF-8");
                ServiceClientHTTPException xx;
                try {
                    JSONObject jjj = JSONObject.fromObject(err);
                    xx = new ServiceClientHTTPException(jjj.toString()); // if it's a JSON object return that
                } catch (Throwable t) {
                    // Not a JSON object. Construct an error.
                    xx = new ServiceClientHTTPException("Error contacting server with code of  " +
                            response.getStatusLine().getStatusCode() + ":\n" + err);
                }
                xx.setContent(x);
                xx.setStatus(response.getStatusLine().getStatusCode());
                //   clientPool.destroy(client);
                throw xx;
            }
            //  clientPool.push(client);
            return x;
        } catch (IOException e) {
            throw new GeneralException("Error invoking http client", e);
        }
    }

    /** Do a GET with a specifically constructed request string
     *
     * @param requestString
     * @param headers
     * @param id
     * @param secret
     * @return
     */
    public String doGet(String requestString, Map<String,String> headers, String id, String secret) {
        HttpGet httpGet = new HttpGet(requestString);
        if(headers != null) {
            addHeaders(httpGet, headers);
        }
        return doRequest(httpGet, id, secret);
    }

    public String doGet(String requestString, Map<String,String> headers) {
        HttpGet httpGet = new HttpGet(requestString);
        if(headers != null) {
            addHeaders(httpGet, headers);
        }
        return doRequest(httpGet);
    }

    public static final String HEADER_KEY = "__headers";
}
