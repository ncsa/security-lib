package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.expressions.*;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  8:11 PM
 */
public class ExpressionTest extends AbstractQDLTester {
     
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
     * Test the !(a+2)<(b-3) evaluates then applies monadic ! rather than implicitly
     * regrouping as
     * <pre>
     *   (!(a+2))<(b-3)
     * </pre>
     * and failing.  (Regression test.)
     * @throws Throwable
     */
    public void testExpression1a() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:= 10; b := 4;");
        addLine(script, "ok := !(a+2)<(b-3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    /**
     * Create some nodes, then change the state and test that the cloning works.
     *
     * @throws Exception
     */
     
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
    /**
     * Test that assignments which are now fully treated like any other dyadic operator
     * and can be grouped and pass back their value reliably. Simple regression test.
     *
     * @throws Throwable
     */
    public void testAssignmentExpression() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "d:= (false =: c) || true;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("d", state);
        assert !getBooleanValue("c", state);
    }

    /**
     * Update of 1/6/2021 changed assignments from a statement to an expression. This was long
     * overdue and makes the code vastly easier. This is a set of regression tests to make
     * sure that functionality with stems (which involves looking up information in the
     * stem) works.
     * @throws Throwable
     */
    public void testStemAssignments() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x := 'h.i.j';");
        addLine(script, "x1 := 'h.i.j.';");

        addLine(script, "w.x := 5;");
        addLine(script, "w.x1 := 10;");
        addLine(script, "wx1_ok := w.x1 == w.'h.i.j.';"); // test that keys with .'s can be used
        addLine(script, "wx_not_ok := is_defined(w.h.i.j);"); // false
        addLine(script, "wx_ok := w.x == w.'h.i.j';"); // test that keys with .'s can be used
        addLine(script, "w.h.i.j := 100;");
        addLine(script, "w2_ok := w.h.i.j == 100;");
        addLine(script, "wx2_ok := w.x == (w.).'h.i.j';"); // test that keys with .'s can be used
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert !getBooleanValue("wx_not_ok", state);
        assert getBooleanValue("wx_ok", state);
        assert getBooleanValue("wx1_ok", state);
        assert getBooleanValue("w2_ok", state);
        assert getBooleanValue("wx2_ok", state);

    }
    public void testStemAssignments0() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [1,2];");
        addLine(script, "z. := [1,2];");
        addLine(script, "az_ok :=reduce(@∧, a. == z.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("az_ok", state);
    }
    public void testStemAssignments1() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "b.c := 2;");
        addLine(script, "bc_ok := b.c == 2;"); // precedence test so it's not bc == b.c
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("bc_ok", state);
    }

    public void testStemAssignments2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "d.e.f := 3;");
        addLine(script, "def_ok := d.e.f == 3;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("def_ok", state);
    }

    public void testStemAssignments3() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        addLine(script, "g.h.i.j := 4;");
        addLine(script, "ghij_ok := g.h.i.j == 4;");
        interpreter.execute(script.toString());
        assert getBooleanValue("ghij_ok", state);
    }

    public void testStemAssignments4() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        addLine(script, "b.c := 2;");
        addLine(script, "b.(q:= 'c');");
        addLine(script, "q_ok := q == 'c';");
        interpreter.execute(script.toString());
        assert getBooleanValue("q_ok", state);
    }
    public void testStemAssignments5() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        addLine(script, "x := 'h.i.j';"); // test that stem keys with embedded periods are handled right
        addLine(script, "g.h.i.j := 4;");
        addLine(script, "x_not_ok := is_defined(g.x);"); //false
        interpreter.execute(script.toString());
        assert !getBooleanValue("x_not_ok", state);
    }

}
