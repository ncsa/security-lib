package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MetaEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.functions.FStack;
import edu.uiuc.ncsa.qdl.functions.FTable;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.module.MAliases;
import edu.uiuc.ncsa.qdl.module.MIStack;
import edu.uiuc.ncsa.qdl.module.MTStack;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolStack;
import edu.uiuc.ncsa.qdl.state.SymbolTableImpl;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  1:12 PM
 */
public class TestUtils {

    public static void set_instance(TestUtils _instance) {
        TestUtils._instance = _instance;
    }

    static TestUtils _instance;

    static public TestUtils newInstance() {
        if (_instance == null) {
            _instance = new TestUtils();
        }
        return _instance;
    }

    MAliases mAliases = MAliases.newMInstances();

    public State createStateObject(
                                   SymbolStack symbolStack,
                                   OpEvaluator opEvaluator,
                                   MetaEvaluator metaEvaluator,
                                   FStack<? extends FTable<? extends XKey, ? extends FunctionRecord>> ftStack,
                                   MTStack mTemplates,
                                   MIStack mInstances,
                                   MyLoggingFacade myLoggingFacade,
                                   boolean isServerMode,
                                   boolean assertionsOn) {
        return new State(
                symbolStack,
                new OpEvaluator(),
                MetaEvaluator.getInstance(),
                new FStack(),
                new MTStack(),
                new MIStack(),
                null,
                false,
                false,
                true);
    }

    public State getNewState() {
        SymbolTableImpl st = new SymbolTableImpl();
        SymbolStack stack = new SymbolStack();
        stack.addParent(st);
        State state = createStateObject(
                stack,
                new OpEvaluator(),
                MetaEvaluator.getInstance(),
                new FStack(),
                new MTStack(),
                new MIStack(),
                null,
                false,
                true);
        return state;
    }

    /**
     * Send along the state but with the pre-populated symbol stack.
     *
     * @return
     */
    public State getTestState() {

        State state = createStateObject(
                getTestSymbolStack(),
                new OpEvaluator(),
                MetaEvaluator.getInstance(),
                new FStack(),
                new MTStack(),
                new MIStack(),
                null,
                false,
                true);
        return state;
    }


    /**
     * This is a test symbol table that is pre-populated with values called string, long, boolean, stem.var
     * and random.0 random.1 and random.2. It is used for testing and every call will return a new table (!),
     * so stash it if you need to reuse it. The intent is that this has a known state at the beginning of a test.
     *
     * @return
     */
    public SymbolStack getTestSymbolStack() {
        SymbolTableImpl st = new SymbolTableImpl();
        st.setValue("string", "a string");
        st.setValue("long", 2468L);
        st.setValue("boolean", Boolean.TRUE);
        st.setValue("random.0", AbstractQDLTester.getRandomString());
        st.setValue("random.1", AbstractQDLTester.getRandomString());
        st.setValue("random.2", AbstractQDLTester.getRandomString());
        SymbolStack stack = new SymbolStack();
        stack.addParent(st);
        return stack;
    }
}
