package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.cf.CFBundle;
import edu.uiuc.ncsa.security.core.cf.CFLoader;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.cf.CFXMLConfigurations;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.inheritance.CyclicalError;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import junit.framework.TestCase;

import java.io.FileInputStream;
import java.util.List;

/**
 * Tests for the configuration framework. This is tested both for Apache and the new {@link CFLoader} implementation.
 */

public class CFConfigurationTest extends TestCase {

    protected String path = DebugUtil.getDevPath() + "/security-lib/core/src/test/resources/cfg_tests/";

    public void testAlias() throws Exception {
        FileInputStream fis = getFileInputStream("alias/cfg-alias.xml");
        CFLoader config = new CFLoader();
        CFBundle bundle = config.loadBundle(fis, "service");
        CFNode node = bundle.getNamedConfig("A");
        //Node testedNode = CFXMLConfigurations.getConfig(doc, "service", "A");
        //assert testedNode.getTextContent().equals("foo");
        assert node.getNodeContents().equals("foo");
    }

    public void testOA4MPCfg() throws Exception {
        FileInputStream fis = new FileInputStream("/home/ncsa/dev/csd/config/servers.xml");
        CFLoader config = new CFLoader();
        CFBundle bundle = config.loadBundle(fis, "service");
        CFNode node = bundle.getNamedConfig("localhost:oa4mp.oa2.mariadb");
        //Node testedNode = CFXMLConfigurations.getConfig(doc, "service", "A");
        //assert testedNode.getTextContent().equals("foo");
        assert node.getFirstAttribute("version").equals("1.0");
    }
    /**
     * Tests that a cycle in an alias is detected and an exception is thrown.
     *
     * @throws Exception
     */
    public void testCyclicalAlias() throws Exception {
        FileInputStream fis = getFileInputStream("alias/cfg-cyclic-alias.xml");
        try {
             CFXMLConfigurations.getConfig(CFXMLConfigurations.loadDocument(fis), "service", "A");
            assert false : "circular reference in alias not detected";
        } catch (CyclicalError crx) {
            assert true;
        }
    }
// /home/ncsa/dev/ncsa-git/security-lib/core/src/test/resources/cfg_tests/env-file-test.xml

    /**
     * Tests that including an environment file works. The basic document is not valid XML and has
     * variables for its tags as well.
     *
     * @throws Exception
     */
    public void testIncludeEnv() throws Exception {
        FileInputStream fis = getFileInputStream("env/env-file-test.xml");
        CFLoader config = new CFLoader();
        CFBundle cfg = config.loadBundle(fis, "service");
        CFNode root = new CFNode(cfg.getRootNode());
        List<CFNode> serviceNodes = cfg.getAllNodes("service");
        assert serviceNodes.size() == 2 : "wrong number of service nodes";
        CFNode fooNode = cfg.getNamedConfig("foo");
        assert fooNode.getName().equals("service");
        System.out.println(fooNode.getNodeContents());
        assert fooNode.getNodeContents().equals("service foo");

        CFNode barNode = cfg.getNamedConfig("bar");
        System.out.println("bar content=" + barNode.getNodeContents());
        // This next lien checks that an empty template is simply removed, as per contract
        assert StringUtils.isTrivial(barNode.getNodeContents()) : "bar content not trivial";
        CFNode sshNode = barNode.getFirstNode("ssh");
        assert sshNode.getFirstAttribute("a").equals("b");
        assert sshNode.getFirstAttribute("c").equals("d");
        // next just shows API can drill down to a specific node
        assert barNode.getFirstNode("JSONWebkey").getFirstNode("entry").getNodeContents().equals("JSON bar");

    }

    protected FileInputStream getFileInputStream(String fileName) throws Exception {
        return new FileInputStream(path + fileName);
    }

    /**
     * Tests the files that include files are completely loaded. Three different configurations are in
     * three different files. Test all are present.
     * @throws Exception
     */
    public void testFileIncludes() throws Exception {
        CFBundle cfg  = new CFLoader.Builder()
                .tagname("include_test")
                .inputStream(getFileInputStream("file/include-test-A.xml"))
                .build()
                .loadBundle();

        CFNode testA = cfg.getNamedConfig( "A");
        assert testA.getNodeContents().equals("included A");

        CFNode testB = cfg.getNamedConfig( "B");
        assert testB.getNodeContents().equals("included B");

        CFNode testC = cfg.getNamedConfig( "C");
        assert testC.getNodeContents().equals("included C");
    }

    public void testBadFileInclude() throws Exception {
        boolean goodTest = false;
        try {
            CFBundle cfg = new CFLoader.Builder()
                    .tagname("bad-include")
                    .inputStream(getFileInputStream("file/bad-include.xml"))
                    .build()
                    .loadBundle();
        }catch(MyConfigurationException e) {
            goodTest = true;
        }
assert goodTest : "bad include not detected";
    }


    /**
     * tests that loading a file that tries to include it (i.e., circular reference) failes
     * @throws Exception
     */
    public void testFileIncludesCyclicalError() throws Exception {
        FileInputStream fis = getFileInputStream("file/include-cycle-test-A.xml");
        try {
            CFLoader loader = new CFLoader();
            loader.loadBundle(fis, "include-test");
            //CFXMLConfigurations.getConfiguration(fis);
            assert false : "circular reference in files not detected";
        } catch (CyclicalError crx) {
            assert true;
        }
    }

    /**
     * A common pattern is to have a driver that has only aliases and includes. This
     * tests such a configuration.
     * @throws Exception
     */
    public void testDriver() throws Exception {
        FileInputStream fis = getFileInputStream("file/driver.xml");
        CFLoader config = new CFLoader();
        CFBundle cfg = config.loadBundle(fis, "include_test");

        CFNode testA = cfg.getNamedConfig( "default");
        assert testA.getNodeContents().equals("include_A");

        CFNode testB = cfg.getNamedConfig( "default2");
        assert testB.getNodeContents().equals("include_B");

        CFNode testC = cfg.getNamedConfig( "default3");
        assert testC.getNodeContents().equals("include_C");

        CFNode testC2 = cfg.getNamedConfig( "default4");
        assert testC.getNodeContents().equals("include_C");

    }

    /**
     * Most basic alias test. Get the configuration from an alias. Also tests loading as a resource.
     * @throws Exception
     */
    public void testAlias2() throws Exception {
        CFLoader cfLoader = new CFLoader();
        CFBundle bundle = cfLoader.loadBundle("cfg_tests/alias/cfg-alias.xml", "service");
        CFNode cfNode = bundle.getNamedConfig("D");
        assert cfNode != null : "could not find alias node D";
    }

    /**
     * Most basic failure mode test. Try to get an alias that does not exist. Also tests loading as a resource.
     * @throws Exception
     */
    public void testBadAlias() throws Exception {
        CFLoader cfLoader = new CFLoader();
        CFBundle bundle = cfLoader.loadBundle("cfg_tests/alias/cfg-alias.xml", "service");
        boolean bad = true;
        try {
            bundle.getNamedConfig("Q");
        }catch(MyConfigurationException myc){
            bad = false;
        }
        assert !bad : "found non-existent alias node Q";
    }

    /**
     * tests case that the aliases form a cycle. This should raise an exception. Also tests loading as a resource.
     * @throws Exception
     */
    public void testCyclicAlias() throws Exception {
        CFLoader cfLoader = new CFLoader();
        CFBundle bundle = cfLoader.loadBundle("cfg_tests/alias/cfg-cyclic-alias.xml", "service");
        boolean bad = true;
        try {
            bundle.getNamedConfig("B");
        }catch(CyclicalError myc){
            bad = false;
        }
        assert !bad : "alias cycle not detected";
    }

    /**
     * Tests that gettig a configuration then iterating over its children works
     * @throws Exception
     */
    public void testChildren() throws Exception {
        FileInputStream fis = getFileInputStream("cfg-children.xml");
        CFLoader config = new CFLoader();
        String[] contents = new String[]{"p_content", "q_content", "r_content", "s_content"};
        CFBundle bundle = config.loadBundle(fis, "service");
        CFNode node = bundle.getNamedConfig("A");
        List<CFNode> kidList = node.getChildren("kids");
        List<CFNode> kids = kidList.get(0).getChildren("kid");
        int i = 0;
        for (CFNode kid : kids) {
            assert kid.getNodeContents().equals(contents[i++]); // checks nodes in order
        }
        // Check that we can read stuff nested in a complex structure
        CFNode test = node.getFirstChild("nested")
                .getFirstChild("elements")
                .getFirstChild("element")
                .getFirstChild("test");
        // Show reading attribute wotks
        assert test.getFirstAttribute("name").equals("nested");
        assert test.getFirstBooleanValue("boolean");
        // Show reading CData section works with weird/illegal characters.
        assert test.getFirstNode("path").getNodeContents().equals("/φΧχΨψΩω/⁺→⇒∅/<  >/∧∨≈≔≕≠");
    }
}
