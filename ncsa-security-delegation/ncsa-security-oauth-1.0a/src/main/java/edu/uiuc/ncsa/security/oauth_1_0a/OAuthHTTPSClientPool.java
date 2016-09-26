package edu.uiuc.ncsa.security.oauth_1_0a;

import net.oauth.client.httpclient4.HttpClientPool;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import java.net.URL;

/**
 * Wraps an HTTPS client, not an OAuth one, but this is needed since the Java implementation
 * of OAuth 1 will leak these unless there is a pool. The interface implemented is from the
 * OAuth package.
 * <p>Created by Jeff Gaynor<br>
 * on Aug 26, 2010 at  12:39:50 PM
 */
public class OAuthHTTPSClientPool implements HttpClientPool {
    public OAuthHTTPSClientPool(HttpClient client) {
        ClientConnectionManager mgr = client.getConnectionManager();
        if (!(mgr instanceof ThreadSafeClientConnManager)) {
            HttpParams params = client.getParams();
            client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
        }
        this.client = client;
    }

    private final HttpClient client;

    public HttpClient getHttpClient(URL server) {
        return client;
    }
}
