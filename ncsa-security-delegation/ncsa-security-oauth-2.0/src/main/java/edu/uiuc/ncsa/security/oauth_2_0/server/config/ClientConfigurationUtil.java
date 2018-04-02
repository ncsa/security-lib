package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * This will read the configuration. It will take the old type of configuration file
 * and transform it into the newer form or take the new format.
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:37 PM
 */
public class ClientConfigurationUtil {
    public static final String CONFIG_KEY="config";

    public static AdminClientConfig getAdminClientConfig(ConfigurationNode node){
        return new AdminClientConfig();
    }

    public static ClientConfiguration getSecureClientConfig(ConfigurationNode node){
        return new ClientConfiguration();
    }

    public static PublicClientConfig getPublicClientConfig(ConfigurationNode node){
        return new PublicClientConfig();
    }
}
