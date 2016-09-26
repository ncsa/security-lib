package edu.uiuc.ncsa.security.core.configuration.provider;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;

import javax.inject.Provider;
import java.util.LinkedList;
import java.util.List;


/**
 * <br><br>All configurations are interpreted locally,
 * that is to say, it is up to something else to hand over the relevant node for this provider.
 * Generally providers should not be aware of the complete hierarchy since that may be hard or impossible to
 * navigate without a lot more information.<br><br>
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/12 at  1:57 PM
 */
public abstract class HierarchicalConfigProvider<T> implements Provider<T>, CfgEventListener {
    LinkedList<CfgEventListener> listeners = new LinkedList<CfgEventListener>();

    public HierarchicalConfigProvider() {
    }


    public void addListener(CfgEventListener c) {
        listeners.add(c);
    }

    public void removeListener(CfgEventListener x) {
        listeners.remove(x);
    }

    protected T fireComponentFound(CfgEvent cfgEvent) {
        for (CfgEventListener cel : listeners) {
            T t = (T) cel.componentFound(cfgEvent);
            if (t != null) {
                return t;
            }
        }
        return null;
    }



    public ConfigurationNode getConfig() {
        return config;
    }

    public void setConfig(ConfigurationNode config) {
        this.config = config;
    }

    ConfigurationNode config;

    public HierarchicalConfigProvider(ConfigurationNode config) {
        this.config = config;
    }

    /**
     * Gets the attribute with the given key. This slaps "[@" and "]" around the key to conform wit
     * how Apache resolves these.
     *
     * @param key
     * @return
     */
    public String getAttribute(String key) {
        // return getConfig().getString(toAttrKey(key));
        List list = getConfig().getAttributes(key);
        if (list.isEmpty()) {
            return null;
        }
        DefaultConfigurationNode node = (DefaultConfigurationNode) list.get(0);
        return node.getValue().toString();
    }

    /**
     * utility method to supply a default value if the given value is missing.
     * @param key
     * @param defaultValue
     * @return
     */
    protected String getAttribute(String key, String defaultValue) {
        String x = getAttribute(key);
        if(x == null){
            x = defaultValue;
        }
        return x;
    }


    public int getIntAttribute(String key, int defaultValue) {
            String x = getAttribute(key);

        if(x == null){
            return defaultValue;
        }
        // this might fail if the integer is of the wrong format. That is ok and no default should be supplied.
        return Integer.parseInt(x);
    }

    public int getIntAttribute(String key) {
        return Integer.parseInt(getAttribute(key));
    }

    public boolean getBooleanAttribute(String key) {
        return Boolean.parseBoolean(getAttribute(key));
    }

    /**
     * Check if the configuration is of the indicated type. This means that the root node has the name
     * of the component, e.g. fileStore, mysql or whatever.
     *
     * @param name
     * @return
     */
    public boolean isA(String name) {
        String x = getConfig().getName();
        if (x == null || x.length() == 0) return false;
        return x.equals(name);
    }

    /**
     * Does this have the named component? In other words, is there
     * a configuration of this name one level down?
     *
     * @param name
     * @return
     */
    public boolean hasA(String name) {
        return 0 < getConfig().getChildren(name).size();
    }

    /**
     * Return the given configuration
     *
     * @param name
     * @return
     */
    public ConfigurationNode getConfigurationAt(String name) {
        List list = getConfig().getChildren(name);
        if(list.isEmpty()){
            return null;
        }
        return (ConfigurationNode) list.get(0);
    }

    /**
     * Checks that the event applies to this component. The type is the component and the target is Normally you set the component key as a static field in the
     * class (e.g. "mysql") and pass it along in the the {@link #componentFound(CfgEvent)}
     * method. If checkEvent fails, do no more processing with the event.
     * @param cfgEvent
     * @return
     */
   abstract  protected boolean checkEvent(CfgEvent cfgEvent);


}
