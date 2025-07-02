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

/*
    A simple complete config file

<!-- A sample config for testing the loader -->
<config>
    <test name="test0"
          enabled="true">
        <data name="string0">This is a string</data>
        <data name="myList">a;b;c;d;e</data>
    </test>
</config>


 */
public abstract class TestConfigLoader<T extends TestEnvironment> extends LoggingConfigLoader<T> {
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
    public static final String INTEGER_DATA_TYPE_TAG = "int";
    public static final String DATA_NAME_TAG = "name";


    public TestConfigLoader(ConfigurationNode node) {
        super(node);
    }

    public TestConfigLoader(ConfigurationNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    public static void main(String[] args) {
        // Args are file path and config name
        ConfigurationNode cn = XMLConfigUtil.findConfiguration(args[0], args[1], TEST_TAG);
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
        System.out.println("Test size=" + testEnvironment.getTestData().size());
        System.out.println("Map :\n" + testEnvironment.getTestData());
    }

    @Override
    public T load() {
         TestMap testMap = new TestMap();

            TestData data = new TestData();
            String content = "";
            String attribName = null;
            String attribValue = null;
            String testName = "";

            data.put(TestData.TEST_ENABLE_KEY, Boolean.TRUE); // default is to run test
            // First, grab all of the attributes -- in particular the name
            boolean hasName = false;
            for (int i = 0; i < cn.getAttributeCount(); i++) {
                attribName = cn.getAttribute(i).getName();
                attribValue = cn.getAttribute(i).getValue().toString();
                switch (attribName) {
                    case TEST_NAME_TAG:
                        testName = attribValue;
                        hasName = true;
                        break;
                        case TEST_ENABLE_TAG:
                            data.put(TestData.TEST_ENABLE_KEY, Boolean.parseBoolean(attribValue)); // default is to run test
                            break;
                    default:
                        data.put(attribName, attribValue);
                }

            }// end for

            if (!hasName) {
                throw new IllegalStateException("Error: The test has no name. Cannot process.");
            }
            testMap.put(testName, data);

            // Now we create the individual data elements for the test and stash them in the testData object.
            List<ConfigurationNode> dataNodes = cn.getChildren(DATA_TAG);
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
                        switch (attribValue) {
                            case LIST_DATA_TYPE_TAG:
                                LinkedList<String> list = new LinkedList<String>();
                                StringTokenizer st = new StringTokenizer(content, ";");
                                while (st.hasMoreTokens()) {
                                    list.add(st.nextToken().trim());
                                }
                                entry = list;
                                break;
                            case INTEGER_DATA_TYPE_TAG:
                                entry = Integer.parseInt(content);
                                break;
                            default:
                                entry = content;
                        }
                    }else{
                        entry = content;
                    }

                } // end attribute search

                data.put(name, entry); // the key is the name attribute of the data element.
            } // end data element loop
        TestEnvironment testEnvironment = new TestEnvironment(data, myLogger);
        return (T) testEnvironment;
    }

    @Override
    public T createInstance() {
        return null;
    }
}
