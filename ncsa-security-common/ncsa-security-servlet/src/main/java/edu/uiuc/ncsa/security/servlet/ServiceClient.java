package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.Pool;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import edu.uiuc.ncsa.security.util.ssl.VerifyingHTTPClientFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
               // return getF().getClient(host.toString());
                return getF().getClient(address.getHost()); // otherwise the client has the *entire* address.
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

    public String getRawResponse(Map m, String id, String secret) {
        return getRawResponse(convertToStringRequest(host().toString(), m), id, secret);
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

    /**
     * Do a POST to the service using the parameters. This is automatically encoded as JSON and the assumption is that
     * the response is JSON as well.
     */
/*    public JSONObject doPost(String address, JSONObject json){
       HttpPost post = new HttpPost(address);
       // now to add the parameters to the body of the post.
        //post.getEntity().()
        StringEntity stringEntity = new StringEntity("", "application/json");
        UrlEncodedFormEntity xxx = new UrlEncodedFormEntity(null);
        xxx.
    }*/

    /**
     * This will set the basic authorization in the headers for the request.
     * @param httpRequestBase
     * @param id
     * @param secret
     * @return
     */
    protected String doRequest(HttpRequestBase httpRequestBase, String id, String secret){
        String creds = id + ":" + secret;
        creds = Base64.encodeBase64String(creds.getBytes());
        while(creds.endsWith("=")){
            // shave off any trailing = from the encoding. 
            creds = creds.substring(creds.length() - 1);
        }
        DebugUtil.trace(this, "Doing request with basic authz " + creds);
        httpRequestBase.setHeader("Authorization", "Basic " + creds);
        return doRequest(httpRequestBase);
    }

    protected String doRequest(HttpRequestBase httpRequestBase){
        HttpClient client = clientPool.pop();
           HttpResponse response = null;
           try{
               response = client.execute(httpRequestBase);
           }catch(Throwable t){
               ServletDebugUtil.trace(this, "Error  invoking execute for client", t);
               if(ServletDebugUtil.isEnabled()){
                   t.printStackTrace();
               }
               throw new GeneralException("Error invoking client", t);
           }
           try {

               if(response.getEntity() != null && response.getEntity().getContentType()!=null) {
                   ServletDebugUtil.trace(this, "Raw response, content type:" + response.getEntity().getContentType());
               }else{
                   ServletDebugUtil.trace(this, "No response entity or no content type.");

               }
               if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT){
                   clientPool.push(client);
                   return "";
               }

               HttpEntity entity1 = response.getEntity();
               String x = EntityUtils.toString(entity1, StandardCharsets.UTF_8);
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
    public String getRawResponse(String requestString, String id, String secret) {
        HttpGet httpGet = new HttpGet(requestString);
        return doRequest(httpGet, id, secret);
    }
    
    public String getRawResponse(String requestString) {
        HttpGet httpGet = new HttpGet(requestString);
        return doRequest(httpGet);
    }


}
