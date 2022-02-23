package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Monad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.VStack;
import edu.uiuc.ncsa.qdl.variables.VThing;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  7:18 PM
 */
public class TestMonadicOperations extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

     
    public void testPlusPlusPostfix() throws Exception {
        State state = testUtils.getNewState();
        VStack vStack = state.getVStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = 1L + initialValue;
        vStack.put(new VThing(new XKey("i"), initialValue));
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad(OpEvaluator.PLUS_PLUS_VALUE, var);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(initialValue);
        assert checkVThing(variableReference, newValue, state);

//        assert vStack.get(new XKey(variableReference)).equals(newValue);
        myMonad.evaluate(state);
        myMonad.evaluate(state);
        assert checkVThing(variableReference, newValue+2L, state);
        //assert vStack.get(new XKey(variableReference)).equals(newValue+2L);

    }

     
    public void testMinusMinusPostfix() throws Exception {
        State state = testUtils.getNewState();
        VStack vStack = state.getVStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = initialValue - 1L;
        vStack.put(new VThing(new XKey("i"), initialValue));
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad( OpEvaluator.MINUS_MINUS_VALUE, var);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(initialValue);
        assert checkVThing(variableReference, newValue, state);

//        assert vStack.get(new XKey(variableReference)).equals(newValue);
    }

     
    public void testPlusPlusPrefix() throws Exception {
        State state = testUtils.getNewState();
        VStack vStack = state.getVStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = 1L + initialValue;
        vStack.put(new VThing(new XKey("i"), initialValue));
        OpEvaluator opEvaluator = new OpEvaluator();
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad(OpEvaluator.PLUS_PLUS_VALUE, var, false);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(newValue);
        assert checkVThing(variableReference, newValue, state);
    }

     
    public void testMinusMinusPrefix() throws Exception {
        State state = testUtils.getNewState();
        VStack vStack = state.getVStack();
        String variableReference = "i"; // name of the variable
        Long initialValue = 2L;
        Long newValue = initialValue - 1L;
        vStack.put(new VThing(new XKey("i"), initialValue));
        VariableNode var = new VariableNode(variableReference);
        Monad myMonad = new Monad( OpEvaluator.MINUS_MINUS_VALUE, var, false);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(newValue);
        assert checkVThing(variableReference, newValue, state);
    }


    public void testNotPrefix() throws Exception {
        State state = testUtils.getNewState();
        Boolean initialValue = Boolean.TRUE;
        ConstantNode constantNode = new ConstantNode(initialValue, Constant.BOOLEAN_TYPE);
        Boolean newValue = !initialValue;
        OpEvaluator opEvaluator = new OpEvaluator();
        Monad myMonad = new Monad( OpEvaluator.NOT_VALUE, constantNode);
        myMonad.evaluate(state);

        assert myMonad.getResult().equals(newValue);
    }

     
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
     
    public void testMMAndPPParsing() throws Throwable {
        StringBuffer script = new StringBuffer();
          addLine(script, "j := 5;");
          addLine(script, "a := ++j - j++;");
          addLine(script, "b := --j - j--;");
          addLine(script, "c := --j - --j;");
          addLine(script, "d := ++j - --j;");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a" , state)== 0L;
        assert getLongValue("b" , state)== 0L;
        assert getLongValue("c" , state)== 1L;
        assert getLongValue("d" , state)== 1L;
        assert getLongValue("j" , state)== 3L;


    }
}
