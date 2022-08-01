package edu.uiuc.ncsa.security.core.configuration;

import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/3/21 at  11:34 AM
 */
public abstract class AbstractInheritanceTest extends TestCase {
    protected MultiConfigurations getConfigurations2(String fileName) throws ConfigurationException {
        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(new File(fileName));
        MultiConfigurations configurations2 = new MultiConfigurations();
        configurations2.ingestConfig(xmlConfiguration, "service");
        return configurations2;
    }


}
