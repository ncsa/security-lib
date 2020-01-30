package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLParserDriver;
import edu.uiuc.ncsa.qdl.state.NamespaceResolver;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  2:43 PM
 */
public class QDLVariableTest extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();
    QDLParserDriver runner;

    /**
     * test that the basic value for basic types get stored right.
     *
     * @throws Exception
     */
    @Test
    public void testVariables() throws Exception {
        SymbolTable st = new SymbolTableImpl(NamespaceResolver.getResolver());
        st.setRawValue("a", "12345");
        assert st.resolveValue("a").equals(new Long(12345));
        st.setRawValue("b", "true");
        assert st.resolveValue("b") == Boolean.TRUE;
        st.setRawValue("c", "false");
        assert st.resolveValue("c") == Boolean.FALSE;
        st.setRawValue("d", "null");
        assert !st.isDefined("d");
        String value = "mairzy((%^998e98nfg98u";
        st.setRawValue("e", "'" + value + "'"); // how it comes out of the parser
        assert st.resolveValue("e").equals(value);
    }

    /**
     * Test that xtem variables get resolved right, so foo.i resolves to foo.0 if i:=0.
     *
     * @throws Exception
     */
    @Test
    public void testStemVariables() throws Exception {
        SymbolTable st = new SymbolTableImpl(NamespaceResolver.getResolver());
        st.setRawValue("i", "0");
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "foo.i";
        String value = "abc";
        st.setRawValue(stem, "'" + value + "'");
        assert st.isDefined("foo.i");
        assert st.isDefined("foo.0");
        assert !st.isDefined("foo.1"); // just in case
        assert st.resolveValue("foo.0").equals(value);
        assert st.resolveValue("foo.i").equals(value);
    }

    @Test
    public void testRemove() throws Exception {
        SymbolTable st = new SymbolTableImpl(NamespaceResolver.getResolver());
        st.setRawValue("i", "0");
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "foo.i";
        String value = "abc";
        st.setRawValue(stem, "'" + value + "'");
        assert st.isDefined(stem);
        st.remove(stem);
        assert !st.isDefined(stem);
        st.remove("i");
        assert !st.isDefined("i");
    }

    /**
     * We allow for multiple stems like a.b.c.d if all the tails resolve. In point of fact
     * we only allow for single stems, so a. exists, but a.b. is disallowed. This test
     * defines a sequence of stem variables and then tests that they resolve.
     *
     * @throws Exception
     */
    @Test
    public void testDeepResolution() throws Exception {
        SymbolTable st = new SymbolTableImpl(NamespaceResolver.getResolver());
        st.setRawValue("z", "1");
        st.setRawValue("y.1", "2");
        st.setRawValue("x.2", "3");
        st.setRawValue("w.3", "4");
        st.setRawValue("A.4", "5");
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "A.w.x.y.z";
        Object output = st.resolveValue(stem);
        // FYI Every integer in QDL is a long!!! so testing against an int fails.
        assert output.equals(5L) : "expected 5 and got " + st.resolveValue(stem);
        assert st.isDefined(stem);
    }

    @Test
    public void testDeepResolutionSet() throws Exception {
        SymbolTable st = new SymbolTableImpl(NamespaceResolver.getResolver());
        st.setRawValue("z", "1");
        st.setRawValue("y.1", "2");
        st.setRawValue("x.2", "3");
        st.setRawValue("w.3", "4");
        st.setRawValue("A.4", "5");
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "A.w.x.y.z";
        // so now we set the value.
        st.setRawValue(stem, "6");
        Object output = st.resolveValue(stem);
        // FYI Every integer in QDL is a long!!! so testing against an int fails.
        assert output.equals(6L) : "expected 6 and got " + st.resolveValue(stem);
        assert st.isDefined(stem);
    }

    /**
     * Acid test for the stack. In this case there is a long stem and each of the variables is
     * placed in a different symbol table, maximizing searching needed.
     * @throws Exception
     */
    @Test
    public void testDeepResolutionOnStack() throws Exception {
        SymbolTableImpl st0 = new SymbolTableImpl(NamespaceResolver.getResolver());
        SymbolTableImpl st1 = new SymbolTableImpl(NamespaceResolver.getResolver());
        SymbolTableImpl st2 = new SymbolTableImpl(NamespaceResolver.getResolver());
        SymbolTableImpl st3 = new SymbolTableImpl(NamespaceResolver.getResolver());
        SymbolTableImpl st4 = new SymbolTableImpl(NamespaceResolver.getResolver());
        SymbolStack stack = new SymbolStack(NamespaceResolver.getResolver());

        st4.setRawValue("z", "1");
        stack.addParent(st4);
        st3.setRawValue("y.1", "2");
        stack.addParent(st3);
        st2.setRawValue("x.2", "3");
        stack.addParent(st2);
        st1.setRawValue("w.3", "4");
        stack.addParent(st0);
        st0.setRawValue("A.4", "5");
        stack.addParent(st1);
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "A.w.x.y.z";
        // so now we set the value.
       // stack.setRawValue(stem, "6");
        Object output = stack.resolveValue(stem);
        // FYI Every integer in QDL is a long!!! so testing against an int fails.
        assert output.equals(5L) : "expected 5 and got " + stack.resolveValue(stem);
        assert stack.isDefined(stem);
    }


    /**
     * This also checks for deep resolution and then it sets the value and then reads it.
     * @throws Exception
     */
    @Test
      public void testDeepResolutionAndSetVariable() throws Exception {
          SymbolTableImpl st0 = new SymbolTableImpl(NamespaceResolver.getResolver());
          SymbolTableImpl st1 = new SymbolTableImpl(NamespaceResolver.getResolver());
          SymbolTableImpl st2 = new SymbolTableImpl(NamespaceResolver.getResolver());
          SymbolTableImpl st3 = new SymbolTableImpl(NamespaceResolver.getResolver());
          SymbolTableImpl st4 = new SymbolTableImpl(NamespaceResolver.getResolver());
          SymbolStack stack = new SymbolStack(NamespaceResolver.getResolver());

          st4.setRawValue("z", "1");
          stack.addParent(st4);
          st3.setRawValue("y.1", "2");
          stack.addParent(st3);
          st2.setRawValue("x.2", "3");
          stack.addParent(st2);
          st1.setRawValue("w.3", "4");
          stack.addParent(st0);
          st0.setRawValue("A.4", "5");
          stack.addParent(st1);
          // first test, i = 0, so foo.i should resolve to foo.0
          String stem = "A.w.x.y.z";
          // so now we set the value.
          stack.setValue(stem, "6");
          Object output = stack.resolveValue(stem);
          // FYI Every integer in QDL is a long!!! so testing against an int fails.
          assert output.equals("6") : "expected 6 and got " + stack.resolveValue(stem);
          assert stack.isDefined(stem);
      }

}
