package edu.uiuc.ncsa.security.util.testing;

import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/21/16 at  1:54 PM
 */
public class TestEnvironment extends AbstractEnvironment {
    public TestEnvironment(TestMap testMap, MyLoggingFacade myLogger) {
        super(myLogger);
        setTestMap(testMap);
    }

    TestMap testMap = null;

    public TestMap getTestMap() {
        return testMap;
    }

    public void setTestMap(TestMap testMap) {
        this.testMap = testMap;
    }

}
