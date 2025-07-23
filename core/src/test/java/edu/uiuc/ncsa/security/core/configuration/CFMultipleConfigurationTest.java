package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import org.apache.commons.configuration.ConfigurationException;


/**
 * This uses configuration files to test this, since that is the easiest way to get these plus
 * it gives yet other tests for configurations.
 * <p>
 * Also, if you need to, {@link MultipleInheritanceEngine} DEBUG_ON
 * can be enabled manually for deep debugging, which will print a report after resolution.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  10:49 AM
 */
/*
  Running this -- it is run in OA4MP and QDL in their builds as a regression test too.
 */
public class CFMultipleConfigurationTest extends AbstractMultipleConfigurationTest {

    @Override
    protected MultiConfigurationsInterface getConfiguration(String filename) throws ConfigurationException {
       return getCFConfiguration(filename);
    }

}
