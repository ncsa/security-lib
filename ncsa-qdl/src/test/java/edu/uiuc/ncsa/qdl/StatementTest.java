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
     * Test that a malformed loop (conditional statement is not a conditional) fails reasonably.
     *
     * @throws Throwable
     */

    public void testBadLoop() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "i:=0;");
        addLine(script, "while[");
        addLine(script, "   i + 2"); // Not a conditional, so the system should throw it out.
        addLine(script, "]do[");
        addLine(script, "  say(i);");
        addLine(script, "]; // end while");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        boolean bad = true;
        try {
            interpreter.execute(script.toString());
        } catch (IllegalStateException isx) {
            bad = false;
        }
        if(bad){
            assert false : "Was able to execute a loop without a valid conditional";
        }

    }

    /**
     * Test a loop that has a basic conditional statement (with something that needs evaluated)
     *
     * @throws Throwable
     */

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
     * Test that assigning a value to a keyword, e.g. false := true fails.
     *
     * @throws Throwable
     */
     
    public void testReservedWordAssignments() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, QDLConstants.RESERVED_TRUE + " := 2;");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        boolean bad = true;
        try {
            interpreter.execute(script.toString());
        } catch (IllegalStateException | IllegalArgumentException iax) {
            bad = false;
        }
        if(bad){
            assert false : "Error; Was able to assign " + QDLConstants.RESERVED_TRUE + " a value";
        }
        bad = true;

        script = new StringBuffer();
        addLine(script, QDLConstants.RESERVED_FALSE + " := 1;");
        try {
            interpreter.execute(script.toString());
        } catch (IllegalStateException | IllegalArgumentException iax) {
            bad = false;
        }
        if(bad){
            assert false : "Error; Was able to assign " + QDLConstants.RESERVED_FALSE + " a value";
        }
bad = true;

        script = new StringBuffer();
        addLine(script, QDLConstants.RESERVED_NULL + " := 'foo';");
        try {
            interpreter.execute(script.toString());
        } catch (IllegalArgumentException iax) {
            bad = false;
        }
        if(bad){
            assert false : "Error; Was able to assign " + QDLConstants.RESERVED_NULL + " a value";
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
        boolean bad = true;
        try {
            interpreter.execute(script.toString());
        } catch (AssertionException assertionException) {
            bad= false;
            assert assertionException.getMessage().equals("test 1");
        }
        if(bad){
            assert false : "failed assertion not asserted";
        }

    }

    public void testBadAssert2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "⊨ 3<2 : 'test 1';");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        boolean bad = true;
        try {
            interpreter.execute(script.toString());
        } catch (AssertionException assertionException) {
            bad = false;
            assert assertionException.getMessage().equals("test 1");
        }
        if(bad){
            assert false : "failed assertion not asserted";
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

    /**
     * Basic try ... catch block. This has spaces to test new parser upgrades in 1.4.
     * @throws Throwable
     */
    public void testTryCatch() throws Throwable{
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "a := -1;");
         addLine(script, "try   [to_number('foo');a:=1;] catch  [a:=2;];");// set local variable, a,
         addLine(script, "ok := a == 2;"); // check that a does not exist outside of block
         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
     }
    public void testSwitch0() throws Throwable{
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "a := -1;b:=2;");
         addLine(script, "switch   [");// set local variable, a,
         addLine(script, "   if[a+b < 0][a:=-2;];");
         addLine(script, "   if[a-b > 0][a:=-3;];");
         addLine(script, "   if[a*b < 0][a:=0;];");
         addLine(script, "];");
         addLine(script, "ok := a == 0;");
         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
     }

    /**
     * Same as {@link #testSwitch0()}, just change the order of the conditionals
     * @throws Throwable
     */
    public void testSwitch1() throws Throwable{
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "a := -1;b:=2;");
         addLine(script, "switch   [");// set local variable, a,
         addLine(script, "   if[a+b < 0][a:=-2;];");
        addLine(script, "   if[a*b < 0][a:=0;];");
        addLine(script, "   if[a-b > 0][a:=-3;];");
         addLine(script, "];");
         addLine(script, "ok := a == 0;");
         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
     }

    /**
     * Same as {@link #testSwitch0()}, just change the order of the conditionals
     * @throws Throwable
     */
    public void testSwitch2() throws Throwable{
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "a := -1;b:=2;");
         addLine(script, "switch   [");// set local variable, a,
        addLine(script, "   if[a*b < 0][a:=0;];");
        addLine(script, "   if[a-b > 0][a:=-3;];");
        addLine(script, "   if[a+b < 0][a:=-2;];");
         addLine(script, "];");
         addLine(script, "ok := a == 0;");
         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
     }

    /**
     * how to do a default case for a switch in QDL: Since the statements are evaluated in order, just
     * have the last one as if[true]. This test shows that if an earlier one works, then that is chosen.
     * @throws Throwable
     */
    public void testSwitchDefault() throws Throwable{
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "a := -1;b:=2;");
          addLine(script, "switch   [");// set local variable, a,
          addLine(script, "   if[a*b < 0][a:=-4;];");
          addLine(script, "   if[a-b > 0][a:=-3;];");
          addLine(script, "   if[a+b == 0][a:=-2;];");
          addLine(script, "   if[true][a:=0;];");  // default case, not used
          addLine(script, "];");
          addLine(script, "ok := a == -4;");
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          interpreter.execute(script.toString());
          assert getBooleanValue("ok", state);
      }

    /**
     * How to do a default case for a switch in QDL part 2:  This test shows that
     * the default is use if all the others fail.
     * @throws Throwable
     */
    public void testSwitchDefault1() throws Throwable{
          State state = testUtils.getNewState();
          StringBuffer script = new StringBuffer();
          addLine(script, "a := -1;b:=2;");
          addLine(script, "switch   [");// set local variable, a,
          addLine(script, "   if[a*b > 0][a:=-4;];");
          addLine(script, "   if[a-b > 0][a:=-3;];");
          addLine(script, "   if[a+b == 0][a:=-2;];");
          addLine(script, "   if[true][a:=0;];"); // default case used
          addLine(script, "];");
          addLine(script, "ok := a == 0;");
          QDLInterpreter interpreter = new QDLInterpreter(null, state);
          interpreter.execute(script.toString());
          assert getBooleanValue("ok", state);
      }
}
