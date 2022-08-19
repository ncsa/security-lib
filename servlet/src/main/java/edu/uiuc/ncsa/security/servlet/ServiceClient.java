package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import edu.uiuc.ncsa.security.util.ssl.VerifyingHTTPClientFactory;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                return getF().getClient(address.getHost()); // otherwise the client has the *entire* address.
            } catch (IOException | NoSuchAlgorithmException | KeyStoreException e) {
                throw new GeneralException("Error getting https-aware client");
            }
        }

        @Override
        public void destroy(HttpClient HttpClient) {
            // stateless so nothing to do really.
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
        return doGet(convertToStringRequest(host().toString(), m), id, secret);
    }

    public String doGet(Map m) {
        return doGet(convertToStringRequest(host().toString(), m));
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
        List<NameValuePair> params = getNameValuePairs(parameters);

        try {
            post.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new GeneralException("error encoding form \"" + e.getMessage() + "\"", e);
        }

        return doRequest(post, id, secret);
    }



    /**
     * Do post using a bearer token
     *
     * @param bearerToken
     * @return
     */
    public String doPost(Map<String, Object> parameters, String bearerToken) {
        HttpPost post = new HttpPost(host().toString());
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
                params.add(new BasicNameValuePair(key, parameters.get(key).toString()));
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
     * @param token -- the bearer token
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

    protected String doRequest(HttpRequestBase httpRequestBase) {
        HttpClient client = clientPool.pop();
        HttpResponse response = null;
        try {
            response = client.execute(httpRequestBase);
        } catch (Throwable t) {
            ServletDebugUtil.trace(this, "Error  invoking execute for client", t);
            if (ServletDebugUtil.isEnabled()) {
                t.printStackTrace();
            }
            throw new GeneralException("Error invoking client:" + t.getMessage(), t);
        }
        try {

            if (response.getEntity() != null && response.getEntity().getContentType() != null) {
                ServletDebugUtil.trace(this, "Raw response, content type:" + response.getEntity().getContentType());
            } else {
                ServletDebugUtil.trace(this, "No response entity or no content type.");

            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                clientPool.push(client);
                return "";
            }

            HttpEntity entity1 = response.getEntity();
            String x = EntityUtils.toString(entity1, StandardCharsets.UTF_8);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // If there was a proper error thrown on the server then we should be able to parse the contents of the
                // response.

                ServiceClientHTTPException xx = new ServiceClientHTTPException("Error contacting server with code of  " +
                        response.getStatusLine().getStatusCode() + ":\n" + URLDecoder.decode(x, "UTF-8"));
                xx.setContent(x);
                xx.setStatus(response.getStatusLine().getStatusCode());
                clientPool.destroy(client);
                throw xx;
            }
            clientPool.push(client);
            return x;
        } catch (IOException e) {
            throw new GeneralException("Error invoking http client", e);
        }


    }

    public String doGet(String requestString, String id, String secret) {
        HttpGet httpGet = new HttpGet(requestString);
        return doRequest(httpGet, id, secret);
    }

    public String doGet(String requestString) {
        HttpGet httpGet = new HttpGet(requestString);
        return doRequest(httpGet);
    }


}
