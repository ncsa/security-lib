package edu.uiuc.ncsa.security.util.ssl;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.Serializable;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;

/**
 * A utility to create an SSLConfiguration from a configuration node. This is included here since
 * it is used in various places in the code base.
 * <p>Created by Jeff Gaynor<br>
 * on 3/21/14 at  3:53 PM
 */
public class SSLConfigurationUtil implements Serializable {

    public static final String SSL_KEYSTORE_TAG = "keystore";
    public static final String SSL_TAG = "ssl";
    public static final String SSL_DEBUG_TAG = "debug";
    public static final String SSL_TLS_VERSION_TAG = "tlsVersion";
    public static final String SSL_KEYSTORE_PATH = "path";
    public static final String SSL_KEYSTORE_PASSWORD = "password";
    public static final String SSL_KEYSTORE_TYPE = "type";

    public static final String SSL_KEYSTORE_FACTORY = "factory";
    public static final String SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE = "useJavaTrustStore";
    public static final String SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE_OLD = "useJavaKeystore";
    public static final String SSL_TRUSTSTORE_TAG = "trustStore";
    public static final String SSL_TRUSTSTORE_PATH = "path";
    public static final String SSL_TRUSTSTORE_PASSWORD = "password";
    public static final String SSL_TRUSTSTORE_TYPE = "type";

    public final static String TLS_VERSION_1_0 = "1.0";
    public final static String TLS_VERSION_1_1 = "1.1";
    public final static String TLS_VERSION_1_2 = "1.2";
    /**
     * The strings that are used in Java are standardized and listed
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SSLContext">here</a>.
     * The values in the configuration file are made to be more easily understood.
     */
    public final static String TLS_DEFAULT = "TLS";
    public final static String TLS_1_0 = "TLSv1";
    public final static String TLS_1_1 = "TLSv1.1";
    public final static String TLS_1_2 = "TLSv1.2";

    /**
     * The old way of doing this. The node is called keystore and values are attributes.
     *
     * @param logger
     * @param node
     * @return
     * @deprecated
     */
    protected static SSLConfiguration getOLDSSLConfiguration(MyLoggingFacade logger, ConfigurationNode node) {
        if (logger != null) {
            logger.info("Loading an (old) SSL configuration");
        }
        SSLConfiguration sslKeystoreConfiguration = new SSLConfiguration();
        if (node == null) {
            sslKeystoreConfiguration.setUseDefaultJavaTrustStore(true); // default
        } else {
            sslKeystoreConfiguration.setKeystore(getFirstAttribute(node, SSL_KEYSTORE_PATH));
            sslKeystoreConfiguration.setKeystorePassword(getFirstAttribute(node, SSL_KEYSTORE_PASSWORD));
            sslKeystoreConfiguration.setKeyManagerFactory(getFirstAttribute(node, SSL_KEYSTORE_FACTORY));
            sslKeystoreConfiguration.setKeystoreType(getFirstAttribute(node, SSL_KEYSTORE_TYPE));
            String x = getFirstAttribute(node, SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE_OLD);
            if (x == null) {
                sslKeystoreConfiguration.setUseDefaultJavaTrustStore(true); //default
            } else {
                sslKeystoreConfiguration.setUseDefaultJavaTrustStore(Boolean.parseBoolean(x)); //default
            }
        }
        return sslKeystoreConfiguration;
    }

    /**
     * The new way of doing it from an SSL node in the configuration file. All values are values of the node
     * rather than as attributes, mostly because string passwords can be put into CDATA elements.
     *
     * @param logger
     * @param node
     * @return
     */
    protected static SSLConfiguration getNEWSSLConfiguration(MyLoggingFacade logger, ConfigurationNode node) {
        if (logger != null) {
            logger.info("Loading an SSL configuration");
        }
        SSLConfiguration sslConfiguration = new SSLConfiguration();

        if (node == null) {
            if (logger != null) {
                logger.info("Using default Java trust store only.");
            }
            sslConfiguration.setUseDefaultJavaTrustStore(true); // default
        } else {
            // Process TLS version
            // Fixes OAUTH-213
            String tlsVersion = getFirstAttribute(node, SSL_TLS_VERSION_TAG);
            if (tlsVersion == null || tlsVersion.length() == 0) {
                sslConfiguration.setTlsVersion(TLS_DEFAULT);
            } else {
                boolean gotOne = false;
                if (tlsVersion.equals(TLS_VERSION_1_0)) {
                    sslConfiguration.setTlsVersion(TLS_1_0);
                    gotOne = true;
                }

                if (tlsVersion.equals(TLS_VERSION_1_1)) {
                    sslConfiguration.setTlsVersion(TLS_1_1);
                    gotOne = true;

                }

                if (tlsVersion.equals(TLS_VERSION_1_2)) {
                    sslConfiguration.setTlsVersion(TLS_1_2);
                    gotOne = true;

                }

                if (!gotOne) {
                    // unrecognized string, use the default
                    logger.warn("Got a TLS version number of \"" + tlsVersion + "\", which is unrecognized. Using the default version.");
                    sslConfiguration.setTlsVersion(TLS_DEFAULT);
                }
            }
            // Process the debug attribute
            String debug = getFirstAttribute(node, SSL_DEBUG_TAG);
            if (debug != null) {
                try {
                    if (Boolean.parseBoolean(debug)) {
                        if (logger != null) {
                            logger.warn("Enabled full SSL debug mode.");
                        }
                        System.setProperty("javax.net.debug", "ssl");
                    } else {
                        if (logger != null) {
                            logger.info("No SSL debug enabled.");
                        }
                        System.setProperty("javax.net.debug", null);
                    }
                } catch (Throwable t) {
                    // do nothing.
                }
            }
            // Process the use java trust store attribute
            String x = getFirstAttribute(node, SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE);

            if (x == null) {
                String y = getFirstAttribute(node, SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE_OLD);
                if (y == null) {
                    sslConfiguration.setUseDefaultJavaTrustStore(true); //default
                } else {
                    sslConfiguration.setUseDefaultJavaTrustStore(Boolean.parseBoolean(y));
                }
            } else {
                sslConfiguration.setUseDefaultJavaTrustStore(Boolean.parseBoolean(x));
            }

            // Now get the trust store and keystore
            ConfigurationNode keyStoreNode = getFirstNode(node, SSL_KEYSTORE_TAG);
            if (keyStoreNode != null) {
                // keystore is optional. Only process if there is one.
                sslConfiguration.setKeystore(getNodeValue(keyStoreNode, SSL_KEYSTORE_PATH));
                sslConfiguration.setKeystorePassword(getNodeValue(keyStoreNode, SSL_KEYSTORE_PASSWORD));
                sslConfiguration.setKeyManagerFactory(getNodeValue(keyStoreNode, SSL_KEYSTORE_FACTORY));
                sslConfiguration.setKeystoreType(getNodeValue(keyStoreNode, SSL_KEYSTORE_TYPE));
            }
            ConfigurationNode trustStoreNode = getFirstNode(node, SSL_TRUSTSTORE_TAG);

            if (trustStoreNode != null) {
                sslConfiguration.setTrustRootPath(getNodeValue(trustStoreNode, SSL_TRUSTSTORE_PATH));
                sslConfiguration.setTrustRootPassword(getNodeValue(trustStoreNode, SSL_TRUSTSTORE_PASSWORD));
                //sslKeystoreConfiguration.setKeyManagerFactory(getNodeValue(keyStoreNode, SSL_KEYSTORE_FACTORY));
                sslConfiguration.setTrustRootType(getNodeValue(trustStoreNode, SSL_TRUSTSTORE_TYPE));

            }
            if (logger != null) {
                logger.info("Done loading SSL configuration");
            }
        }
        return sslConfiguration;
    }

    public static SSLConfiguration getSSLConfiguration(MyLoggingFacade logger, ConfigurationNode node) {
        ConfigurationNode cfgNode = getFirstNode(node, SSL_KEYSTORE_TAG);
        if (cfgNode != null) {
            return getOLDSSLConfiguration(logger, cfgNode);
        }
        cfgNode = getFirstNode(node, SSL_TAG); // if the node is null then the next call returns a generic SSL object.
        return getNEWSSLConfiguration(logger, cfgNode);
    }


}
