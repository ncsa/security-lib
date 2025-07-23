package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;
import edu.uiuc.ncsa.security.core.cf.CFBundle;
import edu.uiuc.ncsa.security.core.cf.CFLoader;
import edu.uiuc.ncsa.security.core.cf.CFMultiConfigurations;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.io.File;
import java.util.List;

/**
 * Top-level test for Apache and CF configuration testing. Inheritence engine testing is done separately from
 * basic functionality (alias, file includes).
 * <p>Created by Jeff Gaynor<br>
 * on 2/3/21 at  11:34 AM
 */
public abstract class AbstractConfigurationTest extends TestCase {

    void verbose(String x) {
        if (MultipleInheritanceEngine.DEBUG_ON) {
            System.out.println(x);
        }
    }

    protected abstract MultiConfigurationsInterface getConfiguration(String filename) throws ConfigurationException;

    protected String getFirstAttribute(MultiConfigurationsInterface mci, Object nodes, String attributeName) throws ConfigurationException {
        if (mci instanceof CFMultiConfigurations) {
            return ((CFMultiConfigurations) mci).getFirstAttribute((CFNode) nodes, attributeName);
        }
        return ((MultiConfigurations) mci).getFirstAttribute((List<ConfigurationNode>) nodes, attributeName);

    }

    protected String getNodeContents(MultiConfigurationsInterface mci, Object nodes, String nodeName) throws ConfigurationException {
        if (mci instanceof CFMultiConfigurations) {
            return ((CFMultiConfigurations) mci).getNodeContents((CFNode) nodes, nodeName);
        }
        return ((MultiConfigurations) mci).getNodeContents((List<ConfigurationNode>) nodes, nodeName);
    }
//
    protected Object getNamedConfiguration(MultiConfigurationsInterface mci, String cfgName) throws ConfigurationException {
        if (mci instanceof CFMultiConfigurations) {
            return ((CFMultiConfigurations)mci).getNamedConfig(cfgName);
        }
        return ((MultiConfigurations)mci).getNamedConfig(cfgName);
    }

    protected MultiConfigurationsInterface getCFConfiguration(String filename) throws ConfigurationException {
        CFLoader cfLoader = new CFLoader();
        CFBundle cfBundle  = cfLoader.loadBundle(new File(filename), "service");
        return cfBundle.getMultiConfigurations();
    }

    protected MultiConfigurationsInterface getApacheConfiguration(String filename) {
        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(new File(filename));
        MultiConfigurations configurations2 = new MultiConfigurations();
        configurations2.ingestConfig(xmlConfiguration, "service");
        return configurations2;
    }
}
