package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLParserDriver;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import org.junit.Test;

/**
 * Test that directly test the functioning of variables and state. These typically create and manipulate stacks
 * then check values. 
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  2:43 PM
 */
public class QDLVariableTest extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();
    QDLParserDriver runner;

    /**
     * test that the basic value for basic types get stored right.
     *
     * @throws Exception
     */
    @Test
    public void testVariables() throws Exception {
        SymbolTable st = new SymbolTableImpl();
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
     * Test that stem variables get resolved right, so foo.i resolves to foo.0 if i:=0.
     *
     * @throws Exception
     */
    @Test
    public void testStemVariables() throws Exception {
        State state = testUtils.getNewState();
        state.setValue("i", 0L);
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "foo.i";
        String value = "abc";
        state.setValue(stem, value);
        assert state.isDefined("foo.i");
        assert state.isDefined("foo.0");
        assert !state.isDefined("foo.1"); // just in case
        assert state.getValue("foo.0").equals(value);
        assert state.getValue("foo.i").equals(value);
    }

    @Test
    public void testRemove() throws Exception {
        State state = testUtils.getNewState();

        state.setValue("i", 0L);
        state.setValue("j", 1L);
        state.setValue("k", -1L); // relative index
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "foo.i";
        String value = "abc";
        state.setValue("foo.i", value);
        state.setValue("foo.j", value);


        assert state.isDefined(stem);
        state.remove(stem);
        assert !state.isDefined(stem);
        state.setValue("foo.i", value);
        state.remove("foo.k"); // removes last one
        assert !state.isDefined("foo.j");

        state.remove("i");
        assert !state.isDefined("i");

    }

    /**
     * We allow for multiple stems like a.b.c.d if all the tails resolve. In point of fact
     * we only allow for single stems, so a. exists, but a.b. is disallowed (b. can be a stem, but not an index)
     * This test
     * defines a sequence of stem variables and then tests that they resolve.
     *
     * @throws Exception
     */
    @Test
    public void testDeepResolution() throws Exception {
        State state = testUtils.getNewState();
        state.setValue("z",   1L);
        state.setValue("y.1", 2L);
        state.setValue("x.2", 3L);
        state.setValue("w.3", 4L);
        state.setValue("A.4", 5L);
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "A.w.x.y.z";
        Object output = state.getValue(stem);
        // FYI Every integer in QDL is a long!!! so testing against an int fails.
        assert output.equals(5L) : "expected 5 and got " + state.getValue(stem);
        assert state.isDefined(stem);
    }

    @Test
    public void testDeepResolutionSet() throws Exception {
        State state = testUtils.getNewState();
        state.setValue("z", "1");
        state.setValue("y.1", "2");
        state.setValue("x.2", "3");
        state.setValue("w.3", "4");
        state.setValue("A.4", "5");
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "A.w.x.y.z";
        // so now we set the value.
        state.setValue(stem, 6L);
        Object output = state.getValue(stem);
        // FYI Every integer in QDL is a long!!! so testing against an int fails.
        assert output.equals(6L) : "expected 6 and got " + state.getValue(stem);
        assert state.isDefined(stem);
    }

    /**
     * Acid test for the stack. In this case there is a long stem and each of the variables is
     * placed in a different symbol table, maximizing searching needed.
     *
     * @throws Exception
     */
    @Test
    public void testDeepResolutionOnStack() throws Exception {
        SymbolTableImpl st0 = new SymbolTableImpl();
        SymbolTableImpl st1 = new SymbolTableImpl();
        SymbolTableImpl st2 = new SymbolTableImpl();
        SymbolTableImpl st3 = new SymbolTableImpl();
        SymbolTableImpl st4 = new SymbolTableImpl();
        SymbolStack stack = new SymbolStack();

        st4.setValue("z", 1L);
        stack.addParent(st4);
        st3.setValue("y.1", 2L);
        stack.addParent(st3);
        st2.setValue("x.2", 3L);
        stack.addParent(st2);
        st1.setValue("w.3", "4");
        stack.addParent(st0);
        st0.setValue("A.4", 5L);
        stack.addParent(st1);
        State state = testUtils.getNewState();
        state.setSymbolStack(stack);
        String stem = "A.w.x.y.z";
        Object output = state.getValue(stem);
        assert output.equals(5L) : "expected 5 and got " + stack.resolveValue(stem);
        assert state.isDefined(stem);
    }


    /**
     * This also checks for deep resolution and then it sets the value and then reads it.
     * So each variable is set in a different symbol table (emulating having a complex
     * set of modules and control structures). This test is to show that a single
     * stem that accesses all of them gets resolved right.
     *
     * @throws Exception
     */
    @Test
    public void testDeepResolutionAndSetVariable() throws Exception {
        SymbolTableImpl st0 = new SymbolTableImpl();
        SymbolTableImpl st1 = new SymbolTableImpl();
        SymbolTableImpl st2 = new SymbolTableImpl();
        SymbolTableImpl st3 = new SymbolTableImpl();
        SymbolTableImpl st4 = new SymbolTableImpl();
        SymbolStack stack = new SymbolStack();

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
        assert output.equals("6") : "expected 6 and got " + stack.resolveValue(stem);
        assert stack.isDefined(stem);
    }

}
