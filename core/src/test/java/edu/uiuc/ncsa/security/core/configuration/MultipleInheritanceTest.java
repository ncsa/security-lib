package edu.uiuc.ncsa.security.core.configuration;

import edu.uiuc.ncsa.security.core.inheritance.InheritanceList;
import edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine;
import edu.uiuc.ncsa.security.core.inheritance.UnresolvedReferenceException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;


/**
 * This uses configuration files to test this, since that is the easiest way to get these plus
 * it gives yet other tests for configurations.
 * <p>
 * Also, if you need to, {@link edu.uiuc.ncsa.security.core.inheritance.MultipleInheritanceEngine} DEBUG_ON
 * can be enabled manually for deep debugging, which will print a report after resolution.
 * <p>Created by Jeff Gaynor<br>
 * on 1/19/12 at  10:49 AM
 */
/*
  Running this -- it is run in OA4MP and QDL in their builds as a regression test too.
 */
public class MultipleInheritanceTest extends AbstractInheritanceTest {
    protected String path = DebugUtil.getDevPath() + "/security-lib/core/src/test/resources/cfg_loader/";


    void verbose(String x) {
        if (MultipleInheritanceEngine.DEBUG_ON) {
            System.out.println(x);
        }
    }

    /**
     * The huge and messy test for this that shows how everything works. This is probably the
     * first thing to break if the code is changed since there are a ton of resolutions
     * as well as aliases in strange places and such. The aim is to show that the system can handle more
     * than minimalist examples and keep its state straight.
     *
     * @throws Exception
     */
    @Test
    public void testBig() throws Exception {
        String fileName = path + "test-big-cfg.xml";
        verbose("testBig");
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        //   resolvedOverrides:{A=[T, U, R, W, Q, P],   D=[T, U, R, W, Q, P], E=[T, U, R, W, Q, P],
        //   ,
        //     X=[T, U, R, W, Q, P], }
        assert ro.size() == 18 : "Expected 17 resolved elements, got " + ro.size();
        // bunch of simple ones all of these have a single value, P
        String[] listOfNames = new String[]{"L", "M", "N", "O", "P"};
        String[] iList = new String[]{"P"};
        for (String v : listOfNames) {
            testInheritanceList(ro, v, iList);
        }
        testInheritanceList(ro, "Q", new String[]{"Q"});
        testInheritanceList(ro, "W", new String[]{"W"});
        testInheritanceList(ro, "T", new String[]{"T"});
        testInheritanceList(ro, "Z", new String[]{"Z"});
        testInheritanceList(ro, "B", new String[]{"W", "Q"});
        testInheritanceList(ro, "Y", new String[]{"W", "Q"});
        testInheritanceList(ro, "C", new String[]{"W", "Z", "T"});
        testInheritanceList(ro, "R", new String[]{"R", "W", "Q"});
        testInheritanceList(ro, "U", new String[]{"U", "R", "W", "Q"});
        listOfNames = new String[]{"A", "D", "E", "X"};
        iList = new String[]{"T", "U", "R", "W", "Q", "P"};
        for (String v : listOfNames) {
            testInheritanceList(ro, v, iList);
        }
        assert configurations2.getNamedConfig("A").size() == 6;
    }

    @Test
    public void testCylces() throws Exception {
        verbose("testCycles:");

        String fileName = path + "cyclic-test.xml";
        try {
            MultiConfigurations configurations2 = getConfigurations2(fileName);
            Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
            assert false;
        } catch (UnresolvedReferenceException t) {
            assert true;
        }
    }

    public void testBasicInclude() throws Exception {
        if(System.getProperty("basic.include.test") == null || !System.getProperty("basic.include.test").equals("true")){
            return;
        }
        verbose("test basic includes:");

        String fileName = path + "basic-include-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.size() == 4 : "Expected 4 resolved elements, got " + ro.size();
        assert ro.containsKey("A") : "Missing A element";
        assert ro.containsKey("B") : "Missing B element";
        List<String> elements = ro.get("A").getElements();

        assert elements.size() == 2;
        assert elements.get(0).equals("A");
        assert elements.get(1).equals("X");

        elements = ro.get("B").getElements();
        assert elements.size() == 2;
        assert elements.get(0).equals("B");
        assert elements.get(1).equals("Y");

    }
    protected void checkOverridesInAlias(MultiConfigurations configurations2, int size) throws Exception{
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();

        //   resolvedOverrides:{A=[A, C, X], B=[C, X], C=[C, X], X=[X]}
        assert ro.size() == size : "Expected 4 resolved elements, got " + ro.size();
        assert ro.containsKey("A") : "Missing A element";
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
    @Test
    public void testOverrideInAlias() throws Exception {
        verbose("testOverrideInAlias:");
        String fileName = path + "override-in-alias-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        checkOverridesInAlias(configurations2, 4); // should have 4 elements at the top.
    }

    /**
     * This test includes a file and extends one of the configurations in that file.
     * It includes the override in alias test, so that is rechecked here to make
     * sure the include works.
     * @throws Exception
     */
    @Test
    public void testSingleInclude() throws Exception {
        if(System.getProperty("basic.include.test") == null || !System.getProperty("basic.include.test").equals("true")){
             return;
         }
        verbose("testSingleInclude:");
        String fileName = path + "single-include-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        checkOverridesInAlias(configurations2, 5);  // regression, make sure that all imported, 5 elements at top
        // check that the extension worked
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.containsKey("Q") : "Missing Q element";
        List<String> elements = ro.get("Q").getElements();

        assert elements.size() == 2;
        assert elements.get(0).equals("Q");
        assert elements.get(1).equals("X");
    }

    /**
     * Most minimal possible configuration with a single inheritance.
     *
     * @throws Exception
     */
    @Test

    public void testMinimum() throws Exception {
        verbose("testMinimum:");
        String fileName = path + "min-test.xml";
        String cfgName = "A";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
        assert ro.containsKey("A");
        List<String> elements = ro.get("A").getElements();

        assert elements.size() == 2;
        assert elements.get(0).equals("A");
        assert elements.get(1).equals("X");
    }

    @Test
    public void testAliasNoInherit() throws Exception {
        verbose("testAliasNoInherit:");

        String fileName = path + "alias-test-no-inherit.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
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

    /**
     * @param ro
     * @param v      the entry whose list will be tested
     * @param values The values in the inheritance list.
     */
    protected void testInheritanceList(Map<String, InheritanceList> ro, String v, String[] values) {
        assert ro.containsKey(v) : "Key \"" + v + "\" missing";
        List<String> elements = ro.get(v).getElements();
        assert elements.size() == values.length : "Wrong number of elements in resolution. Expected " + values.length + " and got " + elements.size();
        for (int i = 0; i < values.length; i++) {
            assert elements.get(i).equals(values[i]) : "Wrong resolved value. Expected " + values[i] + " and got " + elements.get(0);
        }
    }

    @Test
    public void testMinAlias() throws Exception {
        String fileName = path + "min-alias-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
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


    @Test
    public void testMultipleOverrides() throws Exception {
        String fileName = path + "multiple-overrides-test.xml";
        MultiConfigurations configurations2 = getConfigurations2(fileName);
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
        MultiConfigurations configurations2 = getConfigurations2(fileName);
        Map<String, InheritanceList> ro = configurations2.getInheritanceEngine().getResolvedOverrides();
// resolvedOverrides:{A=[A, C], B=[B, C], C=[C], D=[D, A, B, C], E=[E, B, A, C]}
        assert ro.size() == 5;
        testInheritanceList(ro, "A", new String[]{"A", "C"});
        testInheritanceList(ro, "B", new String[]{"B", "C"});
        testInheritanceList(ro, "C", new String[]{"C"});
        testInheritanceList(ro, "D", new String[]{"D", "A", "B", "C"});
        testInheritanceList(ro, "E", new String[]{"E", "B", "A", "C"});
    }
}
