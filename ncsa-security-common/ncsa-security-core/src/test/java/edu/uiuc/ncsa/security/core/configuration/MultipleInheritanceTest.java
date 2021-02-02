package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import junit.framework.TestCase;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * This uses configuration files to test this, since that is the easiest way to get these plus
 * it gives yet other tests for configurations.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  10:49 AM
 */
public class MultipleInheritanceTest extends TestCase {


    String path = "/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-core/src/test/resources/cfg_loader/";

    @Test
      public void testCylces() throws Exception {
        String fileName = path + "cyclic-test.xml";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
    }
    @Test
    public void testOverrideInAlias() throws Exception {
        String fileName = path + "override-in-alias-test.xml";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();

        //   resolvedOverrides:{A=[A, C, X], B=[C, X], C=[C, X], X=[X]}
        assert ro.size() == 4;
        assert ro.containsKey("A");
        List<String> elements = ro.get("A").getElements();

        assert elements.size() == 3;
        assert elements.get(0).equals("A");
        assert elements.get(1).equals("C");
        assert elements.get(2).equals("X");

        assert ro.containsKey("B");
        elements = ro.get("B").getElements();

        assert elements.size() == 2;
        assert elements.get(0).equals("C");
        assert elements.get(1).equals("X");

        assert ro.containsKey("C");
        elements = ro.get("C").getElements();

        assert elements.size() == 2;
        assert elements.get(0).equals("C");
        assert elements.get(1).equals("X");

        assert ro.containsKey("X");
        elements = ro.get("X").getElements();

        assert elements.size() == 1;
        assert elements.get(0).equals("X");

    }

    /**
     * Most minimal possible configuration with a single inheritance.
     *
     * @throws Exception
     */
    @Test
    public void testMinimum() throws Exception {
        String fileName = path + "min-test.xml";
        String cfgName = "A";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.containsKey("A");
        List<String> elements = ro.get("A").getElements();

        assert elements.size() == 2;
        assert elements.get(0).equals("A");
        assert elements.get(1).equals("X");
    }

    @Test
    public void testAliasNoInherit() throws Exception {
        String fileName = path + "alias-test-no-inherit.xml";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.size() == 5 : "expected 5 elements and got " + ro.size();
        String[] values = new String[]{"A", "B", "C", "X", "Y"};
        for (String v : values) {
            assert ro.containsKey(v) : "Key \"" + v + "\" missing";
            List<String> elements = ro.get(v).getElements();
            assert elements.size() == 1 : "Wrong number of elements in resolution. Expected 1 and got " + elements.size();
            assert elements.get(0).equals("Y") : "Wrong resolved value. Expected Y and got " + elements.get(0);
        }

    }

    @Test
    public void testMinAlias() throws Exception {
        String fileName = path + "min-alias-test.xml";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.size() == 3;

        assert ro.containsKey("A");
        List<String> elements = ro.get("A").getElements();
        assert elements.size() == 2;
        assert elements.get(0).equals("X");
        assert elements.get(1).equals("B");

        assert ro.containsKey("B");
        assert ro.get("B").getElements().size() == 1;
        assert ro.get("B").getElements().get(0).equals("B");

        assert ro.containsKey("X");
        assert ro.get("X").getElements().size() == 1;
        assert ro.get("X").getElements().get(0).equals("X");

    }

    protected Configurations2 getConfigurations2(String fileName) throws ConfigurationException {
        XMLConfiguration xmlConfiguration = Configurations.getConfiguration(new File(fileName));
        Configurations2 configurations2 = new Configurations2();
        configurations2.ingestConfig(xmlConfiguration, "service");
        return configurations2;
    }

    @Test
    public void testMultipleOverrides() throws Exception {
        String fileName = path + "multiple-overrides-test.xml";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.size() == 8;
        String[] values = new String[]{"P", "Q", "X", "Y", "Z"};
        List<String> elements;
        for (String v : values) {
            assert ro.containsKey(v) : "Key \"" + v + "\" missing";
            elements = ro.get(v).getElements();
            assert elements.size() == 1 : "Wrong number of elements in resolution. Expected 1 and got " + elements.size();
            assert elements.get(0).equals(v) : "Wrong resolved value. Expected \"" + v + "\" and got " + elements.get(0);
        }
        assert ro.containsKey("C");
        elements = ro.get("C").getElements();
        assert elements.size() == 2;
        assert elements.get(0).equals("C");
        assert elements.get(1).equals("Z");

        assert ro.containsKey("B");
        elements = ro.get("B").getElements();
        assert elements.size() == 4;
        assert elements.get(0).equals("P");
        assert elements.get(1).equals("Q");
        assert elements.get(2).equals("C");
        assert elements.get(3).equals("Z");

        assert ro.containsKey("A");
        elements = ro.get("A").getElements();
        assert elements.size() == 6;
        assert elements.get(0).equals("X");
        assert elements.get(1).equals("Y");
        assert elements.get(2).equals("P");
        assert elements.get(3).equals("Q");
        assert elements.get(4).equals("C");
        assert elements.get(5).equals("Z");

    }

    /**
     * Tests diamond inheritance pattern.
     *
     * @throws Exception
     */
    @Test
    public void testDiamond() throws Exception {
        String fileName = path + "diamond-test.xml";
        Configurations2 configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
// resolvedOverrides:{A=[A, C], B=[B, C], C=[C], D=[D, A, B, C], E=[E, B, A, C]}
        assert ro.size() == 5;
        assert ro.containsKey("A");
        List<String> elements = ro.get("A").getElements();
        assert elements.size() == 2;
        assert elements.get(0).equals("A");
        assert elements.get(1).equals("C");

        assert ro.containsKey("B");
        elements = ro.get("B").getElements();
        assert elements.size() == 2;
        assert elements.get(0).equals("B");
        assert elements.get(1).equals("C");

        assert ro.containsKey("C");
        elements = ro.get("C").getElements();
        assert elements.size() == 1;
        assert elements.get(0).equals("C");

        assert ro.containsKey("D");
        elements = ro.get("D").getElements();
        assert elements.size() == 4;
        assert elements.get(0).equals("D");
        assert elements.get(1).equals("A");
        assert elements.get(2).equals("B");
        assert elements.get(3).equals("C");

        assert ro.containsKey("E");
        elements = ro.get("E").getElements();
        assert elements.size() == 4;
        assert elements.get(0).equals("E");
        assert elements.get(1).equals("B");
        assert elements.get(2).equals("A");
        assert elements.get(3).equals("C");

    }
}
