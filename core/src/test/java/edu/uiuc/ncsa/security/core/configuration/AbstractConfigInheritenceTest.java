package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.MultiConfigurationsInterface;
import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import org.junit.Test;

import java.util.Map;

/**
 * Tests attribute inheritence specifically.
 * <p>Created by Jeff Gaynor<br>
 * on 2/3/21 at  11:33 AM
 */
public abstract class AbstractConfigInheritenceTest extends AbstractConfigurationTest {
    protected String path = DebugUtil.getDevPath() + "/security-lib/core/src/test/resources/cfg_inheritance/";

    /**
     * Shows that overriding a configuration replaces an attribute
     *
     * @throws Exception
     */
    @Test
    public void testAttributeInheritance() throws Exception {
        verbose("testAttributeInheritance:");
        String fileName = path + "attrib-test-A.xml";
        MultiConfigurationsInterface configurations2 = getConfiguration(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        Object nodes = getNamedConfiguration(configurations2, "A");
        assert getFirstAttribute(configurations2, nodes, "test_attrib").equals("Attribute A");
    }

    /**
     * Shows that in an override, the system can still find attributes that have not been overridden
     *
     * @throws Exception
     */
    @Test
    public void testAttributeInheritance2() throws Exception {
        verbose("testAttributeInheritance2:");

        String fileName = path + "attrib-test-B.xml";
        MultiConfigurationsInterface configurations2 = getConfiguration(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        Object nodes = getNamedConfiguration(configurations2, "A");
        assert getFirstAttribute(configurations2, nodes, "other_attrib").equals("Attribute X");
    }

    /**
     * Shows that overriding a node will be resolved.
     *
     * @throws Exception
     */
    @Test
    public void testNodeInheritance1() throws Exception {
        verbose("testNodeInheritance1:");
        String fileName = path + "node-override-test.xml";
        MultiConfigurationsInterface configurations2 = getConfiguration(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        Object nodes = getNamedConfiguration(configurations2, "A");
        assert getNodeContents(configurations2, nodes, "mynode").equals("node A");
    }

    /**
     * Shows that if the node is not overridden it is still found.
     *
     * @throws Exception
     */
    @Test
    public void testNodeInheritance2() throws Exception {
        verbose("testNodeInheritance2:");

        String fileName = path + "node-override-test2.xml";
        MultiConfigurationsInterface configuration = getConfiguration(fileName);
        Map<String, InheritanceList> ro = configuration.getInheritanceEngine().getResolvedOverrides();
        Object nodes = getNamedConfiguration(configuration,"A");
        assert getNodeContents(configuration, nodes, "mynode").equals("node X");
    }

    @Test
    public void testAliasOverrideNode() throws Exception {
        verbose("testAliasOverrideNode:");
        String fileName = path + "alias-override-test.xml";
        MultiConfigurationsInterface configuration = getConfiguration(fileName);
        Map<String, InheritanceList> ro = configuration.getInheritanceEngine().getResolvedOverrides();
        Object nodes = getNamedConfiguration(configuration, "A");
        assert getNodeContents(configuration, nodes, "mynode").equals("node X");

        nodes = getNamedConfiguration(configuration, "B");
        assert getNodeContents(configuration, nodes, "mynode").equals("node C");

    }

    @Test
    public void testAliasOverrideAttribute() throws Exception {
        verbose("testAliasOverrideAttribute:");
        String fileName = path + "alias-override-test.xml";
        MultiConfigurationsInterface configuration = getConfiguration(fileName);
        Map<String, InheritanceList> ro = configuration.getInheritanceEngine().getResolvedOverrides();
        Object nodes = getNamedConfiguration(configuration, "A");
        assert getFirstAttribute(configuration, nodes, "myattrib").equals("attribute X");

        nodes = getNamedConfiguration(configuration, "B");
        assert getFirstAttribute(configuration, nodes, "myattrib").equals("attribute C");
    }
}
