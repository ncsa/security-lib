package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.servlet.Bootstrapper;
import edu.uiuc.ncsa.security.servlet.Initialization;
import edu.uiuc.ncsa.security.servlet.ServletXMLConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.servlet.ServletContext;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  4:04 PM
 */
public class SASBootstrapper extends Bootstrapper {
    public static final String SAS_CONFIG_FILE_KEY = "sas:server.config.file";
    public static final String SAS_CONFIG_NAME_KEY = "sas:server.config.name";
    public static final String SAS_CONFIG_TAG = "sas";


    protected ConfigurationNode getNode(ServletContext servletContext) throws Exception {
        return ServletXMLConfigUtil.findConfigurationNode(servletContext, SAS_CONFIG_FILE_KEY, SAS_CONFIG_NAME_KEY, SAS_CONFIG_TAG);
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

    @Override
    public ConfigurationLoader getConfigurationLoader(CFNode node) throws MyConfigurationException {
        throw new NotImplementedException("Need to add support to SAS for CF System");
    }
}
