package edu.uiuc.ncsa.sat.loader;

import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.DBConfigLoader;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  1:00 PM
 */
public class SATConfigurationLoader<T extends SATEnvironment> extends DBConfigLoader<T> {
    public SATConfigurationLoader(ConfigurationNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    public SATConfigurationLoader(ConfigurationNode node) {
        super(node);
    }

    @Override
    public T load() {
        T sate = createInstance();
      // lots of stuff may eventually go here.
        return sate;
    }

    @Override
    public T createInstance() {
        return (T) new SATEnvironment();
    }

    /**
     * Not needed here
     * @return
     */
    @Override
    public HashMap<String, String> getConstants() {
        return null;
    }

    @Override
    public String getVersionString() {
        return "DBLoader " + VERSION_NUMBER;
    }
}
