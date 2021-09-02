package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.IOEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.Constant;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:26 AM
 */
public class IOFunctionTest extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

    public void scanExample() throws Exception {
        State state = testUtils.getNewState();

        // This has to be run in the CLIRunner class in this package, not
        // as a unit test, because jUnit will hang since it does not process input
        // right during testing.
        Polyad polyad = new Polyad(IOEvaluator.SCAN_FUNCTION);
        ConstantNode prompt = new ConstantNode("sayit>", Constant.STRING_TYPE);
        polyad.getArguments().add(prompt);
        polyad.evaluate(state);
        System.out.println("you entered:\"" + polyad.getResult() + "\"");
    }

     
    public void testSay() throws Exception {
        State state = testUtils.getNewState();
        Polyad polyad = new Polyad(IOEvaluator.SAY_FUNCTION);
        String testString = "These are the droids you are looking for";
        ConstantNode prompt = new ConstantNode(testString, Constant.STRING_TYPE);
        polyad.getArguments().add(prompt);
        polyad.evaluate(state);
        System.out.println("Check that the phrase \"" + testString + "\" was printed");
    }
}
