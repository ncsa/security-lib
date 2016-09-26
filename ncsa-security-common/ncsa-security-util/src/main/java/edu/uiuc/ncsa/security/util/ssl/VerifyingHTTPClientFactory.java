package edu.uiuc.ncsa.security.util.ssl;

import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates HTTP clients which validate connections against a keystore.
 * <p>Created by Jeff Gaynor<br>
 * on Aug 7, 2010 at  2:59:21 PM
 */
public class VerifyingHTTPClientFactory implements Logable {
    public VerifyingHTTPClientFactory(MyLoggingFacade logger, SSLConfiguration sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
        this.logger = logger;
    }

    boolean strictHostnames = true;

    /**
     * Whether to allow strict hostname verification. The default is true. Generally you do not want to set this to
     * false without and excellent reason since it will relax security. It is, however, warranted in certain cases
     * (such as in testing environments with self-signed certs whose host names aren't quite right.) Generally if
     * you have to set this to false in a production environment, there is something wrong with the server's certificates.
     * @return
     */
    public boolean isStrictHostnames() {
        return strictHostnames;
    }

    public void setStrictHostnames(boolean strictHostnames) {
        this.strictHostnames = strictHostnames;
    }

    /**
     * The default amount of time a connection should wait before timing out.
     * (has to be an int since we are using HTTPClient)
     */
    public static int DEFAULT_CONNECTION_TIMEOUT = 10000;

    public void debug(String x) {
        getLogger().debug(x);
    }

    public boolean isDebugOn() {
        return getLogger().isDebugOn();
    }

    public void setDebugOn(boolean setOn) {
        getLogger().setDebugOn(setOn);
    }

    public void info(String x) {
        getLogger().info(x);
    }

    public void warn(String x) {
        getLogger().warn(x);
    }

    public void error(String x) {
        getLogger().error(x);

    }

    public MyLoggingFacade getLogger() {
        if (logger == null) {
            logger = new MyLoggingFacade(getClass().getName(), false);
        }
        return logger;
    }


    MyLoggingFacade logger;

    /**
     * A facade for trust managers. The SSLContext will accept arrays of trust managers, <b>but</b> only
     * the first is actually ever used. This facade will check each registered trust manager and fail
     * only if all attempts fail.
     */
    static public class X509TrustManagerFacade implements X509TrustManager {
        static boolean stackTracesOn = false;

        public List<X509TrustManager> getTrustManagers() {
            if (trustManagers == null) {
                trustManagers = new ArrayList<X509TrustManager>();
            }
            return trustManagers;
        }

        public void setTrustManagers(List<X509TrustManager> trustManagers) {
            this.trustManagers = trustManagers;
        }

        List<X509TrustManager> trustManagers;

        /**
         * Convenience method. Adds a trust manager.
         *
         * @param x509TrustManager
         */
        public void add(X509TrustManager x509TrustManager) {
            getTrustManagers().add(x509TrustManager);
        }

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            for (X509TrustManager tm : getTrustManagers()) {
                try {
                    tm.checkClientTrusted(x509Certificates, s);
                    return;
                } catch (Throwable t) {
                    if (stackTracesOn) {
                        t.printStackTrace();
                    }
                }
            }
            throw new CertificateException("No trust manager accepted the client");
        }

        /**
         * Annoyingly, javax's SSL catches exceptions, but does not propagate them, so many trust managers
         * explicitly print out their stack traces. We need catch them anyway but then discard the
         * output or we might double the number of benign error messages
         *
         * @param x509Certificates
         * @param s
         * @throws CertificateException
         */
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            for (X509TrustManager tm : getTrustManagers()) {
                try {
                    tm.checkServerTrusted(x509Certificates, s);
                    return;
                } catch (Throwable t) {
                    if (stackTracesOn) {
                        t.printStackTrace();
                    }
                }
            }
            throw new CertificateException("No trust manager accepted the server");
        }

        /**
         * This gets all of them from all trust managers. It would actually be
         * fine if it returned an empty array too, since it is never used (all
         * calls are really delegated to each trust manager.
         *
         * @return
         */
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


    /**
     * Creates an HTTPS-aware client that will verify the cert chain and host when called.
     * This is a specific requirement for certain applications and requires that there be
     * a trust root path set as well. I.e., this is for very specific requirements such
     * as the CILogon delegation server callback to the portal. Generally you should just
     * use the built in http client with the right protocol.
     *
     * @param host
     * @return
     * @throws IOException
     */
    public HttpClient getClient(String host) throws IOException {
        return getClient(host, 0, 0);
    }


    public HttpClient getClient(String host, int connectionTimeout, int socketTimeout) throws IOException {
        MyTrustManager myTrustManager = newMyTrustManager();

        myTrustManager.setHost(host); //varies per request.
        debug("my trust manager: trust root path+" + myTrustManager.getTrustRootPath());

        //myTrustManager.setTrustRootPath("/etc/grid-security/certificates");
        myTrustManager.setTrustRootPath(getSSLConfiguration().getTrustrootPath());
        return getClient(myTrustManager, connectionTimeout, socketTimeout);
    }

    public HttpClient getClient(X509TrustManager x509TrustManager) {
        return getClient(x509TrustManager, 0, 0);
    }

    public HttpClient getClient(X509TrustManager x509TrustManager, int connectionTimeout,
                                int socketTimeout) {
        HttpClient httpclient = null;
        try {
            if (0 < connectionTimeout && 0 < socketTimeout) {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
                HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
                httpclient = new DefaultHttpClient(httpParams);
            } else {
                // just take the defaults
                httpclient = new DefaultHttpClient();

            }
            // this is how to configure SSL with a custom socket factory for HttpClient 4.0+
            httpclient.getConnectionManager().getSchemeRegistry().register(
                    new Scheme("https", getSocketFactory(x509TrustManager), 443));
            debug("done creating https client = " + httpclient);
            return httpclient;
        } catch (Throwable t) {
            error("could not create https client.");
            t.printStackTrace();
            throw new GeneralException("Error creating client", t);
        }
    }

    /**
     * Configure the socket factory using an SSL context.  This loads the keystore, grabs its
     * trust manager then adds a custom trust manager.
     *
     * @param tm
     * @return
     * @throws IOException
     * @throws KeyStoreException
     */
    protected SSLSocketFactory getSocketFactory(X509TrustManager tm) throws IOException, GeneralSecurityException {
        //if (socketFactory == null) {
        debug("creating socket factory");
        SSLContext sc = SSLContext.getInstance("TLS");
        X509TrustManagerFacade tmf = new X509TrustManagerFacade();
        tmf.add(tm);
        debug("added trust manager = " + tm);

        TrustManager[] trustAllCerts = new X509TrustManager[]{tmf};
        if (hasKeyStore()) {
            // if it has a keystore, get a trust manager and use it.
            TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(getSSLConfiguration().getKeyManagerFactory());
            tmfactory.init(getKeyStore());

            for (TrustManager tm0 : tmfactory.getTrustManagers()) {
                if (tm0 instanceof X509TrustManager) {
                    tmf.add((X509TrustManager) tm0);
                }
            }
        }
        debug("Added other trust managers, #=" + tmf.getTrustManagers().size());

        sc.init(getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
        SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
        if (isStrictHostnames()) {
            socketFactory.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
        } else {
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
        debug("enabled strict hostname verification");
        return socketFactory;
    }


    public boolean hasKeyStore() {
        return getSSLConfiguration().getKeystore() != null;
    }


    /**
     * Returns a NEW trust manager with each call since the trust manager needs to have its host set.
     *
     * @return
     * @throws IOException
     */
    public MyTrustManager newMyTrustManager() throws IOException {
        return new MyTrustManager(logger, getSSLConfiguration().getTrustrootPath());
    }


    protected KeyManagerFactory getKeyManagerFactory() throws IOException, GeneralSecurityException {
        if (keyManagerFactory == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(getSSLConfiguration().getKeyManagerFactory());
            keyManagerFactory.init(getKeyStore(), getSSLConfiguration().getKeystorePasswordChars());
        }
        return keyManagerFactory;
    }


    protected KeyStore getKeyStore() throws IOException, GeneralSecurityException {
        if (keyStore == null) {
            if (getSSLConfiguration().getKeystore() == null) {
                warn("No keystore");
                return null;
            }
            keyStore = KeyStore.getInstance(getSSLConfiguration().getKeystoreType());
            File keystoreFile = new File(getSSLConfiguration().getKeystore());
            if (!keystoreFile.exists()) {
                throw new FileNotFoundException("Error: the keystore file \"" + keystoreFile + "\" does not exist");
            }
            FileInputStream fis = new FileInputStream(keystoreFile);
            keyStore.load(fis, getSSLConfiguration().getKeystorePasswordChars());
            fis.close();
        }
        return keyStore;
    }

    protected void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }


    KeyStore keyStore;

    protected void setKeyManagerFactory(KeyManagerFactory keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
    }

    KeyManagerFactory keyManagerFactory;

    protected KeyManager[] getKeyManagers() throws IOException, GeneralSecurityException {
        if (!hasKeyStore() || getKeyManagerFactory() == null) {
            return null;
        }
        return getKeyManagerFactory().getKeyManagers();
    }

    public SSLConfiguration getSSLConfiguration() {
        return sslConfiguration;
    }

    public void setSSLConfiguration(SSLConfiguration sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
    }

    SSLConfiguration sslConfiguration;
}
