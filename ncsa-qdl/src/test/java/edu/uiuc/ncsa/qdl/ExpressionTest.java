package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  8:11 PM
 */
public class ExpressionTest extends AbstractQDLTester {
    @Test
    public void testExpression1() throws Exception {
        TestUtils testUtils = TestUtils.newInstance();

        // test !(a+2)<(b-3) for a = 10, b = 4. Should be TRUE
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        symbolTable.setLongValue("a", 10L);
        symbolTable.setLongValue("b", 4L);
        ConstantNode twoNode = new ConstantNode(2L, Constant.LONG_TYPE);
        ConstantNode threeNode = new ConstantNode(3L, Constant.LONG_TYPE);
        VariableNode aNode = new VariableNode("a");
        VariableNode bNode = new VariableNode("b");
        // so to make these, ou start at the bottom and assemble as you rise up.
        Dyad aPlus2 = new Dyad(OpEvaluator.PLUS_VALUE, aNode, bNode);
        Dyad bMinus3 = new Dyad(OpEvaluator.MINUS_VALUE, bNode, threeNode);
        Dyad lessThanNode = new Dyad(OpEvaluator.LESS_THAN_VALUE, aPlus2, bMinus3);
        // top node
        Monad notNode = new Monad(OpEvaluator.NOT_VALUE, lessThanNode);
        notNode.evaluate(state);
        assert (Boolean) notNode.getResult();
    }

    /**
     * Create some nodes, then change the state and test that the cloning works.
     *
     * @throws Exception
     */
    @Test
    public void testMakeCopy() throws Exception {
        TestUtils testUtils = TestUtils.newInstance();
        //
        // a := 10;
        // b := 4;
        // !(a+2)<(b-3);
        //
        // Should be TRUE
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        symbolTable.setLongValue("a", 10L);
        symbolTable.setLongValue("b", 4L);
        ConstantNode twoNode = new ConstantNode(2L, Constant.LONG_TYPE);
        ConstantNode threeNode = new ConstantNode(3L, Constant.LONG_TYPE);
        VariableNode aNode = new VariableNode("a");
        VariableNode bNode = new VariableNode("b");
        // so to make these, ou start at the bottom and assemble as you rise up.
        Dyad aPlus2 = new Dyad(OpEvaluator.PLUS_VALUE, aNode, twoNode);
        Dyad bMinus3 = new Dyad(OpEvaluator.MINUS_VALUE, bNode, threeNode);
        Dyad lessThanNode = new Dyad(OpEvaluator.LESS_THAN_VALUE, aPlus2, bMinus3);
        // top node
        Monad notNode = new Monad(OpEvaluator.NOT_VALUE, lessThanNode);
        notNode.evaluate(state);
        assert (Boolean) notNode.getResult();
        // This is the same as the previous test to show that state is kept straight.
        // now redo it. This time a = 0, b = 5 and the value should be false.
        State state2 = testUtils.getNewState();
        state2.getSymbolStack().setLongValue("a", 0L);
        state2.getSymbolStack().setLongValue("b", 10L);

        ExpressionNode notNode2 = notNode.makeCopy();
        notNode2.evaluate(state2);
        assert !(Boolean) notNode2.getResult();

    }

    /**
     * Test to check that inline conditionals of the form
     *
     * <pre>
     *     <i>boolean</i> <b>?</b> <i>expression0</i> <b>:</b> <i>expression1</i>
     *  </pre>
     *
     *  work as advertised. This checks that operations inside the expressions work,
     *  precedence is followed and that parentheses are interpreted correctly.
     *  <p>And FYI</p>
     *  <pre>
     *    π()^exp()
     * 22.4591577183611
     *   exp()^π()
     * 23.1406926327793
     *  </pre>
     * @throws Throwable
     */
    @Test
    public void testInlineConditional() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x := π()^exp() ≤ exp()^π() ? 'left i'+'s bigger' : 'right' + ' is' + ' bigger';");
        addLine(script, "y := 3 < 2 ? 4>3 ?'a':'b':'c';"); // shows that precedence is followed for nesting too.
        addLine(script, "z := (3 < 2) ? ((4 ≥ 3 )?'a':'b'):'c';"); // shows that parentheses are interpreted correctly
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("x", state).equals("left is bigger");
        assert getStringValue("y", state).equals("c");
        assert getStringValue("z", state).equals("c");
    }
}
