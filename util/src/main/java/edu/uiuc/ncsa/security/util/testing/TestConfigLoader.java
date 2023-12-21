package edu.uiuc.ncsa.security.util.testing;

import edu.uiuc.ncsa.security.core.util.LoggingConfigLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.util.configuration.XMLConfigUtil;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This loads a configuration of tests from a configuration file.
 * This is not used in any other package and exists as a way to test configurations, naught else.
 * <p>Created by Jeff Gaynor<br>
 * on 4/21/16 at  1:55 PM
 */
public abstract class TestConfigLoader<T extends TestEnvironment> extends LoggingConfigLoader<T> {
    public static final String TESTS_TAG = "tests";
    public static final String TEST_TAG = "test";
    public static final String TEST_NAME_TAG = "name";
    /**
     * In the test file, testers can disable the test by specifying this value as true or false.
     * The default is to run the test.
     */
    public static final String TEST_ENABLE_TAG = "enabled";
    public static final String DATA_TAG = "data";
    public static final String DATA_TYPE_TAG = "type";
    public static final String LIST_DATA_TYPE_TAG = "list";
    public static final String DATA_NAME_TAG = "name";
    /**
     * The configuration file path and name is given as an argument on the command line.
     * This tells the system what the name of the parameter (given with the -D option) is.
     */
    public static final String CONFIG_FILE_KEY = "test:config.file";
    public static final String CONFIG_NAME_KEY = "test:config.name";

    public TestConfigLoader(ConfigurationNode node) {
        super(node);
    }

    public TestConfigLoader(ConfigurationNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    public static void main(String[] args) {
        // Args are file path and confi name
        ConfigurationNode cn = XMLConfigUtil.findConfiguration(args[0], args[1], TESTS_TAG);
        TestConfigLoader<TestEnvironment> t = new TestConfigLoader<TestEnvironment>(cn) {
            @Override
            public String getVersionString() {
                return "1.0";
            }

            @Override
            public HashMap<String, String> getConstants() {
                return null;
            }
        };
        TestEnvironment testEnvironment = t.load();
        System.out.println("Test size=" + testEnvironment.getTestMap().size());
    }

    @Override
    public T load() {
         TestMap testMap = new TestMap();

        List<ConfigurationNode> kids = cn.getChildren(TEST_TAG);
        for (ConfigurationNode c : kids) {
            TestData data = new TestData();
            String content = "";
            String attribName = null;
            String attribValue = null;
            String testName = "";

            data.put(TestData.TEST_ENABLE_KEY, Boolean.TRUE); // default is to run test
            // First, grab all of the attributes -- in particular the name
            boolean hasName = false;
            for (int i = 0; i < c.getAttributeCount(); i++) {
                attribName = c.getAttribute(i).getName();
                attribValue = c.getAttribute(i).getValue().toString();
                if (attribName.equals(TEST_NAME_TAG)) {
                    hasName = true;
                    testName = attribValue;
                }
                if (attribName.equals(TEST_ENABLE_TAG)) {
                    data.put(TestData.TEST_ENABLE_KEY, Boolean.parseBoolean(attribValue));
                }
            }// end for

            if (!hasName) {
                throw new IllegalStateException("Error: The test has no name. Cannot process.");
            }
            testMap.put(testName, data);

            // Now we create the individual data elements for the test and stash them in the testData object.
            List<ConfigurationNode> dataNodes = c.getChildren(DATA_TAG);
            for (ConfigurationNode dataNode : dataNodes) {
                Object entry = null;
                String name = null;
                boolean isList = false;
                for (int i = 0; i < dataNode.getAttributeCount(); i++) {
                    attribName = dataNode.getAttribute(i).getName();
                    attribValue = dataNode.getAttribute(i).getValue().toString();
                    Object x = dataNode.getValue();
                    if (x == null) {
                        continue; //nothing to do
                    }
                    content = x.toString();

                    if (attribName.equals(DATA_NAME_TAG)) {
                        name = attribValue;
                    }
                    if (attribName.equals(DATA_TYPE_TAG)) {

                        if (attribValue.equals(LIST_DATA_TYPE_TAG)) {
                            isList = true;
                        }
                    }

                } // end attribute search
                if (isList) {
                    LinkedList<String> list = new LinkedList<String>();
                    StringTokenizer st = new StringTokenizer(content, "\n");
                    while (st.hasMoreTokens()) {
                        list.add(st.nextToken().trim());
                    }

                    entry = list;

                } else {
                    entry = content;
                }

                data.put(name, entry); // the key is the name attribute of the data element.
            } // end data element loop
        }
        TestEnvironment testEnvironment = new TestEnvironment(testMap, myLogger);
        return (T) testEnvironment;
    }

    @Override
    public T createInstance() {
        return null;
    }
}
