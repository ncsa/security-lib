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
     * rather than as attrbiutes, mostly because string passwords can be put into CDATA elements.
     *
     * @param logger
     * @param node
     * @return
     */
    protected static SSLConfiguration getNEWSSLConfiguration(MyLoggingFacade logger, ConfigurationNode node) {
        if (logger != null) {
            logger.info("Loading an SSL configuration");
        }
        SSLConfiguration sslKeystoreConfiguration = new SSLConfiguration();

        if (node == null) {
            if (logger != null) {
                logger.info("Using default Java trust store only.");
            }
            sslKeystoreConfiguration.setUseDefaultJavaTrustStore(true); // default
        } else {
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
                // Process the use java trust store attribute
                String x = getFirstAttribute(node, SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE);

                if (x == null) {
                    String y = getFirstAttribute(node, SSL_TRUSTSTORE_USE_JAVA_TRUSTSTORE_OLD);
                    if (y == null) {
                        sslKeystoreConfiguration.setUseDefaultJavaTrustStore(true); //default
                    } else {
                        sslKeystoreConfiguration.setUseDefaultJavaTrustStore(Boolean.parseBoolean(y));
                    }
                } else {
                    sslKeystoreConfiguration.setUseDefaultJavaTrustStore(Boolean.parseBoolean(x));
                }

                // Now get the trust store and keystore
                ConfigurationNode keyStoreNode = getFirstNode(node, SSL_KEYSTORE_TAG);
                if(keyStoreNode != null) {
                    // keystore is optional. Only process if there is one.
                    sslKeystoreConfiguration.setKeystore(getNodeValue(keyStoreNode, SSL_KEYSTORE_PATH));
                    sslKeystoreConfiguration.setKeystorePassword(getNodeValue(keyStoreNode, SSL_KEYSTORE_PASSWORD));
                    sslKeystoreConfiguration.setKeyManagerFactory(getNodeValue(keyStoreNode, SSL_KEYSTORE_FACTORY));
                    sslKeystoreConfiguration.setKeystoreType(getNodeValue(keyStoreNode, SSL_KEYSTORE_TYPE));
                }
                ConfigurationNode trustStoreNode = getFirstNode(node, SSL_TRUSTSTORE_TAG);

                if(trustStoreNode != null){
                    sslKeystoreConfiguration.setTrustRootPath(getNodeValue(trustStoreNode, SSL_TRUSTSTORE_PATH));
                    sslKeystoreConfiguration.setTrustRootPassword(getNodeValue(trustStoreNode, SSL_TRUSTSTORE_PASSWORD));
                    //sslKeystoreConfiguration.setKeyManagerFactory(getNodeValue(keyStoreNode, SSL_KEYSTORE_FACTORY));
                    sslKeystoreConfiguration.setTrustRootType(getNodeValue(trustStoreNode, SSL_TRUSTSTORE_TYPE));

                }
            }


       /*     sslKeystoreConfiguration.setTrustRootPath(getNodeValue(node, SSL_TRUSTSTORE_PATH));
            sslKeystoreConfiguration.setTrustRootPassword(getNodeValue(node, SSL_TRUSTSTORE_PASSWORD));
            sslKeystoreConfiguration.setTrustRootType(getNodeValue(node, SSL_TRUSTSTORE_TYPE));
       */

            if (logger != null) {
                logger.info("Done loading SSL configuration");
            }
        }
        return sslKeystoreConfiguration;
    }

    public static SSLConfiguration getSSLConfiguration(MyLoggingFacade logger, ConfigurationNode node) {
        ConfigurationNode cfgNode = getFirstNode(node, SSL_KEYSTORE_TAG);
        if (cfgNode != null) {
            return getOLDSSLConfiguration(logger, cfgNode);
        }
        cfgNode = getFirstNode(node, SSL_TAG);
        return getNEWSSLConfiguration(logger, cfgNode);
    }
}
