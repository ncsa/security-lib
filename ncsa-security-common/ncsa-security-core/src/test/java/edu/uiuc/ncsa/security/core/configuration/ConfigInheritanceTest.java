package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/3/21 at  11:33 AM
 */
public class ConfigInheritanceTest extends AbstractInheritanceTest {
    protected String path = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-core/src/test/resources/cfg_inheritance/";

    /**
     * Shows that overriding an configuration replaces an attribute
     *
     * @throws Exception
     */
    @Test
    public void testAttributeInheritance() throws Exception {
        String fileName = path + "attrib-test-A.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig("A");
        assert configurations2.getFirstAttribute(nodes, "test_attrib").equals("Attribute A");
    }

    /**
     * Shows that in an override, the system can still find attributes that have not been overridden
     *
     * @throws Exception
     */
    @Test
    public void testAttributeInheritance2() throws Exception {
        String fileName = path + "attrib-test-B.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig("A");
        assert configurations2.getFirstAttribute(nodes, "other_attrib").equals("Attribute X");
    }

    /**
     * Shows that overriding a node will be resolved.
     *
     * @throws Exception
     */
    @Test
    public void testNodeInheritance1() throws Exception {
        String fileName = path + "node-override-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig("A");
        assert configurations2.getNodeContents(nodes, "mynode").equals("node A");
    }

    /**
     * Shows that if the node is not overridden it is still found.
     *
     * @throws Exception
     */
    @Test
    public void testNodeInheritance2() throws Exception {
        String fileName = path + "node-override-test2.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig("A");
        assert configurations2.getNodeContents(nodes, "mynode").equals("node X");
    }

    @Test
    public void testAliasOverrideNode() throws Exception {
        String fileName = path + "alias-override-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig("A");
        assert configurations2.getNodeContents(nodes, "mynode").equals("node X");

        nodes = configurations2.getNamedConfig("B");
        assert configurations2.getNodeContents(nodes, "mynode").equals("node C");

    }

    @Test
    public void testAliasOverrideAttribute() throws Exception {
        String fileName = path + "alias-override-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        List<ConfigurationNode> nodes = configurations2.getNamedConfig("A");
        assert configurations2.getFirstAttribute(nodes, "myattrib").equals("attribute X");

        nodes = configurations2.getNamedConfig("B");
        assert configurations2.getFirstAttribute(nodes, "myattrib").equals("attribute C");
    }
}
