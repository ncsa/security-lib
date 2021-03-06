package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.functions.FTStack;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.state.ImportManager;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;

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

    ImportManager namespaceResolver = ImportManager.getResolver();

    public State getNewState() {
        SymbolTableImpl st = new SymbolTableImpl();
        SymbolStack stack = new SymbolStack();
        stack.addParent(st);
        State state = new State(new ImportManager(),
                stack,
                new OpEvaluator(),
                MetaEvaluator.getInstance(),
                new FTStack(),
                new ModuleMap(),
                null,
                false,
                true);
        return state;
    }

    /**
     * Send along the state but with the pre-populated symbol stack.
     * @return
     */
    public State getTestState() {

           State state = new State(namespaceResolver,
                   getTestSymbolStack(),
                   new OpEvaluator(),
                   MetaEvaluator.getInstance(),
                   new FTStack(),
                   new ModuleMap(),
                   null,
                   false,
                   true);
           return state;
       }


    /**
     * This is a test symbol table that is pre-populated with values called string, long, boolean, stem.var
     * and random.0 random.1 and random.2. It is used for testing and every call will return a new table (!),
     * so stash it if you need to reuse it. The intent is that this has a know state at the beginning of a test.
     *
     * @return
     */
    public SymbolStack getTestSymbolStack() {
        SymbolTableImpl st = new SymbolTableImpl();
        st.setRawValue("string", "'a string'");
        st.setRawValue("long", "2468");
        st.setRawValue("boolean", "true");
        st.setRawValue("stem.var", "'a stem variable'");
        // these are to be strings, hence need single quotes
        st.setRawValue("random.0", "'" + AbstractQDLTester.getRandomString() + "'");
        st.setRawValue("random.1", "'" + AbstractQDLTester.getRandomString() + "'");
        st.setRawValue("random.2", "'" + AbstractQDLTester.getRandomString() + "'");
        SymbolStack stack = new SymbolStack();
        stack.addParent(st);
        return stack;
    }
}
