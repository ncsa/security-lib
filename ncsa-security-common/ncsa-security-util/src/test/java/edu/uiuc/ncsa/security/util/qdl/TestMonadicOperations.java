package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.TestBase;
import edu.uiuc.ncsa.security.util.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.security.util.qdl.expressions.Monad;
import edu.uiuc.ncsa.security.util.qdl.expressions.OpEvaluator;
import edu.uiuc.ncsa.security.util.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  7:18 PM
 */
public class TestMonadicOperations extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testPlusPlusPostfix() throws Exception {
        SymbolTable st = testUtils.getTestSymbolTable();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = 1L + initialValue;
        st.setLongValue("i", initialValue);
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference, st);
        Monad myMonad = new Monad(opEvaluator, OpEvaluator.PLUS_PLUS_VALUE, var);
        myMonad.evaluate();

        assert myMonad.getResult().equals(initialValue);
        assert st.resolveValue(variableReference).equals(newValue);
        myMonad.evaluate();
        myMonad.evaluate();
        assert st.resolveValue(variableReference).equals(newValue+2L);

    }

    @Test
    public void testMinusMinusPostfix() throws Exception {
        SymbolTable st = testUtils.getTestSymbolTable();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = initialValue - 1L;
        st.setLongValue("i", initialValue);
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference, st);
        Monad myMonad = new Monad(opEvaluator, OpEvaluator.MINUS_MINUS_VALUE, var);
        myMonad.evaluate();

        assert myMonad.getResult().equals(initialValue);
        assert st.resolveValue(variableReference).equals(newValue);
    }

    @Test
    public void testPlusPlusPrefix() throws Exception {
        SymbolTable st = testUtils.getTestSymbolTable();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = 1L + initialValue;
        st.setLongValue("i", initialValue);
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference, st);
        Monad myMonad = new Monad(opEvaluator, OpEvaluator.PLUS_PLUS_VALUE, var, false);
        myMonad.evaluate();

        assert myMonad.getResult().equals(newValue);
        assert st.resolveValue(variableReference).equals(newValue);
    }

    @Test
    public void testMinusMinusPrefix() throws Exception {
        SymbolTable st = testUtils.getTestSymbolTable();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = initialValue - 1L;
        st.setLongValue("i", initialValue);
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference, st);
        Monad myMonad = new Monad(opEvaluator, OpEvaluator.MINUS_MINUS_VALUE, var, false);
        myMonad.evaluate();

        assert myMonad.getResult().equals(newValue);
        assert st.resolveValue(variableReference).equals(newValue);
    }

    @Test
    public void testNotPrefix() throws Exception {
        SymbolTable st = testUtils.getTestSymbolTable();
        Boolean initialValue = Boolean.TRUE;
        ConstantNode constantNode = new ConstantNode(initialValue, Constant.BOOLEAN_TYPE);
        Boolean newValue = !initialValue;
        OpEvaluator opEvaluator = new OpEvaluator();
        Monad myMonad = new Monad(opEvaluator, OpEvaluator.NOT_VALUE, constantNode);
        myMonad.evaluate();

        assert myMonad.getResult().equals(newValue);
    }

    @Test
     public void testMinusPrefix() throws Exception {
        // Tests that the opposite of a value is returned.
         String variableReference = "i"; // name of the variable
         Long initialValue = 25L;
         Long newValue = -initialValue;
         ConstantNode constantNode = new ConstantNode(initialValue, Constant.LONG_TYPE);
         OpEvaluator opEvaluator = new OpEvaluator();
         Monad myMonad = new Monad(opEvaluator, OpEvaluator.MINUS_VALUE, constantNode, false);
         myMonad.evaluate();
         assert myMonad.getResult().equals(newValue);
     }

}
