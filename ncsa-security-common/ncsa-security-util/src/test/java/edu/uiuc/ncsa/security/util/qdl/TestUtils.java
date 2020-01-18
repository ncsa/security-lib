package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.qdl.expressions.FunctionEvaluator;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTableImpl;

import static edu.uiuc.ncsa.security.util.TestBase.getRandomString;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  1:12 PM
 */
public class TestUtils {
    static TestUtils _instance;

    static public TestUtils newInstance() {
        if (_instance == null) {
            _instance = new TestUtils();
        }
        return _instance;
    }

    public OpEvaluator getOpEvaluator() {
        return new OpEvaluator();
    }

    public FunctionEvaluator getFunctionEvaluator() {
        return new FunctionEvaluator();
    }

    public SymbolTable getSymbolTable() {
        return new SymbolTableImpl();
    }

    /**
     * This is a test symbol table that is pre-populated with values called string, long, boolean, stem.var
     * and random.0 random.1 and random.2. It is used for testing and every call will return a new table (!),
     * so stash it if you need to reuse it. The intent is that this has a know state at the beginning of a test.
     *
     * @return
     */
    public SymbolTable getTestSymbolTable() {
        SymbolTable st = new SymbolTableImpl();
        st.setRawValue("string", "'a string'");
        st.setRawValue("long", "2468");
        st.setRawValue("boolean", "true");
        st.setRawValue("stem.var", "'a stem variable'");
        // these are to be strings, hence need single quotes
        st.setRawValue("random.0", "'" + getRandomString() + "'");
        st.setRawValue("random.1", "'" + getRandomString() + "'");
        st.setRawValue("random.2", "'" + getRandomString() + "'");
        return st;
    }
}
