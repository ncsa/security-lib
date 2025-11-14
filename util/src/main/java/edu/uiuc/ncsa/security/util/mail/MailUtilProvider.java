package edu.uiuc.ncsa.security.util.mail;

import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.HierarchicalConfigProvider;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.util.configuration.XMLConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provider for the {@link edu.uiuc.ncsa.security.util.mail.MailUtil} from an XML configuration file.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  4:56 PM
 */
public class MailUtilProvider extends HierarchicalConfigProvider<MailUtil> implements MailConfigurationTags {

    public MailUtilProvider() {
    }

    public MailUtilProvider(ConfigurationNode config) {
        super(config);
    }

    public MailUtilProvider(CFNode config) {
        super(config);
    }

    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        if (cfgEvent.getName().equals(MAIL)) {
            if (hasCFNode()) {
                setCFNode(cfgEvent.getCFNode());
            } else {
                setConfig(cfgEvent.getConfiguration());
            }
            return true;
        }
        return false;
    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        if (checkEvent(configurationEvent)) {
            return get();
        }
        return null;
    }

    MailEnvironment me = null;

    protected MailEnvironment getME() {
        parseIt();
        if (me != null) {
            return me;
        }
        if (hasCFNode()) {
            me = oldME(getCFNode());
        } else {
            me = oldME(getConfig());
        }
        return me;
    }


    protected MailEnvironment oldME(ConfigurationNode node) {
        try {
            String x = getAttribute(MAIL_PORT, "-1");
            int port = Integer.parseInt(x); // if this bombs, catch it.
            String y = getAttribute(MAIL_THROTTLE_INTERVAL, "none");
            long throttleInterval = -1L;
            if (!y.equals("none")) {
                throttleInterval = XMLConfigUtil.getValueSecsOrMillis(y, true);
            }
            MailEnvironment me = new MailEnvironment(
                    Boolean.parseBoolean(getAttribute(MAIL_ENABLED, "false")), //enabled
                    getAttribute(MAIL_SERVER, "none"), //server
                    port, //port
                    getAttribute(MAIL_PASSWORD, "changeme"), //password
                    getAttribute(MAIL_USERNAME, null), //from
                    getAttribute(MAIL_RECIPIENTS), //recipients
                    Configurations.getNodeValue(node, MAIL_MESSAGE_TEMPLATE),// message template
                    Configurations.getNodeValue(node, MAIL_SUBJECT_TEMPLATE), // subject template
                    Boolean.parseBoolean(getAttribute(MAIL_USE_SSL, "false")), //use ssl
                    Boolean.parseBoolean(getAttribute(MAIL_START_TLS, "false")),
                    throttleInterval); //use start tls (mostly for gmail)
            return me;

        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new MyConfigurationException("Error: Could not create mail environment.", t);
        }
    }

    protected MailEnvironment oldME(CFNode node) {
        try {
            String x = getAttribute(MAIL_PORT, "-1");
            int port = Integer.parseInt(x); // if this bombs, catch it.
            String y = getAttribute(MAIL_THROTTLE_INTERVAL, "none");
            long throttleInterval = -1L;
            if (!y.equals("none")) {
                throttleInterval = XMLConfigUtil.getValueSecsOrMillis(y, true);
            }
            MailEnvironment me = new MailEnvironment(
                    Boolean.parseBoolean(getAttribute(MAIL_ENABLED, "false")), //enabled
                    getAttribute(MAIL_SERVER, "none"), //server
                    port, //port
                    getAttribute(MAIL_PASSWORD, "changeme"), //password
                    getAttribute(MAIL_USERNAME, null), //from
                    getAttribute(MAIL_RECIPIENTS), //recipients
                    node.getNodeContents(MAIL_MESSAGE_TEMPLATE),// message template
                    node.getNodeContents(MAIL_SUBJECT_TEMPLATE), // subject template
                    Boolean.parseBoolean(getAttribute(MAIL_USE_SSL, "false")), //use ssl
                    Boolean.parseBoolean(getAttribute(MAIL_START_TLS, "false")),
                    throttleInterval); //use start tls (mostly for gmail)

            return me;

        } catch (Throwable t) {
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            throw new MyConfigurationException("Error: Could not create mail environment.", t);
        }
    }

    protected boolean hasAttribute(ConfigurationNode node, String name) {
        List<ConfigurationNode> attributes = node.getAttributes(name);
        if (attributes != null && !attributes.isEmpty()) {
            return true;
        }
        return false;
    }

    protected String getAttribute(ConfigurationNode node, String name) {
        List<ConfigurationNode> attributes = node.getAttributes(name);
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }
        Object attr = attributes.get(0).getValue();
        if (attr == null) {
            return null;
        }
        String attr0 = attr.toString();
        if (attr0.isEmpty()) return null;
        return attr0;
    }

    // Get the attributes from each entry and stash it in a map. These have no resolution of inheritance.
    protected Map<String, Object> getRawConfig(ConfigurationNode node) {
        HashMap<String, Object> map = new HashMap<>();

        if (hasAttribute(node, MAIL_NAME)) map.put(MAIL_NAME, getAttribute(node, MAIL_NAME));
        if (hasAttribute(node, MAIL_PARENT)) map.put(MAIL_PARENT, getAttribute(node, MAIL_PARENT));
        if (hasAttribute(node, MAIL_PORT)) map.put(MAIL_PORT, Integer.parseInt(getAttribute(node, MAIL_PORT)));
        if (hasAttribute(node, MAIL_ENABLED))
            map.put(MAIL_ENABLED, Boolean.parseBoolean(getAttribute(node, MAIL_ENABLED))); //enabled
        if (hasAttribute(node, MAIL_SERVER)) map.put(MAIL_SERVER, getAttribute(node, MAIL_SERVER));
        if (hasAttribute(node, MAIL_PASSWORD)) map.put(MAIL_PASSWORD, getAttribute(node, MAIL_PASSWORD));
        if (hasAttribute(node, MAIL_USERNAME)) map.put(MAIL_USERNAME, getAttribute(node, MAIL_USERNAME));
        if (hasAttribute(node, MAIL_RECIPIENTS)) map.put(MAIL_RECIPIENTS, getAttribute(node, MAIL_RECIPIENTS));
        if (hasAttribute(node, MAIL_USE_SSL))
            map.put(MAIL_USE_SSL, Boolean.parseBoolean(getAttribute(node, MAIL_USE_SSL)));
        if (hasAttribute(node, MAIL_START_TLS))
            map.put(MAIL_START_TLS, Boolean.parseBoolean(getAttribute(node, MAIL_START_TLS)));

        if(hasAttribute(node, MAIL_THROTTLE_INTERVAL)){
            map.put(MAIL_THROTTLE_INTERVAL, XMLConfigUtil.getValueSecsOrMillis(getAttribute(node, MAIL_THROTTLE_INTERVAL), true));
        }
        String template = Configurations.getNodeValue(node, MAIL_MESSAGE_TEMPLATE);// message template
        if (template != null && !template.isEmpty()) {
            map.put(MAIL_MESSAGE_TEMPLATE, template);
        }
        template = Configurations.getNodeValue(node, MAIL_SUBJECT_TEMPLATE); // subject template
        if (template != null && !template.isEmpty()) {
            map.put(MAIL_SUBJECT_TEMPLATE, template);
        }

        return map;
    }

    protected Map<String, Object> getRawConfig(CFNode node) {
        HashMap<String, Object> map = new HashMap<>();

        if (node.hasAttribute(MAIL_NAME)) map.put(MAIL_NAME, node.getFirstAttribute(MAIL_NAME));
        if (node.hasAttribute(MAIL_PARENT)) map.put(MAIL_PARENT, node.getFirstAttribute(MAIL_PARENT));
        if (node.hasAttribute(MAIL_PORT)) map.put(MAIL_PORT, node.getFirstLongAttribute(MAIL_PORT));
        if (node.hasAttribute(MAIL_ENABLED))
            map.put(MAIL_ENABLED, node.getFirstBooleanValue(MAIL_ENABLED)); //enabled
        if (node.hasAttribute(MAIL_SERVER)) map.put(MAIL_SERVER, node.getFirstAttribute(MAIL_SERVER));
        if (node.hasAttribute(MAIL_PASSWORD)) map.put(MAIL_PASSWORD, node.getFirstAttribute(MAIL_PASSWORD));
        if (node.hasAttribute(MAIL_USERNAME)) map.put(MAIL_USERNAME, node.getFirstAttribute(MAIL_USERNAME));
        if (node.hasAttribute(MAIL_RECIPIENTS)) map.put(MAIL_RECIPIENTS, node.getFirstAttribute(MAIL_RECIPIENTS));
        if (node.hasAttribute(MAIL_USE_SSL))
            map.put(MAIL_USE_SSL, node.getFirstBooleanValue(MAIL_USE_SSL));
        if (node.hasAttribute(MAIL_START_TLS))
            map.put(MAIL_START_TLS, node.getFirstBooleanValue(MAIL_START_TLS));
        if(node.hasAttribute( MAIL_THROTTLE_INTERVAL)){
            map.put(MAIL_THROTTLE_INTERVAL, XMLConfigUtil.getValueSecsOrMillis(node.getFirstAttribute(MAIL_THROTTLE_INTERVAL), true));
        }

        String template = node.getNodeContents(MAIL_MESSAGE_TEMPLATE);// message template
        if (template != null && !template.isEmpty()) {
            map.put(MAIL_MESSAGE_TEMPLATE, template);
        }
        template = node.getNodeContents(MAIL_SUBJECT_TEMPLATE); // subject template
        if (template != null && !template.isEmpty()) {
            map.put(MAIL_SUBJECT_TEMPLATE, template);
        }

        return map;
    }

    protected Map<String, Map> parseIt() {
        // create an initial map of global values.
        Map<String, Object> globalValues;
        if (hasCFNode()) {
            globalValues = getRawConfig(getCFNode());
        } else {
            globalValues = getRawConfig(getConfig());
        }

        Map<String, Map> allMaps = new HashMap<>();
        if (!globalValues.isEmpty()) {
            globalValues.put(MAIL_NAME, MAIL_CONFIG_ROOT);
            globalValues.remove(MAIL_PARENT);
            allMaps.put(MAIL_CONFIG_ROOT, globalValues);
        }
        if(hasCFNode()){
            List<CFNode> kids = getCFNode().getChildren(MAIL_COMPONENT);
            for (CFNode kid : kids) {
                if (kid.getName().equals(MAIL_COMPONENT)) {
                    Map<String, Object> rawMap = getRawConfig(kid);
                    if (rawMap.containsKey(MAIL_NAME)) {
                        allMaps.put(rawMap.get(MAIL_NAME).toString(), rawMap);
                    } else {
                        allMaps.put(MAIL_CONFIG_DEFAULT_NAME, rawMap);
                    }
                }
            }

        }else{
            List<ConfigurationNode> kids = getConfig().getChildren();
            for (ConfigurationNode kid : kids) {
                if (kid.getName().equals(MAIL_COMPONENT)) {
                    Map<String, Object> rawMap = getRawConfig(kid);
                    if (rawMap.containsKey(MAIL_NAME)) {
                        allMaps.put(rawMap.get(MAIL_NAME).toString(), rawMap);
                    } else {
                        allMaps.put(MAIL_CONFIG_DEFAULT_NAME, rawMap);
                    }
                }
            }
        }
        Map<String, Map> allMaps2 = new HashMap<>();

        for (String name : allMaps.keySet()) {
            allMaps2.put(name, resolveParents(allMaps, name));
        }
        return allMaps2;
    }

    protected Map resolveParents(Map<String, Map> allMaps, String name) {
        ArrayList<Map> parents = new ArrayList<>();
        ArrayList<String> namesOfParents = new ArrayList<>();
        Map<String, Object> lastMap = allMaps.get(name);
        parents.add(lastMap);
        namesOfParents.add(name);
        // now we start iterating...
        while (lastMap.containsKey(MAIL_PARENT)) {
            lastMap = allMaps.get(lastMap.get(MAIL_PARENT));
            if (namesOfParents.contains(lastMap.get(MAIL_PARENT))) {
                throw new GeneralException("Error: circular refernce of a parent configuration \"" + lastMap.get(MAIL_PARENT) + "\"to itself");
            }
            parents.add(lastMap);
            namesOfParents.add(lastMap.get(MAIL_NAME).toString());
        }


        // now we have a list of maps with the configuration information in order, We just need to shove these in to a single map.
        Map<String, Object> returnedMap = null;
        if (allMaps.containsKey(MAIL_CONFIG_ROOT)) {
            returnedMap = allMaps.get(MAIL_CONFIG_ROOT);
        } else {
            returnedMap = new HashMap<>();
        }
        for (int i = 0; i < parents.size(); i++) {
            int index = parents.size() - 1 - i; //count backwards so things get over-written in the right order.
            returnedMap.putAll(parents.get(index));
        }
        return returnedMap;
    }

    @Override
    public MailUtil get() {
        if (getConfig() == null) {
            return new MailUtil();
        }
        return new MailUtil(getME());
    }
}
