package edu.uiuc.ncsa.security.util.ssl;

import java.io.Serializable;

/**
 * A bean that holds the configuration for an keystore.  If you have a custom keystore, this will point to it.
 * This is needed, e.g., by MyTrustManager
 * <p>Created by Jeff Gaynor<br>
 * on Jun 27, 2010 at  11:29:15 AM
 */
/*
  Here. since sometimes I need these and they are very hard to track down. Set these as system properties when needed (e.g.
  trying an experiment with a keystore or some such).
      javax.net.ssl.keyStore- Location of the Java keystore file containing an application process's own certificate and private key.
              On Windows, the specified pathname must use forward slashes, /, in place of backslashes.
      javax.net.ssl.keyStorePassword - Password to access the private key from the keystore file specified by javax.net.ssl.keyStore.
              This password is used twice: To unlock the keystore file (store password),
              and To decrypt the private key stored in the keystore (key password).
    javax.net.ssl.trustStore - Location of the Java keystore file containing the collection of CA certificates trusted
              by this application process (trust store). On Windows, the specified pathname
              must use forward slashes, /, in place of backslashes, \.

    If a trust store location is not specified using this property, the SunJSSE implementation searches for and uses a keystore file in the following locations (in order):

        $JAVA_HOME/lib/security/jssecacerts
        $JAVA_HOME/lib/security/cacerts

    javax.net.ssl.trustStorePassword - Password to unlock the keystore file (store password) specified by javax.net.ssl.trustStore.

    javax.net.ssl.trustStoreType - (Optional) For Java keystore file format, this property has the value jks (or JKS).
           You do not normally specify this property,
           because its default value is already jks. NOTE - "PKCS12 is also allowed, JJG).

    javax.net.debug - To switch on logging for the SSL/TLS layer, set this property to ssl.

 */
public class SSLKeystoreConfiguration implements Serializable{
    /**
     * This path is actually part of the java specification.
     */
    public final static String JAVA_DEFAULT_KEYSTORE_PATH = System.getProperty("java.home") + "/lib/security/cacerts";
    /**
     * The default as shipped with Java. If you change the keystore, you should change the password and set it in the configuration.
     */
    public final static String JAVA_DEFAULT_KEYSTORE_PASSWORD = "changeit";
    /**
     * The default type for the built in java keystore.
     * {@value}
     */
    public final static String JAVA_DEFAULT_KEYSTORE_TYPE = "jks";

    public boolean isUseDefaultJavaTrustStore() {
        return useDefaultJavaTrustStore;
    }

    public void setUseDefaultJavaTrustStore(boolean useDefaultJavaKeyStore) {
        this.useDefaultJavaTrustStore = useDefaultJavaKeyStore;
    }

    boolean useDefaultJavaTrustStore = true;

    String keystore;
    String keystoreType = "jks";
    String keystorePassword;
    String keyManagerFactory = "SunX509";



    public void setKeyManagerFactory(String keyManagerFactory) {
        this.keyManagerFactory = keyManagerFactory;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }


    public String getKeystorePassword() {
        if (keystorePassword == null && isUseDefaultJavaTrustStore()) {
            keystorePassword = JAVA_DEFAULT_KEYSTORE_PASSWORD;
        }
        return keystorePassword;
    }

    public String getKeystoreType() {
        if (keystoreType == null && isUseDefaultJavaTrustStore()) {
            keystoreType = JAVA_DEFAULT_KEYSTORE_TYPE;
        }
        return keystoreType;
    }

    char[] pwd;

    /**
     * Get the password to the keystore as a character array
     *
     * @return
     */
    public char[] getKeystorePasswordChars() {
        if (pwd == null) {
            pwd = getKeystorePassword().toCharArray();
        }
        return pwd;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getKeystore() {
        if (keystore == null && isUseDefaultJavaTrustStore()) {
            keystore = JAVA_DEFAULT_KEYSTORE_PATH;
        }
        return keystore;
    }


    public String getKeyManagerFactory() {
        return keyManagerFactory;
    }

    public String toString() {
        return getClass().getName() + "[keystore path=" + getKeystore() + ", pwd=" + getKeystorePassword() + ", type=" + getKeystoreType() + "]";
    }
}
