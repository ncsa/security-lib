package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.state.*;
import edu.uiuc.ncsa.qdl.statements.FunctionTable;

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

    NamespaceResolver namespaceResolver = NamespaceResolver.getResolver();

    public State getNewState() {
        SymbolTableImpl st = new SymbolTableImpl(namespaceResolver);
        SymbolStack stack = new SymbolStack(namespaceResolver);
        stack.addParent(st);
        State state = new State(namespaceResolver,
                stack,
                new OpEvaluator(),
                MetaEvaluator.getInstance(),
                new FunctionTable(),
                new ModuleMap());
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
                   new FunctionTable(),
                   new ModuleMap());
           return state;
       }
    public OpEvaluator getOpEvaluator() {
        return new OpEvaluator();
    }

    public MetaEvaluator getMetaEvaluator() {
        return MetaEvaluator.getInstance();
    }

    public SymbolTable getSymbolTable() {
        return new SymbolTableImpl(namespaceResolver);
    }

    /**
     * This is a test symbol table that is pre-populated with values called string, long, boolean, stem.var
     * and random.0 random.1 and random.2. It is used for testing and every call will return a new table (!),
     * so stash it if you need to reuse it. The intent is that this has a know state at the beginning of a test.
     *
     * @return
     */
    public SymbolStack getTestSymbolStack() {
        SymbolTableImpl st = new SymbolTableImpl(namespaceResolver);
        st.setRawValue("string", "'a string'");
        st.setRawValue("long", "2468");
        st.setRawValue("boolean", "true");
        st.setRawValue("stem.var", "'a stem variable'");
        // these are to be strings, hence need single quotes
        st.setRawValue("random.0", "'" + TestBase.getRandomString() + "'");
        st.setRawValue("random.1", "'" + TestBase.getRandomString() + "'");
        st.setRawValue("random.2", "'" + TestBase.getRandomString() + "'");
        SymbolStack stack = new SymbolStack(namespaceResolver);
        stack.addParent(st);
        return stack;
    }
}
