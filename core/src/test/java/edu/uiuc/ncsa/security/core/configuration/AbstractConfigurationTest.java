package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;
import edu.uiuc.ncsa.security.core.cf.CFBundle;
import edu.uiuc.ncsa.security.core.cf.CFLoader;
import edu.uiuc.ncsa.security.core.cf.CFMultiConfigurations;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import junit.framework.TestCase;

import java.io.File;

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

    protected abstract MultiConfigurationsInterface getConfiguration(String filename) ;

    protected String getFirstAttribute(MultiConfigurationsInterface mci, Object nodes, String attributeName) {
        return ((CFMultiConfigurations) mci).getFirstAttribute((CFNode) nodes, attributeName);
    }

    protected String getNodeContents(MultiConfigurationsInterface mci, Object nodes, String nodeName) {
        return ((CFMultiConfigurations) mci).getNodeContents((CFNode) nodes, nodeName);
    }

    //
    protected Object getNamedConfiguration(MultiConfigurationsInterface mci, String cfgName)  {
        return ((CFMultiConfigurations) mci).getNamedConfig(cfgName);
    }

    protected MultiConfigurationsInterface getCFConfiguration(String filename)  {
        CFLoader cfLoader = new CFLoader();
        CFBundle cfBundle = cfLoader.loadBundle(new File(filename), "service");
        return cfBundle.getMultiConfigurations();
    }
}
