package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/17/20 at  7:10 AM
 */
public class ParserTest2 extends AbstractQDLTester{

    @Test
    public void testContinuedFraction1() throws Throwable {
        //Repeat this test with new notation. x:= [-5/8, -5/7, -5/6, -1, -5/4]
        // and y = [8/17, 7/16, 6/19, 5/11, 3/7]
        // Aim is to show that having a bunch of lists gets interpreted right

            String cf = "1/(2*[-5/8, -5/7, -5/6, -1, -5/4]" +
                    "+3*[8/17, 7/16, 6/19, 5/11, 3/7]/(4*[-5/8, -5/7, -5/6, -1, -5/4]" +
                    "+5*[8/17, 7/16, 6/19, 5/11, 3/7]/(6*[-5/8, -5/7, -5/6, -1, -5/4]" +
                    "+7*[8/17, 7/16, 6/19, 5/11, 3/7]/(8*[-5/8, -5/7, -5/6, -1, -5/4]" +
                    "+9*[8/17, 7/16, 6/19, 5/11, 3/7]/([-5/8, -5/7, -5/6, -1, -5/4]^2+[8/17, 7/16, 6/19, 5/11, 3/7]^2+1)))))";
            String cf2 = "  (192*[-5/8, -5/7, -5/6, -1, -5/4]^3 + 192*[-5/8, -5/7, -5/6, -1, -5/4]^5 " +
                    "+ 68*[-5/8, -5/7, -5/6, -1, -5/4]*[8/17, 7/16, 6/19, 5/11, 3/7] + 216*[-5/8, -5/7, -5/6, -1, -5/4]^2*[8/17, 7/16, 6/19, 5/11, 3/7] " +
                    "+ 68*[-5/8, -5/7, -5/6, -1, -5/4]^3*[8/17, 7/16, 6/19, 5/11, 3/7] + 45*[8/17, 7/16, 6/19, 5/11, 3/7]^2 + " +
                    "     192*[-5/8, -5/7, -5/6, -1, -5/4]^3*[8/17, 7/16, 6/19, 5/11, 3/7]^2 + 68*[-5/8, -5/7, -5/6, -1, -5/4]*[8/17, 7/16, 6/19, 5/11, 3/7]^3)/" +
                    "   (384*[-5/8, -5/7, -5/6, -1, -5/4]^4 + 384*[-5/8, -5/7, -5/6, -1, -5/4]^6 " +
                    "+ 280*[-5/8, -5/7, -5/6, -1, -5/4]^2*[8/17, 7/16, 6/19, 5/11, 3/7] + 432*[-5/8, -5/7, -5/6, -1, -5/4]^3*[8/17, 7/16, 6/19, 5/11, 3/7] " +
                    "+ 280*[-5/8, -5/7, -5/6, -1, -5/4]^4*[8/17, 7/16, 6/19, 5/11, 3/7] + 21*[8/17, 7/16, 6/19, 5/11, 3/7]^2 + " +
                    "     252*[-5/8, -5/7, -5/6, -1, -5/4]*[8/17, 7/16, 6/19, 5/11, 3/7]^2 + 21*[-5/8, -5/7, -5/6, -1, -5/4]^2*[8/17, 7/16, 6/19, 5/11, 3/7]^2 " +
                    "+ 384*[-5/8, -5/7, -5/6, -1, -5/4]^4*[8/17, 7/16, 6/19, 5/11, 3/7]^2 + 280*[-5/8, -5/7, -5/6, -1, -5/4]^2*[8/17, 7/16, 6/19, 5/11, 3/7]^3 " +
                    "+ 21*[8/17, 7/16, 6/19, 5/11, 3/7]^4)";

            State state = testUtils.getNewState();
            StringBuffer script = new StringBuffer();

            addLine(script, "x. := " + cf  + ";");
            addLine(script, "y. := " + cf2 + ";");
            addLine(script, "out1. := " + cf + "-" + cf2 + ";");
            addLine(script, "out2. := x. - y.;");
            QDLParser interpreter = new QDLParser(null, state);
            interpreter.execute(script.toString());
            System.out.println(getStemValue("x.", state));
            System.out.println(getStemValue("y.", state));
            System.out.println(getStemValue("out1.", state));
            System.out.println(getStemValue("out2.", state));
            // That was to make sure the function ended up in the state, so let's be sure it
            // really works
/*
            for (int i = 1; i < 11; i++) {
                script = new StringBuffer();
                addLine(script, "x:=-5/" + i + ";");
                addLine(script, "y := 8/(" + i + "+3);");
                addLine(script, "z := f(x,y);");
                interpreter.execute(script.toString());
                BigDecimal d = (BigDecimal) state.getValue("z");
                assert areEqual(d, BigDecimal.ZERO);
            }
*/
        }


}
