package edu.uiuc.ncsa.security.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/23/18 at  11:44 AM
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        FunctorParserTest.class,
        JFunctorFactoryTests.class,
        JFunctorTest.class
})
public class TestSuite extends junit.framework.TestSuite {
}
