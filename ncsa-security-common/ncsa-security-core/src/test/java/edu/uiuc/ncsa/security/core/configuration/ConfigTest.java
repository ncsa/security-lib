package edu.uiuc.ncsa.security.core.configuration;

import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * Abstract class that allows for testing a configuration. Implement the {@link #getConfiguration()}
 * method to pull in the named configuration.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  10:49 AM
 */
public abstract class ConfigTest extends TestCase {

    /**
     * Implements this to get the configuration file with a given name from the resources directory.
     *
     * @return
     */
    abstract protected XMLConfiguration getConfiguration() throws ConfigurationException;

    /**
     * The type of the configuration. At this point it is either "client" or "service"
     *
     * @return
     */
    abstract protected String getConfigurationType();

    protected XMLConfiguration getConfiguration(String resourceName) throws ConfigurationException {
        if (configuration == null) {
            configuration = Configurations.getConfiguration(getClass().getResource(resourceName));
        }

        return configuration;
    }

    XMLConfiguration configuration;


    protected ConfigurationNode getConfig(String configName) throws ConfigurationException {
        return getConfig(getConfiguration(), configName);
    }

    protected ConfigurationNode getConfig(XMLConfiguration cfg, String configName) throws ConfigurationException {
        return Configurations.getConfig(cfg, getConfigurationType(), configName);
    }

    protected void printNodes(ConfigurationNode root) {
        for (Object kid : root.getChildren()) {
            ConfigurationNode cn = (ConfigurationNode) kid;
            say("key=" + cn.getName() + ", value=" + cn.getValue());
            printNodes(cn);
        }

    }

    protected void say(Object x) {
        System.out.println(x.toString());
    }
}
