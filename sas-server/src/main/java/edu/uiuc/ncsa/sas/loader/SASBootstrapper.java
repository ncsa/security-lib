package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.servlet.Bootstrapper;
import edu.uiuc.ncsa.security.servlet.Initialization;
import edu.uiuc.ncsa.security.servlet.ServletConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.servlet.ServletContext;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  4:04 PM
 */
public class SASBootstrapper extends Bootstrapper {
    public static final String SAT_CONFIG_FILE_KEY = "sat:server.config.file";
    public static final String SAT_CONFIG_NAME_KEY = "sat:server.config.name";
    public static final String SAT_CONFIG_TAG = "sat";


    protected ConfigurationNode getNode(ServletContext servletContext) throws Exception {
        return ServletConfigUtil.findConfigurationNode(servletContext, SAT_CONFIG_FILE_KEY, SAT_CONFIG_NAME_KEY, SAT_CONFIG_TAG);
    }

    @Override
    public SASConfigurationLoader getConfigurationLoader(ServletContext servletContext) throws Exception {
        return getConfigurationLoader(getNode(servletContext));
    }

    @Override
    public SASConfigurationLoader getConfigurationLoader(ConfigurationNode node) throws MyConfigurationException {
        return new SASConfigurationLoader(node);
    }

    @Override
    public Initialization getInitialization() {
        return new SASServletInitializer();
    }
}