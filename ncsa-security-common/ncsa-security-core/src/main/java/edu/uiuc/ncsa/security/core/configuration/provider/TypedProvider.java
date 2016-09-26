package edu.uiuc.ncsa.security.core.configuration.provider;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.configuration.tree.DefaultConfigurationNode;

import java.util.List;

/**
 * Provide an instance of something that is a given type. Note that type attributes are for everything of a given type, e.g.
 * for all sql stores that share the same schema and must have this as part of their configuration information.
 * Typically this would be one of a category of objects, all of which share certain attributes.
 * At this point, only the first of a given item is returned. So for example, store that has multiple
 * user objects would only have the first one found.
 * <p>Created by Jeff Gaynor<br>
 * on 1/15/12 at  12:14 PM
 */
public abstract class TypedProvider<T> extends HierarchicalConfigProvider<T> {
    String type;
    String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TypedProvider(ConfigurationNode config, String type, String target) {
        super(config);
        this.type = type;
        this.target = target;
    }

    public TypedProvider() {
    }

    protected TypedProvider(String type, String target) {
        this.target = target;
        this.type = type;
    }

    /**
     * This is the parent node for this configuration. It has information for this type of object.
     *
     * @return
     */
    public ConfigurationNode getTypeConfig() {
        return typeConfig;
    }

    public void setTypeConfig(ConfigurationNode typeConfig) {
        this.typeConfig = typeConfig;
    }

    ConfigurationNode typeConfig;

    @Override
    protected boolean checkEvent(CfgEvent cfgEvent) {
        if (cfgEvent.getConfiguration().getName().equals(getType()) && !cfgEvent.getConfiguration().getChildren(getTarget()).isEmpty()) {
            setTypeConfig(cfgEvent.getConfiguration());
            setConfig((ConfigurationNode) cfgEvent.getConfiguration().getChildren(getTarget()).get(0));
            return true;
        }
        return false;
    }

    /**
     * Get an attribute for this type.
     *
     * @param key
     * @return
     */
    public String getTypeAttribute(String key) {
        List list = getTypeConfig().getAttributes(key);
        if (list.isEmpty()) {
            return null;
        }
        DefaultConfigurationNode node = (DefaultConfigurationNode) list.get(0);
        return node.getValue().toString();
    }

    protected String getTypeAttribute(String key, String defaultValue) {
        String x = getTypeAttribute(key);
        if (x == null) {
            x = defaultValue;
        }
        return x;
    }


    public int getTypeIntAttribute(String key, int defaultValue) {
        String x = getTypeAttribute(key);

        if (x == null) {
            return defaultValue;
        }
        // this might fail if the integer is of the wrong format. That is ok and no default should be supplied.
        return Integer.parseInt(x);
    }

    public int getTypeIntAttribute(String key) {
        return Integer.parseInt(getTypeAttribute(key));
    }

    public boolean getTypeBooleanAttribute(String key) {
        return Boolean.parseBoolean(getTypeAttribute(key));
    }

    @Override
    public String toString() {
        String className = getClass().getSimpleName();
        if(className.length() == 0){
            className="<anonymous>";
        }
        return className + "[type=" + getType()+", target=" + getTarget() + ", cfg node=" + (getConfig().getName()==null?"(null)":getConfig().getName()) + "]";
    }
}
