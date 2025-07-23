package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;
import org.apache.commons.configuration.ConfigurationException;

public class ApacheConfigInheritenceTest extends AbstractConfigInheritenceTest {
    @Override
    protected MultiConfigurationsInterface getConfiguration(String filename) throws ConfigurationException {
        return getApacheConfiguration(filename);
    }
}
