package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.OpEvaluator;
import edu.uiuc.ncsa.qdl.exceptions.AssertionException;
import edu.uiuc.ncsa.qdl.expressions.Assignment;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Dyad;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.QDLConstants;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/14/20 at  10:57 AM
 */
public class StatementTest extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

    /**
     * Basic regression test that the assignment resolves its variable and places the result in to the state
     * as it should.
     *
     * @throws Exception
     */
    @Test
    public void testAssignment() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable st = state.getSymbolStack();

        ConstantNode left = new ConstantNode(1L, Constant.LONG_TYPE);
        ConstantNode right = new ConstantNode(2L, Constant.LONG_TYPE);
        OpEvaluator opEvaluator = new OpEvaluator();

        Dyad dyad = new Dyad(OpEvaluator.PLUS_VALUE, left, right);
        Assignment assignmentNode = new Assignment("A", dyad);
        assignmentNode.evaluate(state);
        assert st.isDefined("A");
        assert st.resolveValue("A").equals(3L);
    }

    /**
     * Test a loop that has a basic conditional statement (with something that needs evaluated)
     *
     * @throws Throwable
     */
    @Test
    public void testBasicLoop() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "i:=0;");
        addLine(script, "j:=1;");
        addLine(script, "while[");
        addLine(script, "   i++ < 5");
        addLine(script, "]do[");
        addLine(script, "  j *= j+i;");
        addLine(script, "]; // end while");
        // Note the conditional for the loop has i = 0,1,2,3,4
        // but inside the loop it has been incremented and is 1,2,3,4,5
        // With the multiplication assignment to j, this results in fast exponential growth.
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("j", state).equals(65585696L) : "Loop did not execute properly.";
    }

    /**
     * Test that a malformed loop (conditional statement is not a conditional) fails reasonably.
     *
     * @throws Throwable
     */
    @Test
    public void testBadLoop() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "i:=0;");
        addLine(script, "while[");
        addLine(script, "   i + 2");
        addLine(script, "]do[");
        addLine(script, "  say(i);");
        addLine(script, "]; // end while");
        // Note the conditional for the loop has i = 0,1,2,3,4
        // but inside the loop it has been incremented and is 1,2,3,4,5
        // With the multiplication assignment to j, this results in fast exponential growth.
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to execute a loop without a valid conditional";
        } catch (IllegalStateException isx) {
            assert true;
        }
    }

    /**
     * Test that assigning a value to a keyword, e.g. false := true fails.
     *
     * @throws Throwable
     */
    @Test
    public void testReservedWordAssignments() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, QDLConstants.RESERVED_TRUE + " := 2;");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Error; Was able to assign " + QDLConstants.RESERVED_TRUE + " a value";
        } catch (IllegalStateException | IllegalArgumentException iax) {
            assert true;
        }

        script = new StringBuffer();
        addLine(script, QDLConstants.RESERVED_FALSE + " := 1;");

        //QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Error; Was able to assign " + QDLConstants.RESERVED_FALSE + " a value";
        } catch (IllegalStateException | IllegalArgumentException iax) {
            assert true;
        }

        script = new StringBuffer();
        addLine(script, QDLConstants.RESERVED_NULL + " := 'foo';");

        // QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Error; Was able to assign " + QDLConstants.RESERVED_NULL + " a value";
        } catch (IllegalArgumentException iax) {
            assert true;
        }

    }

    public void testAssert() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "assert[2<3]['test 1'];");
        addLine(script, "⊨ 2<3 : 'test 1';");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // should work without incident since both assert statements pass
    }

    public void testBadAssert1() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "assert[3<2]['test 1'];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "failed assertion not asserted";
        } catch (AssertionException assertionException) {
            assert assertionException.getMessage().equals("test 1");
        }
    }

    public void testBadAssert2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "⊨ 3<2 : 'test 1';");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "failed assertion not asserted";
        } catch (AssertionException assertionException) {
            assert assertionException.getMessage().equals("test 1");
        }
    }

    /**
     * Simple block test. This resets a global variable, ok, and sets a local
     * variable, a.
     * @throws Throwable
     */
    public void testVariableBlock() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := false;");
        addLine(script, "block[a:=2;ok:=a==2;];");// set local variable, a, reset ok
        addLine(script, "oka := !is_defined(a);"); // check that a does not exist outside of block
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("oka", state);
    }

    /**
     * tests that function defined in a block are local to the block.
     * @throws Throwable
     */
    public void testFunctionBlock() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := false;");
        addLine(script, "block[f(x)->!x; ok :=f(false);];");
        addLine(script, "okf := !is_function(f,1);"); // check that a does not exist outside of block
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("okf", state);
    }
}
