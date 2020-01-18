package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.TestBase;
import edu.uiuc.ncsa.security.util.qdl.expressions.*;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  10:26 AM
 */
public class IOFunctionTest extends TestBase {
    public void scanExample() throws Exception{
        // This has to be run in the CLIRunner class in this package, not
        // as a unit test, because jUnit will hang since it does not process input
        // right during testing.
        OpEvaluator opEvaluator = new OpEvaluator();
        FunctionEvaluator functionEvaluator = new FunctionEvaluator();
        Polyad polyad = new Polyad(opEvaluator,functionEvaluator,  IOEvaluator.SCAN_TYPE);
        ConstantNode prompt = new ConstantNode("sayit>", Constant.STRING_TYPE);
        polyad.getArgumments().add(prompt);
        polyad.evaluate();
        System.out.println("you entered:\"" + polyad.getResult() + "\"");
    }
    @Test
    public void testSay() throws Exception{
         OpEvaluator opEvaluator = new OpEvaluator();
        FunctionEvaluator functionEvaluator = new FunctionEvaluator();

         Polyad polyad = new Polyad(opEvaluator, functionEvaluator, IOEvaluator.SAY_TYPE);
         String testString = "These are the droids you are looking for";
         ConstantNode prompt = new ConstantNode(testString, Constant.STRING_TYPE);
         polyad.getArgumments().add(prompt);
         polyad.evaluate();
         System.out.println("Check that the phrase :\"" + testString + "\" was printed");
     }
}
