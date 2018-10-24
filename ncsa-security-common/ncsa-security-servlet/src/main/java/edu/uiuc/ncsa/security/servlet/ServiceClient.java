package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import edu.uiuc.ncsa.security.util.ssl.VerifyingHTTPClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

    public ServiceClient(URI host, SSLConfiguration sslConfiguration) {
        this.host = host;
        this.sslConfiguration = sslConfiguration;
    }

    URI host;

    /**
     * Basic default service client that uses the java keystore only.
     *
     * @param host
     */
    public ServiceClient(URI host) {
        this.host = host;
        SSLConfiguration sslConfiguration1 = new SSLConfiguration();
        sslConfiguration1.setUseDefaultJavaTrustStore(true);
        this.sslConfiguration = sslConfiguration1;
    }

    public URI host(URI... x) {
        if (0 < x.length) host = x[0];
        return host;
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
                return getF().getClient(host.toString());
            } catch (IOException e) {
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
        if(x == null) return "";
        String xx = URLEncoder.encode(x, ENCODING);
        return xx;
    }

    public static String decode(String x) throws UnsupportedEncodingException {
        return URLDecoder.decode(x, ENCODING);
    }

    public static String convertToStringRequest(String host, Map m) {
        int size = m.size();
        int i = 0;
        String[][] strings = new String[size][2];
        for (Object o : m.keySet()) {
            Object v = m.get(o);
            if (v != null) {
                strings[i][0] = o.toString();
                strings[i++][1] = v.toString();
            }
        }
        return convertToStringRequest(host, strings);
    }

    public String getRawResponse(Map m) {
        return getRawResponse(convertToStringRequest(host().toString(), m));
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

    public String getRawResponse(String requestString) {
        HttpGet httpGet = new HttpGet(requestString);
        HttpClient client = clientPool.pop();
        HttpResponse response = null;
        try{

            response = client.execute(httpGet);
        }catch(Throwable t){
            ServletDebugUtil.dbg(this, "Error  invoking execute for client", t);
            if(ServletDebugUtil.isEnabled()){
                t.printStackTrace();
            }
            throw new GeneralException("Error invoking client", t);
        }
        try {

            if(response.getEntity() != null && response.getEntity().getContentType()!=null) {
                ServletDebugUtil.dbg(this, "Raw response, content type:" + response.getEntity().getContentType());
            }else{
                ServletDebugUtil.dbg(this, "No response entity or no content type.");

            }
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT){
                clientPool.push(client);
                return "";
            }

            HttpEntity entity1 = response.getEntity();
            String x = EntityUtils.toString(entity1);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // If there was a proper error thrown on the server then we should be able to parse the contents of the
                // response.

                ServiceClientHTTPException xx = new ServiceClientHTTPException("Error contacting server with code of  " + response.getStatusLine().getStatusCode());
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


}
