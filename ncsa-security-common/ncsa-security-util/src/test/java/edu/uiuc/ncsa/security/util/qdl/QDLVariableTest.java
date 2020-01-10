package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.TestBase;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  2:43 PM
 */
public class QDLVariableTest extends TestBase {
    QDLRunner runner;

    /**
     * test that the basic value for basic types get stored right.
     *
     * @throws Exception
     */
    @Test
    public void testVariables() throws Exception {
        SymbolTable st = new SymbolTable();
        st.setValue("a", "12345");
        assert st.resolveValue("a").equals(new Long(12345));
        st.setValue("b", "true");
        assert st.resolveValue("b") == Boolean.TRUE;
        st.setValue("c", "false");
        assert st.resolveValue("c") == Boolean.FALSE;
        st.setValue("d", "null");
        assert !st.isDefined("d");
        String value = "mairzy((%^998e98nfg98u";
        st.setValue("e", "'" + value + "'"); // how it comes out of the parser
        assert st.resolveValue("e").equals(value);
    }

    /**
     * Test that xtem variables get resolved right, so foo.i resolves to foo.0 if i:=0.
     *
     * @throws Exception
     */
    @Test
    public void testStemVariables() throws Exception {
        SymbolTable st = new SymbolTable();
        st.setValue("i", "0");
        // first test, i = 0, so foo.i should resolve to foo.0
        String stem = "foo.i";
        String value = "abc";
        st.setValue(stem, "'" + value + "'");
        assert st.isDefined("foo.i");
        assert st.isDefined("foo.0");
        assert !st.isDefined("foo.1"); // just in case
        assert st.resolveValue("foo.0").equals(value);
        assert st.resolveValue("foo.i").equals(value);
    }

    @Test
    public void testRemove() throws Exception{
        SymbolTable st = new SymbolTable();
           st.setValue("i", "0");
           // first test, i = 0, so foo.i should resolve to foo.0
           String stem = "foo.i";
           String value = "abc";
           st.setValue(stem, "'" + value + "'");
            assert st.isDefined(stem);
            st.remove(stem);
            assert !st.isDefined(stem);
            st.remove("i");
            assert !st.isDefined("i");
    }
}
