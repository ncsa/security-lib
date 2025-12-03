package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;

public class CFBundleInheritenceTest extends AbstractConfigInheritenceTest {
    @Override
    protected MultiConfigurationsInterface getConfiguration(String filename)  {
        return getCFConfiguration(filename);
    }
}
