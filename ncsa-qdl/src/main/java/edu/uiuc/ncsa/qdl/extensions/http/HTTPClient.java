package edu.uiuc.ncsa.qdl.extensions.http;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.extensions.QDLFunction;
import edu.uiuc.ncsa.qdl.extensions.QDLModuleMetaClass;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Class that is the workhorse for {@link QDLHTTPModule}. See the blurb <br/>
 * /home/ncsa/dev/ncsa-git/security-lib/ncsa-qdl/src/main/docs/http-extension.odt
 * <p>Created by Jeff Gaynor<br>
 * on 10/5/21 at  7:02 AM
 */

/*
q := module_load('edu.uiuc.ncsa.qdl.extensions.http.QDLHTTPLoader','java') ;
     module_import(q) ;
       http#host('https://localhost:9443/oauth2/.well-known/openid-configuration/')
         http#host('https://localhost:9443/oauth2/.well-known/openid-confzzz')

       http#host('https://didact-patto.dev.umccr.org/api/visa') ;
       http#open();
        z. := http#get({'sub':'https://nagim.dev/p/wjaha-ppqrg-10000'});

  Ex. Doing a DB service (to CILogon) all to my local box with a self-signed cert.
      This approves the user with the user_code who has a pending flow

      q := module_load('edu.uiuc.ncsa.qdl.extensions.http.QDLHTTPLoader','java') ;
      module_import(q) ;
      http#host('https://localhost:9443/oauth2/dbService');
      http#open(true);
      http#get({'action':'userCodeApproved','approved':'1','user_code':'JJX-J6N-RJ6'})

{
 headers: {
  Transfer-Encoding:chunked,
  Server:Apache-Coyote/1.1,
  Date:Wed, 02 Mar 2022 12:16:18 GMT,
  Content-Type:oa4mp:form_encoding;charset=UTF-8
 },
 content: [status=0,client_id=localhost:test/df,grant=NB2HI4B2F4XWY33DMFWGQ33TOQ5DSNBUGMXW6YLVORUDELZXMI3GMYRSHFSGKNTEGBRWKOBWMQ4TSZJSMM3TAN3FHE3TEZBQGI7XI6LQMU6WC5LUNB5EO4TBNZ2CM5DTHUYTMNBWGIZDGMBRGI2TSNZGOZSXE43JN5XD25RSFYYCM3DJMZSXI2LNMU6TSMBQGAYDA,user_code=JJX-J6N-RJ6],
 status: {
  code:200,
  message:OK
 }
}

  Which returns a status of 0 (so all ok), the client_id and the current base 32 encoded grant.
  */
public class HTTPClient implements QDLModuleMetaClass {
    CloseableHttpClient httpClient = null;
    String host = null;
    public String HOST_METHOD = "host";
    public String GET_METHOD = "get";
    JSONObject headers;
    public String HEADERS_METHOD = "headers";
    public String PUT_METHOD = "put";
    public String POST_METHOD = "post";
    public String DELETE_METHOD = "delete";
    public String CLOSE_METHOD = "close";
    public String OPEN_METHOD = "open";
    public String IS_OPEN_METHOD = "is_open";
    public static final String CONTENT_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_JSON = "application/json";
    public static final String CONTENT_HTML = "text/html";

    protected void checkInit() {
        if (StringUtils.isTrivial(host)) {
            throw new IllegalStateException("you must set the host before doing a get");
        }
        if (httpClient == null) {
            throw new IllegalStateException("The connection has been closed. Please open a new one if you need to.");
        }
    }

    /**
     * Takes the array of objects for an evaluate method and creates the right url
     * This is used in get and delete. Options are
     * 0 args - return current host
     * 1 arg - stem, parameters
     * 2 args - uri path + stem of parameters
     *
     * @param objects
     * @return a valid get/delete string of host+uri_path+?key0=value0&key1=value1...
     */
    protected String paramsToRequest(Object[] objects) {
        String actualHost = host;
        StemVariable parameters = null;
        if (objects.length == 2) {
            actualHost = actualHost + (actualHost.endsWith("/") ? "" : "/") + objects[0];
            parameters = (StemVariable) objects[1];
        }
        if (objects.length == 0) {
            parameters = new StemVariable(); // empty
        }
        if (objects.length == 1) {
            parameters = (StemVariable) objects[0];
        }
        // make the parameters.
        String p = parameters.size() == 0 ? "" : "?";
        boolean isFirst = true;
        for (String key : parameters.keySet()) {
            if (isFirst) {
                p = p + key + "=" + parameters.get(key);
                isFirst = false;
            } else {
                p = p + "&" + key + "=" + parameters.get(key);

            }
        }
        return actualHost + p;
    }

    public class Host implements QDLFunction {
        @Override
        public String getName() {
            return HOST_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0, 1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            String oldHost = host;
            if (objects.length == 1) {
                if (objects[0] instanceof String) {
                    host = (String) objects[0];
                } else {
                    throw new IllegalArgumentException("the argument to " + getName() + " must be a string");
                }
            }
            return oldHost == null ? "" : oldHost;
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            return null;
        }
    }

    public class Get implements QDLFunction {
        @Override
        public String getName() {
            return GET_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0, 1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            checkInit();
            String r = paramsToRequest(objects);
            DebugUtil.trace(this, "getting from address " + r);
            HttpGet request = new HttpGet(r);
            if ((headers != null) && !headers.isEmpty()) {
                for (Object key : headers.keySet()) {
                    request.addHeader(key.toString(), headers.getString(key.toString()));
                }
            }
            try {
                CloseableHttpResponse response = httpClient.execute(request);
                return getResponseStem(response);
            } catch (ClientProtocolException e) {
                throw new QDLException("could not do " + getName() + " because of protocol error:'" + e.getMessage() + "'");
            } catch (IOException e) {
                throw new QDLException("could not do " + getName() + " because of I/O error:'" + e.getMessage() + "'");
            }
        }


        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "({uri_path,}{parameters.}) -  do an HTTP GET");
            switch (argCount) {
                case 0:
                    doxx.add(getName() + "() - do an HTTP GET to the host with the current headers and no parameters.");
                    break;
                case 1:
                    doxx.add(getName() + "(parameters.) - do an HTTP GET to the host with the current headers and use the parameters.");
                    break;
                case 2:
                    doxx.add(getName() + "(uri_path, parameters.) - do an HTTP GET to host + uri_path with the current headers and use the parameters.");
                    break;
            }
            doxx.add("The two basic ways of accessing RESTful services are to have the uri overloaded or to send parameters (or both).");
            doxx.add("This function will do either of those. ");
            doxx.add("E.g.");
            doxx.add("Let us say you needed to make a call to https://students.bsu.edu/user/123?format=json");
            doxx.add("In this case, you must supply the type of object ('user') and an identifier ('123') as part of the path");
            doxx.add("http#host('https://students.bsu.edu'); // sets up host with protocol");
            doxx.add("out. := http#get('user/123', {'format':'json'});");
            doxx.add("\nAlternately, if you only needed to make this call repeatedly (so never vary the user) you could set ");
            doxx.add("http#host:='https://students.bsu.edu/user/123'; // sets up host with protocol and user id");
            doxx.add("out. := http#get({'format':'json'});");
            doxx.add("\nThe response is always a 3 element stem with major keys");
            doxx.add("  status - the status of the response. out.status.code is the actual integer HTTP code");
            doxx.add("           This is always present.");
            doxx.add("  content - the actual content. This is a stem and may be either a list of lines.");
            doxx.add("            or be a stem if the response was JSON.");
            doxx.add("  headers - the headers in the response as a stem.");

            return doxx;
        }
    }

    /**
     * Utillity to turn the response, whatever it is, into a stem.
     *
     * @param response
     * @return
     */
    public StemVariable getResponseStem(HttpResponse response) throws IOException {
        StemVariable s = new StemVariable();
        StemVariable responseStem = new StemVariable();
        responseStem.put("code", (long) response.getStatusLine().getStatusCode());
        responseStem.put("message", response.getStatusLine().getReasonPhrase());
        s.put("status", responseStem);
        HttpEntity entity = response.getEntity();
        StemVariable stemResponse = null;
        String rawResult = EntityUtils.toString(entity);
        if ((entity.getContentType() != null) && entity.getContentType().getValue().contains("application/json")) {
            stemResponse = jsonToStemJSON(rawResult);
        } else {
            // alternately, try to chunk it up
            stemResponse = new StemVariable();
            if (!StringUtils.isTrivial(rawResult)) {
                stemResponse.addList(StringUtils.stringToList(rawResult));
            }
        }

        s.put("content", stemResponse);
        Header[] headers = response.getAllHeaders();
        StemVariable h = new StemVariable();
        for (int i = 0; i < headers.length; i++) {
            Header header = headers[i];
            h.put(header.getName(), header.getValue());
        }
        if (!h.isEmpty()) {
            s.put("headers", h);
        }
        return s;
    }

    public class Headers implements QDLFunction {
        @Override
        public String getName() {
            return HEADERS_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0, 1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            JSONObject oldHeaders = headers;
            if (objects.length == 1) {
                if (objects[0] instanceof StemVariable) {
                    headers = (JSONObject) ((StemVariable) objects[0]).toJSON();
                } else {
                    throw new IllegalArgumentException(getName() + " requires a stem as its argument if present");
                }
            }
            StemVariable stemVariable = new StemVariable();
            if (oldHeaders != null) {
                stemVariable.fromJSON(oldHeaders);
            }
            return stemVariable;
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "({headers.}) - get or set the headers");
            switch (argCount) {
                case 0:
                    doxx.add("get current set of headers.");
                    break;
                case 1:
                    doxx.add("The argument is the new headers. Previous headers are returned.");
            }
            doxx.add("The keys are the names of the headers, the value is its value");
            doxx.add("E.g.s of various random headers you can set");
            doxx.add("header.'Content-Type':= 'application/json;charset=UTF-8';");
            doxx.add("header.'Content-Type':= 'text/html;charset=UTF-8';");
            doxx.add("header.'Content-Type':= 'application/x-www-form-urlencoded';//mostly used in POST");
            doxx.add("header.'Authorization':= 'Bearer ' + bearer_token; // note the space!");
            doxx.add("header.'Authorization':= 'Basic ' + " + CREATE_CREDENTIALS_METHOD + "('bob','my_password'); // note the space"); // note the space");
            return doxx;
        }
    }

    protected StemVariable jsonToStemJSON(String rawJSON) {
        StemVariable stemVariable = new StemVariable();
        try {
            stemVariable.fromJSON(JSONObject.fromObject(rawJSON));
            return stemVariable;
        } catch (Throwable t) {

        }
        try {
            stemVariable.fromJSON(JSONArray.fromObject(rawJSON));
            return stemVariable;
        } catch (Throwable t) {

        }
        throw new QDLException("could not convert '" + rawJSON + "' to stem");
    }

    public class Close implements QDLFunction {
        @Override
        public String getName() {
            return CLOSE_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                throw new QDLException("could not close connection: '" + e.getMessage() + "'");
            }
            httpClient = null;
            return true;

        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "() - close the connection. You will not be able to do any operations until you call " + OPEN_METHOD);
            return doxx;
        }
    }

    public class Open implements QDLFunction {
        @Override
        public String getName() {
            return OPEN_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0, 1};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            boolean doInsecure = false;
            if (objects.length == 1) {
                if (!(objects[0] instanceof Boolean)) {
                    throw new IllegalArgumentException(getName() + " retuires a boolean argument if present");
                }
                doInsecure = (Boolean) objects[0];
            }
            if (httpClient != null) {
                throw new IllegalStateException("You must close the current connection before opening a new one.");
            }
            if (doInsecure) {
                try {
                    httpClient = createUnverified();
                } catch (Exception ex) {
                    throw new QDLException("unable to create insecure http client: '" + ex.getMessage() + "'", ex);
                }
            } else {
                httpClient = HttpClients.createDefault();
            }
            return true;
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            switch (argCount) {
                case 0:
                    doxx.add(getName() + "() - opens a connection with standard ssl certs.");
                    doxx.add(" If the protocol is");
                    doxx.add("ssl, cert and hostname verification are done automatically.");
                    doxx.add("You should use this unless you have an explicit reason not to.");
                    break;
                case 1:
                    doxx.add(getName() + "(unverified) - if unverified is true will allow for connecting without SSL verfication");
                    doxx.add("Unless you have a very specific");
                    doxx.add("reason for this (such as you are testing a server with a self-signed cert");
                    doxx.add("you should not use this feature.");
                    doxx.add("This neither boost speed nor performance.");
                    break;
            }
            return doxx;
        }

        /**
         * Internal method to create an SSL context that does no hostname verification or cert chain
         * checking. Mostly this is so people can debug servers they are writing before they get a real cert.
         * for it.
         *
         * @return
         * @throws KeyStoreException
         * @throws NoSuchAlgorithmException
         * @throws KeyManagementException
         */
        protected CloseableHttpClient createUnverified() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", sslsf)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();

            BasicHttpClientConnectionManager connectionManager =
                    new BasicHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager).build();
            return httpClient;
        }
    }

    public class IsOpen implements QDLFunction {
        @Override
        public String getName() {
            return IS_OPEN_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            return httpClient != null;
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "() - returns true if the connection is open, false otherwise.");

            return doxx;
        }
    }

    public class Post implements QDLFunction {
        @Override
        public String getName() {
            return POST_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            return doPostOrPut(objects, state, true);
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "({uri_path,} payload.) do a post with the payload. ");
            return doxx;
        }
    }

    public class Put implements QDLFunction {
        @Override
        public String getName() {
            return PUT_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            return doPostOrPut(objects, state, false);
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "({uri_path,} payload.) do a put with the payload. ");
            doxx.add("Note that the payload will be the body of the post.");
            doxx.add("If you need to add authorization headers, set them in the header() function first.");
            return doxx;
        }
    }

    public Object doPostOrPut(Object[] objects, State state, boolean isPost) {
        // if the type is form encoded, escape each element in the payload.
        // If JSON, send the payload as a JSON blob.
        String uriPath = "";
        StemVariable payload = null;
        checkInit();
        switch (objects.length) {
            case 1:
                if (objects[0] instanceof StemVariable) {
                    payload = (StemVariable) objects[0];
                } else {
                    throw new IllegalArgumentException("monadic " + (isPost ? POST_METHOD : PUT_METHOD) + " must have a stem as its argument");
                }

                break;
            case 2:
                if (objects[0] instanceof String) {
                    uriPath = (String) objects[0];
                } else {
                    throw new IllegalArgumentException("dyadic " + (isPost ? POST_METHOD : PUT_METHOD) + " must have a string as it first argument");
                }
                if (objects[1] instanceof StemVariable) {
                    payload = (StemVariable) objects[1];
                } else {
                    throw new IllegalArgumentException("dyadic " + (isPost ? POST_METHOD : PUT_METHOD) + " must have a stem as its second argument");
                }
                break;
            default:
                throw new IllegalArgumentException((isPost ? POST_METHOD : PUT_METHOD) + " requires one or two arguments");
        }
        String contentType = "Content-Type";
        String body = "";
        String actualHost = host;
        if (0 < uriPath.length()) {
            actualHost = (actualHost.endsWith("/") ? "" : "/");
        }
        if (headers.containsKey(contentType)) {
            switch (headers.getString(contentType)) {
                case CONTENT_JSON:
                    body = payload.toJSON().toString();
                    break;
                case CONTENT_FORM:
                    boolean isFirst = true;
                    for (String key : payload.keySet()) {
                        body = body + (isFirst ? "" : "&") + key + "=" + payload.getString(key);
                        if (isFirst) isFirst = false;
                    }
                    break;
            }
        }
        HttpEntityEnclosingRequest request;
        if (isPost) {
            request = new HttpPost(actualHost);
        } else {
            request = new HttpPut(actualHost);

        }
        HttpEntity httpEntity = null;
        try {
            httpEntity = new ByteArrayEntity(body.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new NFWException("UTF-8is, apparently buested in Java:" + unsupportedEncodingException.getMessage());
        }
        request.setEntity(httpEntity);

        if ((headers != null) && !headers.isEmpty()) {
            for (Object key : headers.keySet()) {
                request.addHeader(key.toString(), headers.getString(key.toString()));
            }
        }
        try {
            CloseableHttpResponse response = httpClient.execute((HttpUriRequest) request);
            return getResponseStem(response);
        } catch (ClientProtocolException e) {
            throw new QDLException("could not do " + (isPost ? POST_METHOD : PUT_METHOD) + " because of protocol error:'" + e.getMessage() + "'");
        } catch (IOException e) {
            throw new QDLException("could not do " + (isPost ? POST_METHOD : PUT_METHOD) + " because of I/O error:'" + e.getMessage() + "'");
        }
    }

    public class Delete implements QDLFunction {
        @Override
        public String getName() {
            return DELETE_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{0, 1, 2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            checkInit();
            String r = paramsToRequest(objects);
            DebugUtil.trace(this, "delete from address " + r);
            HttpDelete request = new HttpDelete(r);
            if ((headers != null) && !headers.isEmpty()) {
                for (Object key : headers.keySet()) {
                    request.addHeader(key.toString(), headers.getString(key.toString()));
                }
            }
            try {
                CloseableHttpResponse response = httpClient.execute(request);
                return getResponseStem(response);
            } catch (ClientProtocolException e) {
                throw new QDLException("could not do " + getName() + " because of protocol error:'" + e.getMessage() + "'");
            } catch (IOException e) {
                throw new QDLException("could not do " + getName() + " because of I/O error:'" + e.getMessage() + "'");
            }
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "({uri_path}, parameters) - delete something from the server");
            doxx.add("This uses the current host and headers.");
            switch (argCount) {
                case 0:
                    doxx.add(getName() + "() - use only the current host ");
                    break;
                case 1:
                    doxx.add(getName() + "(parameters.) - use only the current host and append the parameters to the request uri ");
                    break;
                case 2:
                    doxx.add(getName() + "(uri_path, parameters.)  - use  current host + uri_path, then append the parameters to the request uri ");
                    break;
            }
            doxx.add("This returns a response stem if the operation worked and throws an error if it did not.");
            return doxx;
        }
    }

    public static String CREATE_CREDENTIALS_METHOD = "credentials";
    public class CreateCredentials implements QDLFunction{
        @Override
        public String getName() {
            return CREATE_CREDENTIALS_METHOD;
        }

        @Override
        public int[] getArgCount() {
            return new int[]{2};
        }

        @Override
        public Object evaluate(Object[] objects, State state) {
            try {
                String username = URLEncoder.encode(objects[0].toString(), "UTF-8");
                String password = URLEncoder.encode(objects[1].toString(), "UTF-8");
                String raw = username + ":" + password;
                return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
            }catch(UnsupportedEncodingException unsupportedEncodingException){
                throw new NFWException("Encoding failed:'" + unsupportedEncodingException.getMessage() + "'");
            }
        }

        @Override
        public List<String> getDocumentation(int argCount) {
            List<String> doxx = new ArrayList<>();
            doxx.add(getName() + "(username, password) - create the correct credential for the basic auth header");
            doxx.add("The standard is to return encode_b64(url_escape(username) + ':' + url_escape(password))");
            return doxx;
        }
    }
}
