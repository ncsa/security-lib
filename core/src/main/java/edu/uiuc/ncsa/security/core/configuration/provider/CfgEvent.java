package edu.uiuc.ncsa.security.core.configuration.provider;

import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.EventObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/12 at  8:46 AM
 */
public class CfgEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public CfgEvent(Object source, ConfigurationNode config) {
        super(source);
        configuration = config;
    }

    public ConfigurationNode getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ConfigurationNode configuration) {
        this.configuration = configuration;
    }

    ConfigurationNode configuration;
}
