package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.delegation.storage.JSONUtil;
import edu.uiuc.ncsa.security.util.ssl.SSLConfiguration;
import edu.uiuc.ncsa.security.util.ssl.SSLConfigurationUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.LinkedList;

import static edu.uiuc.ncsa.security.core.configuration.Configurations.*;
import static edu.uiuc.ncsa.security.core.configuration.Configurations.getFirstAttribute;
import static edu.uiuc.ncsa.security.core.configuration.Configurations.getNodeValue;

/**
 * A utility that loads the configuration from a node and has the tags, etc. for it.
 * <p>Created by Jeff Gaynor<br>
 * on 5/4/16 at  8:50 AM
 */
public class LDAPConfigurationUtil {
    public static final String LDAP_TAG = "ldap";
    public static final String LDAP_PASSWORD_TAG = "password";
    public static final String LDAP_ADDRESS_TAG = "address";
    public static final String LDAP_SEARCH_BASE_TAG = "searchBase";
    public static final String LDAP_SEARCH_ATTRIBUTES_TAG = "searchAttributes";
    public static final String LDAP_SEARCH_ATTRIBUTE_TAG = "attribute";
    public static final String LDAP_SECURITY_PRINCIPAL_TAG = "principal";
    public static final String LDAP_PORT_TAG = "port";
    public static final String LDAP_CONTEXT_NAME_TAG = "contextName";
    public static final String LDAP_ENABLED_TAG = "enabled";
    public static final int DEFAULT_PORT = 636;
    public static final String LDAP_AUTH_TYPE = "authorizationType";
    public static final String LDAP_AUTH_NONE = "none";
    public static final int LDAP_AUTH_UNSPECIFIED_KEY = 0;
    public static final int LDAP_AUTH_NONE_KEY = 1;
    public static final String LDAP_AUTH_SIMPLE = "simple";
    public static final int LDAP_AUTH_SIMPLE_KEY = 10;
    public static final String LDAP_AUTH_STRONG = "strong";
    public static final int LDAP_AUTH_STRONG_KEY = 100;
    public static final String RETURN_NAME = "returnName"; // attribute for the attribute tag.E.g. <attribute returnName="foo">bar</attributte>
    public static final String RETURN_AS_LIST = "returnAsList"; // attribute for the attribute tag.E.g. <attribute returnAsList="true">bar</attributte>

    public static class AttributeEntry {
        public AttributeEntry(String sourceName, String targetName, boolean isList) {
            this.isList = isList;
            this.sourceName = sourceName;
            this.targetName = targetName;
        }

        public String sourceName;
        public String targetName;
        public boolean isList = false;

    }

    public static LDAPConfiguration getLdapConfiguration(MyLoggingFacade logger, ConfigurationNode node) {
        LDAPConfiguration ldapConfiguration = new LDAPConfiguration();
        logger.info("Starting to load LDAP configuration.");
        ConfigurationNode ldapNode = Configurations.getFirstNode(node, LDAP_TAG);


        if (ldapNode == null) {
            logger.info("No LDAP configuration found.");
            ldapConfiguration.setEnabled(false);
            return ldapConfiguration;
        }
        // There is a configuration, so implicitly enable this.
        ldapConfiguration.setEnabled(true);
        SSLConfiguration sslConfiguration = SSLConfigurationUtil.getSSLConfiguration(logger, ldapNode);
        ldapConfiguration.setSslConfiguration(sslConfiguration);

        ldapConfiguration.setServer(getNodeValue(ldapNode, LDAP_ADDRESS_TAG));
        String x = getNodeValue(ldapNode, LDAP_CONTEXT_NAME_TAG);
        ldapConfiguration.setContextName(x == null ? "" : x); // set to empty string if missing.
        ldapConfiguration.setSecurityPrincipal(getNodeValue(ldapNode, LDAP_SECURITY_PRINCIPAL_TAG));
        // Do stuff related to searching
        ConfigurationNode attributeNode = getFirstNode(ldapNode, LDAP_SEARCH_ATTRIBUTES_TAG);
        if (attributeNode == null) {
            ldapConfiguration.setSearchAttributes(null);
        } else {
            for (int i = 0; i < attributeNode.getChildrenCount(); i++) {
                // only get the elements tagged as attributes in case others get added in the future.
                if (LDAP_SEARCH_ATTRIBUTE_TAG.equals(attributeNode.getChild(i).getName())) {
                    Object kid = attributeNode.getChild(i).getValue();
                    if (kid != null) {
                        String returnName = getFirstAttribute(attributeNode.getChild(i), RETURN_NAME);
                        if (returnName == null) {
                            returnName = kid.toString(); // name returned is the same as the search attribute
                        }
                        x = getFirstAttribute(attributeNode.getChild(i), RETURN_AS_LIST);
                        boolean returnAsList = false;
                        if (x != null) {
                            try {
                                returnAsList = Boolean.parseBoolean(x);
                            } catch (Throwable t) {
                                // Rock on.
                            }
                        }
                        AttributeEntry attributeEntry = new AttributeEntry(kid.toString(), returnName, returnAsList);
                        ldapConfiguration.getSearchAttributes().put(attributeEntry.sourceName, attributeEntry);
                    }
                }
            }
        }
        ldapConfiguration.setSearchBase(getNodeValue(ldapNode, LDAP_SEARCH_BASE_TAG));
        //   ldapConfiguration.setPort(DEFAULT_PORT);

        String port = getNodeValue(ldapNode, LDAP_PORT_TAG);

        try {
            if (port != null) {
                ldapConfiguration.setPort(Integer.parseInt(port));
            }
        } catch (Throwable t) {
            logger.warn("Could not parse port \"" + port + "\" for the LDAP handler. Using default of no port.");
        }

        ldapConfiguration.setPassword(getNodeValue(ldapNode, LDAP_PASSWORD_TAG));
        x = getFirstAttribute(ldapNode, LDAP_ENABLED_TAG);
        if (x != null) {
            try {
                ldapConfiguration.setEnabled(Boolean.parseBoolean(x));
            } catch (Throwable t) {
                logger.warn("Could not parsed enabled flag value of \"" + x + "\". Assuming LDAP is enabled.");
            }
        }
        x = getFirstAttribute(ldapNode, LDAP_AUTH_TYPE);
        ldapConfiguration.setAuthType(getAuthType(x));
/*
        ldapConfiguration.setAuthType(LDAP_AUTH_UNSPECIFIED_KEY); // default
        if (x != null) {
            // If specified, figure out what they want.
            if (x.equals(LDAP_AUTH_NONE)) {
                ldapConfiguration.setAuthType(LDAP_AUTH_NONE_KEY);
            }
            if (x.equals(LDAP_AUTH_SIMPLE)) {
                ldapConfiguration.setAuthType(LDAP_AUTH_SIMPLE_KEY);
            }
            if (x.equals(LDAP_AUTH_STRONG)) {
                ldapConfiguration.setAuthType(LDAP_AUTH_STRONG_KEY);
            }
        }
*/
        logger.info("LDAP configuration loaded.");

        return ldapConfiguration;
    }

    public static JSONObject toJSON(LDAPConfiguration configuration) {
        JSONUtil jsonUtil = getJSONUtil();
        JSONObject ldap = new JSONObject();
        JSONObject content = new JSONObject();
        ldap.put(LDAP_TAG, content);

        jsonUtil.setJSONValue(ldap, LDAP_ADDRESS_TAG, configuration.getServer());
        jsonUtil.setJSONValue(ldap, LDAP_PORT_TAG, configuration.getPort());
        jsonUtil.setJSONValue(ldap, LDAP_ENABLED_TAG, configuration.isEnabled());
        jsonUtil.setJSONValue(ldap, LDAP_ENABLED_TAG, configuration.isEnabled());
        jsonUtil.setJSONValue(ldap, LDAP_AUTH_TYPE, configuration.getAuthType());
        jsonUtil.setJSONValue(ldap, LDAP_AUTH_TYPE, configuration.getAuthType());
        if (configuration.getAuthType() == LDAP_AUTH_NONE_KEY) {
            // nothing to do
        }
        if (configuration.getAuthType() == LDAP_AUTH_SIMPLE_KEY) {
            jsonUtil.setJSONValue(ldap, LDAP_PASSWORD_TAG, configuration.getPassword());
            jsonUtil.setJSONValue(ldap, LDAP_SECURITY_PRINCIPAL_TAG, configuration.getSecurityPrincipal());
        }

        // Now for the search attributes
        JSONArray searchAttributes = new JSONArray();
        for (String key : configuration.getSearchAttributes().keySet()) {
            AttributeEntry ae = configuration.getSearchAttributes().get(key);
            JSONObject entry = new JSONObject();
            entry.put("name", ae.sourceName);
            entry.put(RETURN_AS_LIST, ae.isList);
            entry.put(RETURN_NAME, ae.targetName);
            searchAttributes.add(entry);
        }
        jsonUtil.setJSONValue(ldap, LDAP_SEARCH_ATTRIBUTES_TAG, searchAttributes);
        jsonUtil.setJSONValue(ldap, LDAP_SEARCH_BASE_TAG, configuration.getSearchBase());
        jsonUtil.setJSONValue(ldap, LDAP_CONTEXT_NAME_TAG, configuration.getContextName());


        return ldap;
    }

    static JSONUtil jsonUtil = null;

    protected static JSONUtil getJSONUtil() {
        if (jsonUtil == null) {
            jsonUtil = new JSONUtil(LDAP_TAG);
        }
        return jsonUtil;
    }

    protected static int getAuthType(String x) {
        int rc = LDAP_AUTH_UNSPECIFIED_KEY; // default
        if (x != null) {
            // If specified, figure out what they want.
            if (x.equals(LDAP_AUTH_NONE)) {
                rc = LDAP_AUTH_NONE_KEY;
            }
            if (x.equals(LDAP_AUTH_SIMPLE)) {
                rc = LDAP_AUTH_SIMPLE_KEY;
            }
            if (x.equals(LDAP_AUTH_STRONG)) {
                rc = LDAP_AUTH_STRONG_KEY;
            }
        }
        return rc;

    }

    public static LDAPConfiguration fromJSON(JSONObject json) {
        JSONUtil jsonUtil = getJSONUtil();

        LDAPConfiguration config = new LDAPConfiguration();
        config.setContextName(jsonUtil.getJSONValueString(json, LDAP_CONTEXT_NAME_TAG));
        config.setEnabled(jsonUtil.getJSONValueBoolean(json, LDAP_ENABLED_TAG));
        String x = jsonUtil.getJSONValueString(json, LDAP_AUTH_TYPE);
        config.setAuthType(getAuthType(x)); // default
        config.setServer(jsonUtil.getJSONValueString(json, LDAP_ADDRESS_TAG));
        config.setPort(jsonUtil.getJSONValueInt(json, LDAP_ADDRESS_TAG));
        Object se = jsonUtil.getJSONValue(json, LDAP_SEARCH_ATTRIBUTES_TAG);
        if (se instanceof JSONArray) {
            JSONArray searchAttributes = (JSONArray) se;
            LinkedList<AttributeEntry> attributeEntries = new LinkedList<>();
            for (int i = 0; i < searchAttributes.size(); i++) {
                JSONObject current = searchAttributes.getJSONObject(i);
                String name = current.getString("name");
                String targetName = current.getString(RETURN_NAME);
                boolean isList = current.getBoolean(RETURN_AS_LIST);
                AttributeEntry attributeEntry = new AttributeEntry(name, targetName, isList);
                config.getSearchAttributes().put(attributeEntry.sourceName, attributeEntry);
            }

            config.setSearchBase(jsonUtil.getJSONValueString(json, LDAP_SEARCH_BASE_TAG));
            config.setSecurityPrincipal(jsonUtil.getJSONValueString(json, LDAP_SECURITY_PRINCIPAL_TAG));
            config.setPassword(jsonUtil.getJSONValueString(json, LDAP_PASSWORD_TAG));

        }
        return config;
    }

}
