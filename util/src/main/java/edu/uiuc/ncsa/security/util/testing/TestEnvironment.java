package edu.uiuc.ncsa.security.util.testing;

import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.util.List;

/**
 * An implementation of {@link AbstractEnvironment} solely for testing.
 * Thjis can be used to test the environment class or as part of testing
 * CLIs that need an environment.
 * <p>Created by Jeff Gaynor<br>
 * on 4/21/16 at  1:54 PM
 */
public class TestEnvironment extends AbstractEnvironment {
    public TestEnvironment(TestData testData, MyLoggingFacade myLogger) {
        super(myLogger);
        setTestData(testData);
    }

    TestData testData = null;

    public TestData getTestData() {
        return testData;
    }

    public void setTestData(TestData testData) {
        this.testData = testData;
    }

    public String getString(String key) {
        return testData.get(key).toString();
    }

    public List getList(String key) {
        return (List) testData.get(key);
    }

    public boolean isEmpty() {
        return testData.isEmpty();
    }

    public boolean hasValue(String key) {
        return testData.containsKey(key);
    }

    public boolean isString(String key) {
        Object o = testData.get(key);
        return o instanceof String;
    }

    public boolean isList(String key) {
        Object o = testData.get(key);
        return o instanceof List;
    }

}
