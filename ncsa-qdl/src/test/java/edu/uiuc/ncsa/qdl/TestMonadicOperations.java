package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Monad;
import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  7:18 PM
 */
public class TestMonadicOperations extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testPlusPlusPostfix() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = 1L + initialValue;
        st.setLongValue("i", initialValue);
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad(OpEvaluator.PLUS_PLUS_VALUE, var);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(initialValue);
        assert st.resolveValue(variableReference).equals(newValue);
        myMonad.evaluate(state);
        myMonad.evaluate(state);
        assert st.resolveValue(variableReference).equals(newValue+2L);

    }

    @Test
    public void testMinusMinusPostfix() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = initialValue - 1L;
        st.setLongValue("i", initialValue);
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad( OpEvaluator.MINUS_MINUS_VALUE, var);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(initialValue);
        assert st.resolveValue(variableReference).equals(newValue);
    }

    @Test
    public void testPlusPlusPrefix() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = 1L + initialValue;
        st.setLongValue("i", initialValue);
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad(OpEvaluator.PLUS_PLUS_VALUE, var, false);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(newValue);
        assert st.resolveValue(variableReference).equals(newValue);
    }

    @Test
    public void testMinusMinusPrefix() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = initialValue - 1L;
        st.setLongValue("i", initialValue);
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad( OpEvaluator.MINUS_MINUS_VALUE, var, false);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(newValue);
        assert st.resolveValue(variableReference).equals(newValue);
    }

    @Test
    public void testNotPrefix() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();
        Boolean initialValue = Boolean.TRUE;
        ConstantNode constantNode = new ConstantNode(initialValue, Constant.BOOLEAN_TYPE);
        Boolean newValue = !initialValue;
        OpEvaluator opEvaluator = new OpEvaluator();
        Monad myMonad = new Monad( OpEvaluator.NOT_VALUE, constantNode);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(newValue);
    }

    @Test
     public void testMinusPrefix() throws Exception {
        // Tests that the opposite of a value is returned.
         String variableReference = "i"; // name of the variable
         Long initialValue = 25L;
         Long newValue = -initialValue;
         ConstantNode constantNode = new ConstantNode(initialValue, Constant.LONG_TYPE);
        State state = testUtils.getNewState();
         Monad myMonad = new Monad( OpEvaluator.MINUS_VALUE, constantNode, false);
         myMonad.evaluate(state);
         assert myMonad.getResult().equals(newValue);
     }

}
