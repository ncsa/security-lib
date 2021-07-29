package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.exceptions.NamespaceException;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.QDLNull;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;

import static edu.uiuc.ncsa.qdl.evaluate.OpEvaluator.*;

/**
 * A class for testing the parser. Write little scripts, test that the state is what is should be.
 * This is to let us know if, for instance, changing the lexer blows up.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:27 AM
 */
public class ParserTest extends AbstractQDLTester {
    /**
     * Tests the rational function<br/><br/>
     * f(x,y)=(x^3 + x*y^2 - 3*x*y + 1)/(x^4 + y^2 +2)<br/><br/>
     * With values
     * <ul>
     *     <li>f(3, -5) ==   1.37037037037037    </li>
     *     <li>f(3/2, -5/2) ==  1.87793427230047 </li>
     *     <li>f(3/3, -5/3) ==   1.69230769230769</li>
     *     <li>f(3/4, -5/4) ==  1.39375629405841 </li>
     * </ul>
     *
     * @throws Throwable
     */
    @Test
    public void testRational1() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("1.37037037037037"),
                new BigDecimal("1.87793427230047"),
                new BigDecimal("1.69230769230769"),
                new BigDecimal("1.39375629405841")
        };
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "define[");
        addLine(script, "f(x,y)");
        addLine(script, "]body[");
        addLine(script, "v := (x^3 + x×y^2 - 3×x×y + 1)÷(x^4 + y^2 +2);");
        addLine(script, "return(v);");
        addLine(script, "];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        addLine(script, "a:=3;");
        addLine(script, "b := -5;");
        addLine(script, "c := f(a,b);");
        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "a:=3/" + i + ";");
            addLine(script, "b := -5/" + i + ";");
            addLine(script, "c := f(a,b);");
            interpreter.execute(script.toString());

            BigDecimal bd = results[i - 1];
            BigDecimal c = (BigDecimal) state.getValue("c");
            assert areEqual(c, bd);
        }


    }

    /**
     * Test the rational function of three variables <br/><br/>
     * f(x,y,z) = (x*y^2*z^3 - x/y^2 + z^4)/(1-(x*y*(1-z))^2)
     * <p>
     * <ul>
     *     <li>f(-5, 3, 5) == 1.38912043468865      </li>
     *     <li>f(-5/2, 3/2, 5/2) == 1.55731202901014</li>
     *     <li>f(-5/3, 1, 5/3) == -7.10526315789474 </li>
     *     <li>f(-5/4, 3/4, 5/4) == 3.48158672751801</li>
     * </ul>
     *
     * @throws Throwable
     */
    @Test
    public void testRational2() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("1.38912043468865"),
                new BigDecimal("1.55731202901014"),
                new BigDecimal("-7.10526315789474"),
                new BigDecimal("3.48158672751801")
        };
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "define[");
        addLine(script, "f(x,y,z)");
        addLine(script, "]body[");
        addLine(script, "v := (x*y^2*z^3 - x/y^2 + z^4)/(1-(x*y*(1-z))^2);");
        addLine(script, "return(v);");
        addLine(script, "];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "a:=-5/" + i + ";");
            addLine(script, "b := 3/" + i + ";");
            addLine(script, "c := 5/" + i + ";");
            addLine(script, "d := f(a,b,c);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = (BigDecimal) state.getValue("d");
            assert areEqual(d, bd);
        }
    }

    /**
     * Same as above with spaces and such added to the define statement
     *
     * @throws Throwable
     */
    @Test
    public void testRational2Spaces() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("1.38912043468865"),
                new BigDecimal("1.55731202901014"),
                new BigDecimal("-7.10526315789474"),
                new BigDecimal("3.48158672751801")
        };
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "define  \n[");
        addLine(script, "f(x,y,z)");
        addLine(script, "]  body   [");
        addLine(script, "v := (x*y^2*z^3 - x/y^2 + z^4)/(1-(x*y*(1-z))^2);");
        addLine(script, "return(v);");
        addLine(script, "];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "a:=-5/" + i + ";");
            addLine(script, "b := 3/" + i + ";");
            addLine(script, "c := 5/" + i + ";");
            addLine(script, "d := f(a,b,c);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = (BigDecimal) state.getValue("d");
            assert areEqual(d, bd);
        }
    }

    /**
     * Same as previous, no <b>body</b> keyword.
     */
    @Test
    public void testRational2Spaces2() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("1.38912043468865"),
                new BigDecimal("1.55731202901014"),
                new BigDecimal("-7.10526315789474"),
                new BigDecimal("3.48158672751801")
        };
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "define  \n[");
        addLine(script, "f(x,y,z)");
        addLine(script, "]  \n\n   [");
        addLine(script, "v := (x*y^2*z^3 - x/y^2 + z^4)/(1-(x*y*(1-z))^2);");
        addLine(script, "return(v);");
        addLine(script, "];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "a:=-5/" + i + ";");
            addLine(script, "b := 3/" + i + ";");
            addLine(script, "c := 5/" + i + ";");
            addLine(script, "d := f(a,b,c);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = (BigDecimal) state.getValue("d");
            assert areEqual(d, bd);
        }
    }

    /**
     * Here is what we are testing to see if it parses right:
     * f(x,y) = 1/(2*x+3*y/(4*x+5*y/(6*x+7*y/(8*x+9*y/(x^2+y^2+1)))))
     * <pre>
     *                           1
     * =        -----------------------------------
     *                             3 y
     *          2 x + -----------------------------
     *                                5 y
     *                4 x + -----------------------
     *                                   7 y
     *                      6 x + -----------------
     *                                      9 y
     *                            8 x + -----------
     *                                       2    2
     *                                  1 + x  + y
     *   </pre>
     * <p>
     * = (192*x^3 + 192*x^5 + 68*x*y + 216*x^2*y + 68*x^3*y + 45*y^2 +
     * 192*x^3*y^2 + 68*x*y^3)/
     * (384*x^4 + 384*x^6 + 280*x^2*y + 432*x^3*y + 280*x^4*y + 21*y^2 +
     * 252*x*y^2 + 21*x^2*y^2 + 384*x^4*y^2 + 280*x^2*y^3 + 21*y^4)
     * <p>
     * The test plan is really simple: we subtract two versions of these expression and check
     * that, after evaluation, they are effectively zero.
     * <p>
     *
     * @throws Exception
     */
    @Test
    public void testContinuedFraction1() throws Throwable {
        String cf = "1/(2*x+3*y/(4*x+5*y/(6*x+7*y/(8*x+9*y/(x^2+y^2+1)))))";
        String cf2 = "  (192*x^3 + 192*x^5 + 68*x*y + 216*x^2*y + 68*x^3*y + 45*y^2 + " +
                "     192*x^3*y^2 + 68*x*y^3)/" +
                "   (384*x^4 + 384*x^6 + 280*x^2*y + 432*x^3*y + 280*x^4*y + 21*y^2 + " +
                "     252*x*y^2 + 21*x^2*y^2 + 384*x^4*y^2 + 280*x^2*y^3 + 21*y^4)";

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();

        addLine(script, "f(x,y)->" + cf);
        addLine(script, "-" + cf2 + ";"); // so f should be zero.
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        // That was to make sure the function ended up in the state, so let's be sure it
        // really works
        for (int i = 1; i < 11; i++) {
            script = new StringBuffer();
            addLine(script, "x:=-5/" + i + ";");
            addLine(script, "y := 8/(" + i + "+3);");
            addLine(script, "z := f(x,y);");
            interpreter.execute(script.toString());
            BigDecimal d = (BigDecimal) state.getValue("z");
            assert areEqual(d, BigDecimal.ZERO);
        }
    }

    /**
     * This takes a large rational expression (the one in {@link #testContinuedFraction1()} and substitutes
     * functions for the variables. The plan is define two modules with separate functions and then import them.
     * Mostly this is  a massive test tto see that states are all kept straight in a pretty intense test.
     * <pre>
     *
     *                1
     *  -----------------------------------------------------
     *            3 h(y)
     * 2 g(x) + --------------------------------------------
     *              5 h(y)
     * 4 g(x) + -----------------------------------
     *                 7 h(y)
     * 6 g(x) + --------------------------
     *                     9 h(y)
     *       8 g(x) + -----------------
     *                        2       2
     *                1 + g(x)  + h(y)
     *
     * = (192 g(x)  + 192 g(x)  + 68 g(x) h(y) + 216 g(x)  h(y) +
     *
     *              3               2           3     2               3
     *       68 g(x)  h(y) + 45 h(y)  + 192 g(x)  h(y)  + 68 g(x) h(y) ) /
     *
     *               4           6           2                3
     *      (384 g(x)  + 384 g(x)  + 280 g(x)  h(y) + 432 g(x)  h(y) +
     *
     *                4               2                2          2     2
     *        280 g(x)  h(y) + 21 h(y)  + 252 g(x) h(y)  + 21 g(x)  h(y)  +
     *
     *                4     2           2     3          4
     *        384 g(x)  h(y)  + 280 g(x)  h(y)  + 21 h(y) )
     *
     *
     * = (192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) +
     * 68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/
     * (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) +
     * 280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 +
     * 384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)
     *
     *
     * set
     * g(x) = (x^2-1)/(x^4+1)
     * h(y) = (3*y^3-2)/(4*y^2+y+1)
     * </pre>
     *
     * @throws Throwable
     */
    @Test
    public void testCalledFunctions() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("0.224684095740375"),
                new BigDecimal("0.542433484968442"),
                new BigDecimal("1.22827852483025"),
                new BigDecimal("-0.454986621086766"),
                new BigDecimal("-0.749612627182112")
        };
        String f_body = "(192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \n" +
                "     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\n" +
                "   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \n" +
                "     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \n" +
                "     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)";

        String g_x = "define[g(x)]body[return((x^2-1)/(x^4+1));];";
        String h_y = "define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];";
        String f_xy = "define[f(x,y)]body[return(" + f_body + ");];";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_x);
        addLine(script, h_y);
        addLine(script, f_xy);
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // now verify the results
        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "x :=-3/" + i + ";");
            addLine(script, "y := 5/" + i + ";");
            addLine(script, "z := f(x,y);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = getBDValue("z", state);
            assert areEqual(d, bd);
        }
    }

    @Test
        public void testLargeStemSubtraction() throws Throwable {
            // Use new bracket for stems x. := [-5/8, -5/7, -5/6, -1, -5/4]
            // and y. := [8/17, 7/16, 6/19, 5/11, 3/7]
            // Aim is to show that having a bunch of hard-coded lists gets interpreted right
            // and can do an operation on them.

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

            addLine(script, "x. := " + cf + ";");
            addLine(script, "y. := " + cf2 + ";");
            // Evaluate this is two ways and check they match
            addLine(script, "out1. := " + cf + "-" + cf2 + ";"); // single, massive expression
            addLine(script, "out2. := x. - y.;"); // do separately, subtract results
            QDLInterpreter interpreter = new QDLInterpreter(null, state);
            interpreter.execute(script.toString());
            StemVariable out1 = getStemValue("out1.", state);
            StemVariable out2 = getStemValue("out2.", state);
            assert out1.size() == 5;
            assert out2.size() == 5;
            for (long i = 0L; i < 5; i++) {
                assert areEqual(out1.getDecimal(i), out2.getDecimal(i));
            }
        }
    /**
     * Test for multiplying two matrices with integer stems. Mostly this is a regression
     * test to check that compound stems (like a.0.0) are  resolved right. When twiddleing
     * with the resolution mechanism for stems, it is very easy to break this case, so we test for it.
     * Here is how the indices are done
     * <pre>
     *      a_00  a_01
     *      a_10  a_11
     *  </pre>
     *
     * @throws Throwable
     */
    @Test
    public void testMatrixMultiply1() throws Throwable {
        // Takes x.i.j, y.i.j and returns the matrix product z.i.j

        String set_a = "a.0.0:=1; a.0.1 := 2; a.1.0 := 3; a.1.1 := 4;";
        //String set_a = "a. := [[1,2],[3,4]];";
        //String set_b = "b. := [[5,4],[3,2]];";
        String set_b = "b.0.0:=5; b.0.1 := 4; b.1.0 := 3; b.1.1 := 2;";
        String set_c00 = "c.0.0 := x.0.0*y.0.0 + x.0.1*y.1.0;\n";
        String set_c10 = "c.1.0 := x.1.0*y.0.0 + x.1.1*y.1.0;\n";
        String set_c01 = "c.0.1 := x.0.0*y.0.1 + x.0.1*y.1.1;\n";
        String set_c11 = "c.1.1 := x.1.0*y.0.1 + x.1.1*y.1.1;\n";
        String define_mm = "define[mm(x.,y.)]body[" +
                set_c00 +
                set_c01 +
                set_c10 +
                set_c11 +
                "return(c.);];";
        StringBuffer script = new StringBuffer();
        addLine(script, define_mm);
        addLine(script, set_a);
        addLine(script, set_b);
        // interpret this much so if we have to debug it we don't have a ton of initializiation to go through
        State state = testUtils.getNewState();
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        script = new StringBuffer();
        addLine(script, "z. := mm(a.,b.);");
        addLine(script, "q := is_defined(c.);"); // double check internal state stays there
        interpreter.execute(script.toString());


        assert !getBooleanValue("q", state); // checks that internally, the c. variable stayed local to the function
        assert getLongValue("z.0.0", state).equals(11L);
        assert getLongValue("z.1.0", state).equals(27L);
        assert getLongValue("z.0.1", state).equals(8L);
        assert getLongValue("z.1.1", state).equals(20L);
    }

    /**
     * Uses the setup for {@link #testMatrixMultiply1()}. This will then loop through the result with
     * a double loop and check that the indices on the result resolves right.
     *
     * @throws Throwable
     */
    @Test
    public void testMatrixLoop() throws Throwable {
        // Takes x.i.j, y.i.j and returns the matrix product z.i.j

        String set_a = "a.0.0:=1; a.0.1 := 2; a.1.0 := 3; a.1.1 := 4;";
        String set_b = "b.0.0:=5; b.0.1 := 4; b.1.0 := 3; b.1.1 := 2;";
        String set_c00 = "c.0.0 := x.0.0*y.0.0 + x.0.1*y.1.0;\n";
        String set_c10 = "c.1.0 := x.1.0*y.0.0 + x.1.1*y.1.0;\n";
        String set_c01 = "c.0.1 := x.0.0*y.0.1 + x.0.1*y.1.1;\n";
        String set_c11 = "c.1.1 := x.1.0*y.0.1 + x.1.1*y.1.1;\n";
        String define_mm = "define[mm(x.,y.)]body[" +
                set_c00 +
                set_c01 +
                set_c10 +
                set_c11 +
                "return(c.);];";
        StringBuffer script = new StringBuffer();
        addLine(script, define_mm);
        addLine(script, set_a);
        addLine(script, set_b);
        addLine(script, "z. := mm(a.,b.);");
        addLine(script, "q := is_defined(c.);"); // double check internal state stays there
        addLine(script, "w. := indices(4);"); // We want w. to exist outside of the loop so we can test it.
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        String outer_loop_start = "while[for_next(i,2)]do[";
        String inner_loop_start = "while[for_next(j,2)]do[";
        String inner_body = "w.i.j := z.i.j;";
        String inner_loop_end = "];";
        String outer_loop_end = "];";
        script = new StringBuffer();
        addLine(script, outer_loop_start);
        addLine(script, inner_loop_start);
        addLine(script, inner_body);
        addLine(script, inner_loop_end);
        addLine(script, outer_loop_end);
        interpreter.execute(script.toString());


        assert getLongValue("w.0.0", state).equals(11L);
        assert getLongValue("w.1.0", state).equals(27L);
        assert getLongValue("w.0.1", state).equals(8L);
        assert getLongValue("w.1.1", state).equals(20L);
    }

    /**
     * Checks that creating a list then looping through the elements preserves order. These elements are
     * 0,1., 2, 3. (so alternating stems)
     *
     * @throws Throwable
     */
    @Test
    public void testLoopOrder() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "ok := true;");
        addLine(script, "a. := null;");
        addLine(script, "while[");
        addLine(script, "   for_next(j, 4)");
        addLine(script, "]do[");
        addLine(script, "  if[mod(j,2)==0]then[a.j := j;]else[a.j.:=indices(2);];");
        addLine(script, "];");
        addLine(script, "// Now test that they work");
        addLine(script, "while[");
        addLine(script, "   for_keys(j,a.)");
        addLine(script, "]do[");
        addLine(script, "// say(j);");
        // we don't really need these, just test that it runs
        addLine(script, "y:= 'j==' + j + ', type=' + var_type(a.j);");
        addLine(script, "]; // end while");

        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "Looping through a list was not done in order.";

    }

    /**
     * This checks that managing scope outside of a block works. Here a variable is set to
     * null then set inside a loop and the values are updated correctly
     *
     * @throws Throwable
     */
    @Test
    public void testListScope() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := null;");
        addLine(script, "while[");
        addLine(script, "    for_next(j,10)");
        addLine(script, "]do[");
        addLine(script, "   if[");
        addLine(script, "      mod(j,2) == 0");
        addLine(script, "   ]then[");
        addLine(script, "     a.j := random_string();");
        addLine(script, "   ]else[");
        addLine(script, "     a.j. := random(3);");
        addLine(script, "   ]; // end if");
        addLine(script, "]; // end while");
        addLine(script, "");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stemVariable = getStemValue("a.", state);
        assert stemVariable.size() == 10; // Fingers and toes test.
        assert stemVariable.containsKey("1"); // odds are stems
        assert stemVariable.containsKey("0"); // evens are scalars
    }

    @Test
    public void testListScopeSpaces() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := null;");
        addLine(script, "while");
        addLine(script, "     [");
        addLine(script, "    for_next(j,10)");
        addLine(script, "]\n");
        addLine(script, "[");
        addLine(script, "   if[");
        addLine(script, "      mod(j,2) == 0");
        addLine(script, "   ]then[");
        addLine(script, "     a.j := random_string();");
        addLine(script, "   ]else[");
        addLine(script, "     a.j. := random(3);");
        addLine(script, "   ]; // end if");
        addLine(script, "]; // end while");
        addLine(script, "");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stemVariable = getStemValue("a.", state);
        assert stemVariable.size() == 10; // Fingers and toes test.
        assert stemVariable.containsKey("1"); // odds are stems
        assert stemVariable.containsKey("0"); // evens are scalars
    }

    /**
     * Tests that short-circuit conditionals work. There are two basic tests for && and ||
     * then a couple of tests that show that it does not simply bail on all subsequent conditionals
     * if one found.
     *
     * @throws Throwable
     */
    @Test
    public void testShortCircuitLogical() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 1;");
        // short circuits
        // Note that d. is not defined, so this will throw an undefined symbol error
        // if the evaluation is attempted.
        addLine(script, "b := 0 < a ||  d.2 == 5;"); // true
        addLine(script, "c := a < 1 &&  d.2 == 5;"); // false
        addLine(script, "d := 2 < 3 && (0 < a ⋁ d.2 == 5);"); //true
        addLine(script, "e := 3 < 2 ⋁ (a < 1 ⋀ d.2 ≡ 5);"); //false
        addLine(script, "f := 2 < 3 &&  1 < 3 && 4 < 3;"); //false
        addLine(script, "g := 2 < 3 &&  4 < 3 && 1 < 3;"); //false
        addLine(script, "h := 4 < 3 ⋀  1 < 3 && 1 < 3;"); //false
        addLine(script, "i := 0 < 3 ⋀  1 < 3 && 2 < 3;"); //true
        addLine(script, "j := false ||  true || d.5 == 5;"); //true
        addLine(script, "k := true ||  false || d.5 == 5;"); //true
        addLine(script, "l := is_defined(d.) && d.3 ≤ 5 ;"); //false -- d. undefined
        // before implementing this, the last test would have failed since
        // the system would still check d.3, which is undefined.
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("b", state);
        assert !getBooleanValue("c", state);
        assert getBooleanValue("d", state);
        assert !getBooleanValue("e", state);
        assert !getBooleanValue("f", state);
        assert !getBooleanValue("g", state);
        assert !getBooleanValue("h", state);
        assert getBooleanValue("i", state);
        assert getBooleanValue("j", state);
        assert getBooleanValue("k", state);
        assert !getBooleanValue("l", state);
    }

    /**
     * The parser should throw an exception on single equals, e.g.
     * <pre>
     *     a=1;
     * </pre>
     * should blow up. This is the most common beginner error (using wrong assignment operator)
     * and should not just fail silently.
     *
     * @throws Throwable
     */
    @Test
    public void testSingleEquals() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "a=1;");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to interpret single equals without parser error";
        } catch (IllegalStateException pcx) {
            assert true;
        }
    }

    /*
     a. := null;
     while[for_next(j,10)
     ]do[
       if[
          mod(j,2) == 0
       ]then[
        a.j := random_string();
        say('a.j == ' + a.j);
       ]else[
        a.j. := random(3);
        say('a.j. == ' + a.j.);
       ]; // end if
     ]; //end else
     say(a.);

     */

    /**
     * Identical setup to {@link #testCalledFunctions()} but puts g and h into modules which are then imported
     * outside the function and called. g is defined outside the module.
     * This should <b>FAIL</b> since there are multiple definitions of g
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionAndModules_Bad() throws Throwable {
        String f_body = "(192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \n" +
                "     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\n" +
                "   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \n" +
                "     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \n" +
                "     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)";

        String g_x = "define[g(x)]body[return((x^2-1)/(x^4+1));];";
        String g_module = "module['a:a','a']body[" + g_x + "];";
        String h_y = "define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];";
        String h_module = "module['b:b','b']body[" + h_y + "];";
        String import_g = "module_import('a:a');";
        String import_h = "module_import('b:b');";
        String f_xy = "define[f(x,y)]body[return(" + f_body + ");];";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_x);
        addLine(script, g_module);
        addLine(script, h_module);
        addLine(script, import_g);
        addLine(script, import_h);
        addLine(script, f_xy);
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        // all that is set up. Now put in some values and try to evaluate it
        script = new StringBuffer();
        addLine(script, "x :=-3;");
        addLine(script, "y := 5;");
        addLine(script, "z := f(x,y);");
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to access imported functions inside another function without importing it";
        } catch (NamespaceException ufx) {
            assert true;
        }

    }

    @Test
    public void testFunctionAndModules_Good() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("0.224684095740375"),
                new BigDecimal("0.542433484968442"),
                new BigDecimal("1.22827852483025"),
                new BigDecimal("-0.454986621086766"),
                new BigDecimal("-0.749612627182112")
        };
        String f_body = "(192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \n" +
                "     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\n" +
                "   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \n" +
                "     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \n" +
                "     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)";

        String g_x = "define[g(x)]body[return((x^2-1)/(x^4+1));];";
        String g_module = "module['a:a','a']body[" + g_x + "];";
        String h_y = "define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];";
        String h_module = "module['b:b','b']body[" + h_y + "];";
        String import_g = "module_import('a:a');";
        String import_h = "module_import('b:b');";
        String f_xy = "define[f(x,y)]body[" +
                import_g + "\n" +
                import_h + "\n" +
                "return(" + f_body + ");];";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_module);
        addLine(script, h_module);
        addLine(script, import_g);
        addLine(script, import_h);
        addLine(script, f_xy);
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());


        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "x :=-3/" + i + ";");
            addLine(script, "y := 5/" + i + ";");
            addLine(script, "z := f(x,y);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = getBDValue("z", state);
            assert areEqual(d, bd);
        }
    }

    /**
     * Import the same module several times and show that the state of each
     * is kept separate.
     *
     * @throws Throwable
     */
    @Test
    public void testMultipleModuleImport() throws Throwable {
        String g_x = "define[g(x)]body[return(x+1);];";
        String h_y = "define[h(y)]body[return(y-1);];";
        String g_module = "module['a:a','a']body[q:=2;w:=3;" + g_x + h_y + "];";
        String import_g = "module_import('a:a');";
        String import_g1 = "module_import('a:a', 'b');";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_module);
        addLine(script, import_g);
        addLine(script, import_g1);
        addLine(script, "a#q:=10;");
        addLine(script, "b#q:=11;");
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a#q").equals(10L);
        assert state.getValue("b#q").equals(11L);
    }


    /**
     * We test the Fibonacci continued (partial) fraction
     * f(x):=1+(1/(1+1/(1+1/(1+1/(x^2+1)))))
     * <p>
     * = (8 + 5*x^2)/(5 + 3*x^2)
     * <p>
     * <pre>
     *          2
     *   8 + 5 x
     * = --------
     *          2
     *   5 + 3 x
     * </pre>
     *
     * @throws Throwable
     */
    @Test
    public void testContinuedFraction2() throws Throwable {
        BigDecimal[] results = {
                new BigDecimal("1.64705882352941"),
                new BigDecimal("1.625"),
                new BigDecimal("1.6140350877193"),
                new BigDecimal("1.60869565217391")
        };
        String cf = "1+(1/(1+1/(1+1/(1+1/(x^2+1)))))";

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "define[");
        addLine(script, "f(x)");
        addLine(script, "]body[");
        addLine(script, "return(" + cf + ");");
        addLine(script, "];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        // That was to make sure the function ended up in the state, so let's be sure it
        // really works
        for (int i = 1; i < 5; i++) {
            script = new StringBuffer();
            addLine(script, "x:=2/" + i + ";");
            addLine(script, "z := f(x);");
            interpreter.execute(script.toString());
            BigDecimal d = getBDValue("z", state);
            BigDecimal r = results[i - 1];
            assert areEqual(d, r);
        }


    }

    /**
     * Make modules with the same variables, import then use NS qualification on the stem and its
     * indices to access them.
     *
     * @throws Throwable
     */
    public void testNSAndStem() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','a']body[i:=2;j:=3;list. := -10 + indices(5);];");
        addLine(script, "module['a:b','b']body[i:=1;j:=4;list. := -20 + indices(5);];");
        addLine(script, "i:=0;");
        addLine(script, "j:=5;");
        addLine(script, "list. := indices(10);");
        addLine(script, "module_import('a:a');");
        addLine(script, "module_import('a:b');");
        addLine(script, "d := a#list.b#i;");
        addLine(script, "e := b#list.#i;");


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        Long d = getLongValue("d", state);
        Long e = getLongValue("e", state);
        assert d.equals(-9L);
        assert e.equals(-20l);
    }

    /**
     * very, very basic compact stem notation test.
     *
     * @throws Throwable
     */

    public void testCompactStemNotation() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [5,3,1];");
        addLine(script, "b. := {'p':'q', abs(-3.4):size('abcd')};");
        addLine(script, "c. := {'z':{'t':abs(-42)}};");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        StemVariable a = getStemValue("a.", state);
        assert a.size() == 3;
        assert a.get(0L).equals(5L);
        assert a.get(1L).equals(3L);
        assert a.get(2L).equals(1L);

        StemVariable b = getStemValue("b.", state);
        assert b.size() == 2;
        assert b.containsKey("p");
        assert b.get("p").equals("q");
        assert b.containsKey("3.4");
        assert b.get("3.4").equals(4L);

        StemVariable c = getStemValue("c.", state);
        assert c.size() == 1;
        assert c.containsKey("z");
        StemVariable innnerStem = (StemVariable) c.get("z");
        assert innnerStem.size() == 1;
        assert innnerStem.containsKey("t");
        assert innnerStem.getLong("t").equals(42L);
    }

    /**
     * If a variable is in a module and that module is imported, you should be able
     * to access the variable without a namespace if it has been imported and there
     * are no clashes
     *
     * @throws Throwable
     */
    public void testNSAndVariableResolution() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','a']body[i:=2;list. := -10 + indices(5);];");
        addLine(script, "module['a:b','b']body[j:=4;list2. := -20 + indices(5);];");
        addLine(script, "module_import('a:a');");
        addLine(script, "module_import('a:b');");
        addLine(script, "p := i;");
        addLine(script, "q := list.0;");
        addLine(script, "r := j;");
        addLine(script, "s := list2.0;");
        // Note that if we want to change the value, we need to qualify it still, however.
        // Since an unqualified name gets created in the local state, not the module.
        // May want to revisit how this is done in the design though....
        addLine(script, "a#i := 5;");


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        Long p = getLongValue("p", state);
        Long q = getLongValue("q", state);
        Long r = getLongValue("r", state);
        Long s = getLongValue("s", state);
        Long i = getLongValue("i", state);
        assert q.equals(-10L);
        assert s.equals(-20l);
        assert p.equals(2L);
        assert r.equals(4L);
        assert i.equals(5L);
    }

    /**
     * In this case, modules have, of course, unique namespaces, but the aliases conflict so that is
     * changed in import.
     *
     * @throws Throwable
     */
    @Test
    public void testImportAndAlias() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:a','a']body[i:=2;j:=3;list. := -10 + indices(5);];");
        addLine(script, "module['b:b','b']body[i:=1;j:=4;list. := -20 + indices(5);];");
        addLine(script, "module['a:b','b']body[i:=1;j:=4;list. := indices(5);];");
        addLine(script, "i:=0;");
        addLine(script, "j:=5;");
        addLine(script, "list. := indices(10);");
        addLine(script, "module_import('a:a');");
        addLine(script, "module_import('b:b');");
        addLine(script, "module_import('a:b', 'd');");
        addLine(script, "d := d#list.b#i;");
        addLine(script, "e := b#list.d#i;");


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        Long d = getLongValue("d", state);
        Long e = getLongValue("e", state);
        assert d.equals(1L);
        assert e.equals(-19l);
    }

    /**
     * Basic test of assignments. Shows that := is treated as a digraph. Very basic but essential test.
     * @throws Throwable
     */
    @Test
    public void testAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=5;");
        addLine(script, "b := -10;");
        addLine(script, "c:=a+b;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 5L;
        assert getLongValue("b", state) == -10L;
        assert getLongValue("c", state) == -5L;
    }

    /**
     * Aside from the basic assignment of := there are several other assignment operators. This tests them
     *
     * @throws Throwable
     */

    @Test
    public void testAssignments() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := indices(25);");
        addLine(script, "b := 3;");
        addLine(script, "a.0+=b;"); // 0 + 3 = 3
        addLine(script, "a.1-=b;"); // 1 - 3 = -2
        addLine(script, "a.2*=b;"); // 2 * 3 =  6
        addLine(script, "a.12" + TIMES2 + "=b;"); // 12 * 3 =  36
        addLine(script, "a.3/=b;"); //   3/3 =  1
        addLine(script, "a.15" + DIVIDE2 + "=b;"); //   15/3 =  5
        addLine(script, "a.4%=b;"); //   4%3 =  1
        addLine(script, "a.5^=b;"); //   5^3 =  125
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a.0", state) == 3L;
        assert getLongValue("a.1", state) == -2L;
        assert getLongValue("a.2", state) == 6L;
        assert getLongValue("a.12", state) == 36L;
        assert areEqual(getBDValue("a.3", state), new BigDecimal("1.0"));
        assert areEqual(getBDValue("a.15", state), new BigDecimal("5.0"));

//        assert getLongValue("a.15", state) == 5L;
        assert getLongValue("a.4", state) == 1L;
        assert getLongValue("a.5", state) == 125L;
    }

    /**
     * Tests that multiple assignments on one line are processed correctly.
     *
     * @throws Throwable
     */
    @Test
    public void testMultipleAssignments() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "A := 'a'; B := 'b'; C := 'pqrc';");
        addLine(script, "q := A += B += D := C -= 'pqr';");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getStringValue("D", state).equals("c");
        assert getStringValue("C", state).equals("c");
        assert getStringValue("B", state).equals("bc");
        assert getStringValue("A", state).equals("abc");
        assert getStringValue("q", state).equals("abc");
    }

    /**
     * Tests that all the standard comparisons work.
     *
     * @throws Throwable
     */
    @Test
    public void testComparisons() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 5;");
        addLine(script, "b := 3;");
        addLine(script, "a.0 := a<b;"); //F
        addLine(script, "a.1 := a>b;"); // T
        addLine(script, "a.2 := a==b;"); //F
        addLine(script, "a.12 := a" + EQUALS2 + "b;"); //F
        addLine(script, "a.3 := a!=b;"); // T
        addLine(script, "a.13 := a" + NOT_EQUAL2 + "b;"); // T
        addLine(script, "a.4 := a<=a;"); //T
        addLine(script, "a.14 := a" + LESS_THAN_EQUAL3 + "a;"); //T
        addLine(script, "a.5 := a=<a;"); //T
        addLine(script, "a.6 := b>=b;"); //T
        addLine(script, "a.16 := b" + MORE_THAN_EQUAL3 + "b;"); //T
        addLine(script, "a.7 := b=>b;"); //T
        addLine(script, "a.8 := a==a;"); //T
        addLine(script, "a.18 := a" + EQUALS2 + "a;"); //T
        addLine(script, "a.9 := a!=a;"); //F
        addLine(script, "a.19 := a" + NOT_EQUAL2 + "a;"); //F
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("a.1", state);
        assert getBooleanValue("a.3", state);
        assert getBooleanValue("a.13", state);
        assert getBooleanValue("a.4", state);
        assert getBooleanValue("a.14", state);
        assert getBooleanValue("a.5", state);
        assert getBooleanValue("a.6", state);
        assert getBooleanValue("a.16", state);
        assert getBooleanValue("a.7", state);
        assert getBooleanValue("a.8", state);
        assert getBooleanValue("a.18", state);
        assert !getBooleanValue("a.0", state);
        assert !getBooleanValue("a.2", state);
        assert !getBooleanValue("a.12", state);
        assert !getBooleanValue("a.9", state);
        assert !getBooleanValue("a.19", state);
    }

    /**
     * Tests order of operations with logical operators is respected.
     * <pre>
     *     a:=5;b:=10;c:=-5;
     *     a&lt;b&&c&lt;a
     *  true
     * </pre>
     * @throws Throwable
     */
    @Test
    public void testLogic() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=5;");
        addLine(script, "b:=10;");
        addLine(script, "c:=-5;");
        addLine(script, "d:=a<b&&c<a;");
        addLine(script, "e:=!(a<b" + AND2 + "c<a);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);

        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 5L;
        assert getLongValue("b", state) == 10L;
        assert getLongValue("c", state) == -5L;
        assert getBooleanValue("d", state);
        assert !getBooleanValue("e", state);
    }

    @Test
    public void testLogic2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=0;");
        addLine(script, "b:=1;");
        addLine(script, "(((--b)));"); // just to be sure that multiple parentheses don't trigger multiple evaluations.
        addLine(script, "c:=2;");
        addLine(script, "d:=a++ < 0;");
        addLine(script, "e:=!((a<--b)&&(c<a));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 1L;// got incremented
        assert getLongValue("b", state) == -1L; // got decremented twice
        assert getLongValue("c", state) == 2L;
        assert !getBooleanValue("d", state);
        assert getBooleanValue("e", state);
    }

    /**
     * Tests that the contents of the string (with single quotes, double quotes and such)
     * are treated as strings and not interpreted otherwise.
     *
     * @throws Throwable
     */
    @Test
    public void testString() throws Throwable {
        String test = getRandomString(100) + "\\'!@#$%^&*()-_=+[{]}`~;:,<.>";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:='" + test + "';");
        addLine(script, "say('test say:');");
        addLine(script, "say(a);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
    }

    /**
     * test that a language with lots of diacriticals gets handled right in encode and decoding.
     *
     * @throws Throwable
     */
    @Test
    public void testUTF8StringDecodeEncode() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        // First line of the Vietnamese epic, the Tale of Kieu
        addLine(script, "a :='Trăm năm trong cõi người ta, Chữ tài chữ mệnh khéo là ghét nhau.';");
        // Some Ahmaric text...
        addLine(script, "p :='በሰማይ ፡ የምትኖር ፡ ኣባታችን ፡ ሆይ ፡';");
        addLine(script, "b := (a == vdecode(vencode(a)));");
        addLine(script, "q := (p == vdecode(vencode(p)));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("b", state);
        assert getBooleanValue("q", state);
    }

    /**
     * In order to handle "-expression" in the parser, there has to be a wrapper. This
     * tests that it does the right thing.
     *
     * @throws Throwable
     */
    @Test
    public void testMinus() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=-4;");
        addLine(script, "b:=11;");
        addLine(script, "c:=-(11+2*a);");
        addLine(script, "d.:=-indices(3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == -4L;
        assert getLongValue("b", state) == 11L;
        assert getLongValue("c", state) == -3L;
        assert getLongValue("d.0", state) == 0L;
        assert getLongValue("d.1", state) == -1L;
        assert getLongValue("d.2", state) == -2L;
    }

    @Test
    public void testPreMinuses() throws Throwable {
        // This tests multiple decrements. NOTE that the values are multipled as it decrements,
        // so the effect is to compute the factorial in this case. Prefix means the new value
        // is computed and returned, so this is (4-1)!
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=4;");
        addLine(script, "b:=-(--a * 1 *  --a * 1 * --a);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getLongValue("a", state) == 1L;
        assert getLongValue("b", state) == -6L;
    }

    @Test
    public void testPostMinuses() throws Throwable {
        // This tests multiple decrements. NOTE that the values are multiplied as it decrements,
        // so the effect is to compute the factorial in this case. Post decr. means the previous value
        // is returned and the new one is stored, so this is 4!
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=4;");
        addLine(script, "b:=-(a-- * 1 *  a-- * 1 * a--);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getLongValue("a", state) == 1L;
        assert getLongValue("b", state) == -24L;
    }

    /**
     * This test exists because the has_keys function was not actually converting the arguments of
     * its key list to strings, hence no actual list of integers would ever give a true result.
     * This test makes sure that is checked since it was a serious issue.
     *
     * @throws Throwable
     */
    @Test
    public void testHasKeys() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "var. := random(5);");
        addLine(script, "w. := indices(10);");
        addLine(script, "z. := has_keys(var., w.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // so the first 5 entries are true, the next 5 are false.
        for (int i = 0; i < 5; i++) {
            assert getBooleanValue("z." + i, state);
        }

        for (int i = 5; i < 10; i++) {
            assert !getBooleanValue("z." + i, state);
        }

    }

    /**
     * Shows making an assignment with '=' and not ':=' gets caught early as a parser error
     * (rather than having it blow up elsewhere).
     *
     * @throws Throwable
     */
    @Test
    public void testBadAssignment() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a = 3");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to make an assignment with = not :=";
        } catch (IllegalStateException t) {
            assert true;
        }

    }

    /**
     * Regression test that using compound assignment (like += ) does not create
     * unwanted entries in the symbol table. Mostly this is to make sure
     * that if some future change to the parser happens, this bug
     * does not resurface since it was very hard to isolate.
     *
     * @throws Throwable
     */
    @Test
    public void testCheckSymbolTableAssignment() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 'aaa';");
        addLine(script, "a += 'qqq';");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a").equals("aaaqqq");
        // Either of these indicate the logic for parsing op + assignment is broken. Regression tests.
        assert !state.isDefined("qqq") : "Check parser in exitAssignment call.";
        assert !state.isDefined("'qqq'") : "Check parser in exitAssignment call.";
    }

    @Test
    public void testListAppend() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        String phrase = "This is my stem " + getRandomString();
        addLine(script, "my_stem.help := '" + phrase + "';");
        addLine(script, "b. := 10 + indices(5);");
        addLine(script, "b.foo := 'bar';");
        addLine(script, "x. := list_append(my_stem., b.);");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // original unchanged
        StemVariable stem = getStemValue("my_stem.", state);
        assert stem.size() == 1;
        assert stem.getString("help").equals(phrase);

        // result has elements
        StemVariable xstem = getStemValue("x.", state);
        assert xstem.getLong("0") == 10L;
        assert xstem.getLong("1") == 11L;
        assert xstem.getLong("2") == 12L;
        assert xstem.getLong("3") == 13L;
        assert xstem.getLong("4") == 14L;

        assert xstem.containsKey("help");
        assert !xstem.containsKey("foo");
    }

    @Test
    public void testSafeUnboxWithBadVariable() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        // This creates a stem with an illegal variable name as its key, 'f(x)'
        // then tries to unbox it. It should fail
        addLine(script, "a := 'f(x)';"); // Create existing entry in symbol table
        addLine(script, "x.a := 2;"); // Create existing entry in symbol table
        addLine(script, "unbox(x.);"); // try to overwrite it in safe mode -- should encode variable

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert true;
        } catch (IllegalArgumentException ix) {
            assert false : "Was not able to unbox a variable with a bad name in safe mode.";
        }
    }


    @Test
    public void testUNSafeUnboxWithBadVariable() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        // This creates a stem with an illegal variable name as its key, 'f(x)'
        // then tries to unbox it. It should fail
        addLine(script, "a := 'f(x)';"); // Create existing entry in symbol table
        addLine(script, "x.a := 2;"); // Create existing entry in symbol table
        addLine(script, "unbox(x., false);"); // try to overwrite without safe mode -- refuse

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to unbox a variable with a name clash in safe mode.";
        } catch (IllegalArgumentException ix) {
            assert true;
        }
    }

    @Test
    public void testSafeUnbox() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 'foo';"); // Create existing entry in symbol table
        addLine(script, "unbox({'a':'b'});"); // try to overwrite it in safe mode -- should fail

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to unbox a variable with a name clash in safe mode.";
        } catch (IllegalArgumentException ix) {
            assert true;
        }
    }

    @Test
    public void testUnboxWithFlag() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 'foo';"); // Create existing entry in symbol table
        addLine(script, "unbox({'a':'b'}, false);"); // force overwrite

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("a", state).equals("b") :
                "Failed to overwrite in unbox. Expected \"b\", got \"" + getStringValue("a", state) + "\"";
    }

    @Test
    public void testUnboxWithBadVariableAndFlag() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 'f(x)';"); // Create existing entry in symbol table
        addLine(script, "x.a := 2;"); // Create existing entry in symbol table
        addLine(script, "unbox(x., true);"); // Variable should be v encoded in safe mode

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("f$28x$29", state) == 2L :
                "Failed to vencode in unbox. Expected  variable \"f$28x$29\" not found.";
    }

    /**
     * This should be improved! It does check the bare bones minimum for this feature through.
     *
     * @throws Throwable
     */
    @Test
    public void testURI() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " z := 'https://www.google.com/search?channel=fs&client=ubuntu&q=URI+specification#fragment=42';");
        addLine(script, "a := z == from_uri(to_uri(z));");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("a", state) :
                "Failed to parse a uri";
    }

    /**
     * Shows that variables can be set to null and that they exist in the symbol table.
     *
     * @throws Throwable
     */
    @Test
    public void testNullAssignments() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := null;");
        addLine(script, "a. := null;");
        addLine(script, "a0 := a == null;");
        addLine(script, "a1 := a. == null;");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a") instanceof QDLNull;
        assert state.getValue("a.") instanceof QDLNull;
        assert getBooleanValue("a0", state);
        assert getBooleanValue("a1", state);

    }

    /**
     * Basic test that setting variables to null before entering a scope has them
     * settable inside the scope and visible outside. This pattern is used
     * for conditionals, loops, and switch statements. If this breaks it means that
     * scope handling is broken generally.
     *
     * @throws Throwable
     */
    @Test
    public void testNullAssignmentScope() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := null;");
        addLine(script, "a. := null;");
        addLine(script, "r := 42;");
        addLine(script, "if[r < 100]then[a := 5;];");
        addLine(script, "if[r < 57]then[a. := indices(2);];");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getLongValue("a", state) == 5L;
        StemVariable a = getStemValue("a.", state);
        assert a.getLong("0") == 0L;
        assert a.getLong("1") == 1L;
    }

    @Test
    public void testExecute() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "execute('var := \\'abc\\' + \\'def\\';');");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("var", state).equals("abcdef");

    }

    /**
     * Tests that setting a variable to null outside of a block then setting the value inside
     * the block (which has its own state) results in the variable being set and accessible.
     *
     * @throws Throwable
     */
    @Test
    public void testVariableScope() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := null;");
        addLine(script, "if[2 < 3]then[a :=2;];");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a") != null;
        assert getLongValue("a", state) == 2L;
    }

    /**
     * Same as above, testing that spaces in the conditional do not alter it.
     * No <b>then</b> keyword
     *
     * @throws Throwable
     */
    @Test
    public void testVariableScopeSpaces1() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := null;");
        addLine(script, "if\n\n[2 < 3\n]\n\n[\n\na :=2;\n\n];");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a") != null;
        assert getLongValue("a", state) == 2L;
    }

    /**
     * Same as above, testing that spaces in the conditional do not alter it.
     * With <b>then</b> keyword
     *
     * @throws Throwable
     */
    @Test
    public void testVariableScopeSpaces2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := null;");
        addLine(script, "if\n\n[2 < 3\n]\nthen\n[\n\na :=2;\n\n];");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a") != null;
        assert getLongValue("a", state) == 2L;
    }

    /**
     * Tests that to_ and from_ json are inverses for suitably
     * well-behaved JSON. "Well-behaved" = no integers used as keys.
     * Or there is a conflict with deserializing stems since a stem
     * might have an entry like stem.0 (in a list) and stem.0. (which is
     * a stem). Such a stem cannot be turned in to a JSON Object (and an
     * {@link edu.uiuc.ncsa.qdl.exceptions.IndexError} is thrown.
     * <br/><br/>
     * So it should always work turning a JSON object in to a stem
     * Turning it back might not but the former is all we can guarantee.
     * Now, if the stem follows a few guidelines of not replicating
     * integer indices as lists (it's fine to have stem.0. be an entry and
     * the stem entry will be deserialized) but not both stem.0. and stem.0
     * and as long as this is observed, there should be no problems mapping
     * stems to JSON and back.
     * <br/>
     * This also has JSON keys that are not simply ascii, so the encoding of those
     * to and from QDL should work. Since the keys after round-tripping are checked against the
     * ones before any conversion, this tests that everything is converted as it should be.
     *
     * @throws Throwable
     */
    @Test
    public void testJSONInvariance() throws Throwable {
     /*  Used tot est with this but it fails now that string support has improved.
         probably because of embedded control characters. QDL strings do nto allow
         for embedded control characters (at least at this point).
         String rawJSON = "{" +
                "\"sub\": \"jeff\"," +
                "\"aud\": \"ashigaru:command.line2\"," +
                "\"Jäger-Groß\": \"test value\",\n" +
                " \"你浣\": \"test value2\",\n" +
                " \"uid\": \"jgaynor\",\n" +
                " \"uidNumber\": \"25939\",\n" +
                " \"isMemberOf\":  [\n" +
                "    {\n" +
                "   \"name\": \"org_ici\",\n" +
                "   \"id\": 1282\n" +
                "  },\n" +
                "    {\n" +
                "   \"name\": \"list_apcs\",\n" +
                "   \"id\": 1898\n" +
                "  }\n" +
                " ]\n" +
                "}\n";*/
        String rawJSON = "{" +
                "\"sub\": \"jeff\"," +
                "\"aud\": \"ashigaru:command.line2\"," +
                "\"Jäger-Groß\": \"test value\"," +
                " \"你浣\": \"test value2\"," +
                " \"uid\": \"jgaynor\"," +
                " \"uidNumber\": \"25939\"," +
                " \"isMemberOf\":  [" +
                "    {" +
                "   \"name\": \"org_ici\"," +
                "   \"id\": 1282" +
                "  }," +
                "    {" +
                "   \"name\": \"list_apcs\"," +
                "   \"id\": 1898" +
                "  }" +
                " ]" +
                "}";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "j := '" + rawJSON + "';");
        addLine(script, "claims. := from_json(j);");
        addLine(script, "j2 := to_json(from_json(to_json(from_json(to_json(from_json(j))))));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("j2") != null;
        String j2 = (String) state.getValue("j2");
        JSONObject jsonObject = JSONObject.fromObject(rawJSON);
        JSONObject result = JSONObject.fromObject(j2);
        assert jsonObject.size() == result.size();
        for (Object k : jsonObject.keySet()) {
            String key = k.toString();
            assert jsonObject.getString(key).equals(result.getString(key)) : "JSON equality failed for key=" + key;
        }
    }

    /**
     * Create a module, then import it to another module. The variables should be resolvable transitively
     * and the states should all be separate.
     *
     * @throws Throwable
     */
    @Test
    public void testNestedVariableImport() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/a','a']body[q:=1;];");
        addLine(script, "module_import('a:/a');");
        addLine(script, "module_import('a:/a','b');");
        addLine(script, "module['q:/q','w']body[module_import('a:/a');zz:=a#q+2;];");
        addLine(script, "a#q:=10;");
        addLine(script, "b#q:=11;");
        // Make sure that some of the state has changed to detect state management issues.
        addLine(script, "module_import('q:/q');");
        addLine(script, "w#a#q:=3;");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a#q", state) == 10L;
        assert getLongValue("b#q", state) == 11L;
        assert getLongValue("w#a#q", state) == 3L;

    }

    /*
    Define a function then define variants in modules.  Values are checked to track whether the state
    gets corrupted. This can be put into a QDL workspace to check manually:

        define[f(x)]body[return(x+100);];
        module['a:/t','a']body[define[f(x)]body[return(x+1);];];
        module['q:/z','w']body[module_import('a:/t');define[g(x)]body[return(a#f(x)+3);];];
        module_import('q:/z');
        w#a#f(3)
        w#g(2)
     */
    @Test
    public void testNestedFunctionImport() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "define[f(x)]body[return(x+100);];");
        addLine(script, "module['a:/t','a']body[define[f(x)]body[return(x+1);];];");
        addLine(script, "module['q:/z','w']body[module_import('a:/t');define[g(x)]body[return(a#f(x)+3);];];");
        addLine(script, "test_f:=f(1);");
        addLine(script, "module_import('a:/t');");
        addLine(script, "test_a:=a#f(1);");
        // Make sure that some of the state has changed to detect state management issues.
        addLine(script, "module_import('q:/z');");
        addLine(script, "test_waf := w#a#f(2);");
        addLine(script, "test_wg := w#g(2);");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("test_f", state) == 101L;
        assert getLongValue("test_a", state) == 2L;
        assert getLongValue("test_waf", state) == 3L;
        assert getLongValue("test_wg", state) == 6L;

    }
    /*
    Possible switch test?

    a.0 := 42;
    a.1. := random(3);
    a.2 := 'foo';
    a.4 := true;
    a.5 := -34555.554345;
    a.6 := null;

    while[
      for_keys(j, a.)
    ]do[
       type := var_type(a.j);
       switch[
         if[type == -1]then[say('undefined');];
         if[type == 0]then[say('null');];
         if[type == 1]then[say('boolean:' + a.j);];
         if[type == 2]then[say('integer:' + a.j);];
         if[type == 3]then[say('string:' + a.j);];
         if[type == 4]then[say('stem:' + a.j);];
         if[type == 5]then[say('decimal:' + a.j);];

        ]; //end switch
    ]; // end do
     */

    /**
     * Common construction is to set a variable null (allocate where it is in which scope)
     * then set it elsewhere inside another scope. This checks each type gets set and that a non-existent
     * variable is also not just set.
     *
     * @throws Throwable
     */
    @Test
    public void testNestedVariableScope() throws Throwable {
        StringBuffer script = new StringBuffer();
        State state = testUtils.getNewState();
        addLine(script, "a := null;");
        addLine(script, "stem. := null;");
        addLine(script, "boolean := null;");
        addLine(script, "integer := null;");
        addLine(script, "string := null;");
        addLine(script, "decimal := null;");
        addLine(script, "while[                                         ");
        addLine(script, "  for_next(j, 10)                              ");
        addLine(script, "]do[                                           ");
        addLine(script, "   switch[                                     ");
        addLine(script, "     if[j == 0]then[stem.0 := 5;];          ");
        addLine(script, "     if[j == 1]then[stem.1 := stem.0 + j;];");
        addLine(script, "     if[j == 4]then[boolean := false;];");
        addLine(script, "     if[j == 6]then[integer := j;]; ");
        addLine(script, "     if[j == 8]then[string := 'fluffy'+j;];   ");
        addLine(script, "     if[j == 9]then[decimal := j-0.4321;];");
        addLine(script, "    ]; //end switch                            ");
        addLine(script, "]; // end do                                   ");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("stem.", state);
        assert stem.getLong("0") == 5L;
        assert stem.getLong("1") == 6L;
        assert !getBooleanValue("boolean", state);
        assert getLongValue("integer", state) == 6L;
        assert getStringValue("string", state).equals("fluffy8");
        assert areEqual(getBDValue("decimal", state), new BigDecimal("8.5679"));
        assert state.getValue("a") == QDLNull.getInstance(); // QDLNull is a singleton, so we can check with ==
        assert state.getValue("A") == null;// This is what is returned for actual variables that are undefined.
    }

    @Test
    public void testLambda() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "f(x)->x^2;");
        addLine(script, "a := f(3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 9L;

    }

    @Test
    public void testMultiArgLambda() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "f(x,y,z)->x + y + z;");
        addLine(script, "a := f(1,1,1);");
        addLine(script, "b := f(3,2,1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 3L;
        assert getLongValue("b", state) == 6L;
    }

    /**
     * Since the body of a lambda function can be either a single statement or enclosed in brackets [... ]
     * what happens if the single statement is standard list notation?
     *
     * @throws Throwable
     */
    @Test
    public void testLambdaStemList() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "g(n) -> [[-n(n), 3-n(n)^2],[3*n(n+2), (n+3)*n(n+1)^3]];");
        addLine(script, "a := g(3).i(1).i(1).i(3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 162L;
    }

    /**
     * Lambda with body of function in brackets.
     *
     * @throws Throwable
     */
    @Test
    public void testLambdaInBracket() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "h(x)->[n:=2*x;return(1+3*n(n));];");
        addLine(script, "a := h(3).i(5);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 16L;
    }

    @Test
    public void testMultiStatementLambda() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "f(x)->[z:=x^2;return(z);];");
        addLine(script, "a := f(3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 9L;
    }

    @Test
    public void testMultiStatementMultiArgLambda() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "f(x,y,z)->[q:=x; q+= y; q += z; return(q);];");
        addLine(script, "a := f(1,1,1);");
        addLine(script, "b := f(3,2,1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 3L;
        assert getLongValue("b", state) == 6L;
    }

    @Test
    public void testExpAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := indices(5);j(n)->n;f()->a.;");
        addLine(script, "f().j(2) := 100;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stemVariable = getStemValue("a.", state);
        // check that exactly the value was updated
        assert stemVariable.getLong(0L) == 0L;
        assert stemVariable.getLong(1L) == 1L;
        assert stemVariable.getLong(2L) == 100L;
        assert stemVariable.getLong(3L) == 3L;
        assert stemVariable.getLong(4L) == 4L;
    }

    /**
     * Check that expressions for stems with multiple indices can be resolved and
     * have the value set. This is a crucial operation in many instances.
     *
     * @throws Throwable
     */
    @Test
    public void testMultiIndexExpAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [-indices(5), indices(6)^2];j(n)->n;f()->a.;");
        addLine(script, "f().j(1).j(2) := 100;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a.1.2", state) == 100L;
    }

    @Test
    public void testChainExpAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "q := 99; a. := indices(5);j(n)->n;f()->a.;");
        addLine(script, "f().j(2) := p := q += 1;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stemVariable = getStemValue("a.", state);
        // check that exactly the value was updated
        assert getLongValue("p", state) == 100L;
        assert getLongValue("q", state) == 100L;
        assert stemVariable.getLong(0L) == 0L;
        assert stemVariable.getLong(1L) == 1L;
        assert stemVariable.getLong(2L) == 100L;
        assert stemVariable.getLong(3L) == 3L;
        assert stemVariable.getLong(4L) == 4L;
    }

    /**
     * Assignment was changed to an operator and moved in the order of
     * operations hierarchy. This means we have to guard against regression like
     * <pre>
     *     a.b.c := d.e.f
     * </pre>
     * being parsed as
     * <pre>
     *     a.b. (c := d) .e.f
     * </pre>
     * Then either failing or giving squirrely but silent results
     * If this test breaks, then probably assignments
     * @throws Throwable
     */
    public void testChainExpAssignment2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a.b.c := 2; d.e.f :=5;");
        addLine(script, "a.b.c := d.e.f;");
        addLine(script, "rc := a.b.c == 5;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // check that exactly the value was updated
        assert getBooleanValue("rc", state);
    }
    public void testChainExpAssignment3() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "q := 99; a. := indices(5);j(n)->n;f()->a.;");
        addLine(script, "f().2 := p := q += 1;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stemVariable = getStemValue("a.", state);
        // check that exactly the value was updated
        assert getLongValue("p", state) == 100L;
        assert getLongValue("q", state) == 100L;
        assert stemVariable.getLong(0L) == 0L;
        assert stemVariable.getLong(1L) == 1L;
        assert stemVariable.getLong(2L) == 100L;
        assert stemVariable.getLong(3L) == 3L;
        assert stemVariable.getLong(4L) == 4L;
    }
    @Test
    public void testReverseAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "99 =: q; indices(5) ≕ a. ;j(n)->n;f()->a.;");
        addLine(script, "q =: f().j(2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stemVariable = getStemValue("a.", state);
        // check that exactly the value was updated
        assert getLongValue("q", state) == 99L;
        assert stemVariable.getLong(0L) == 0L;
        assert stemVariable.getLong(1L) == 1L;
        assert stemVariable.getLong(2L) == 99L;
        assert stemVariable.getLong(3L) == 3L;
        assert stemVariable.getLong(4L) == 4L;
    }

    @Test
    public void testBadExpressionStem() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "(0).(1);");
        try {
            QDLInterpreter interpreter = new QDLInterpreter(null, state);
            interpreter.execute(script.toString());
            assert false : "Was able to create a stem from (0).(1)";
        } catch (IllegalStateException t) {
            assert true;
        }
    }

    @Test
    public void testUserFunctionReference() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "r(x)->x^2 + 1;");
        addLine(script, "f(@h, x) -> h(x);");
        addLine(script, "a := f(@r, 2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 5L;

    }

    @Test
    public void testBuiltInFunctionReference() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "g(@h, x, y)->h(x, y);");
        addLine(script, "a := g(@substring, 'abcd', 2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("a", state).equals("cd");
    }

    /**
     * Fancier version
     *
     * @throws Throwable
     */
    @Test
    public void testBuiltInFunctionReference2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "g1(@h, x,y)->h(x+'pqr', y+1) + h(x+'tuv', y);");
        addLine(script, "a := g1(@substring, 'abcd', 2 );");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("a", state).equals("dpqrcdtuv");
    }

    @Test
    public void testBuiltInFunctionReferenceOrder() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "g1(x,y,@h)->h(x+'pqr', y+1) + h(x+'tuv', y);");
        addLine(script, "a := g1('abcd', 2, @substring);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("a", state).equals("dpqrcdtuv");
    }

    @Test
    public void testOpReference() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "  op(@h(), x, y) -> h(x,y);");
        addLine(script, "a := op(@+, 2, 3);");
        addLine(script, "b := op(@*, 2, 3);");
        addLine(script, "c := op(@^, 2, 3);");
        addLine(script, "d := op(@-, 2, 3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 5L;
        assert getLongValue("b", state) == 6L;
        assert getLongValue("c", state) == 8L;
        assert getLongValue("d", state) == -1L;

    }

    /**
     * Basic test of reduce over a list. Computes the factorial.
     * <pre>
     *    reduce(@*, 1+n(5))
     *  120
     * </pre>
     * @throws Throwable
     */
    @Test
    public void testReduceWithOperator() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x := reduce(@*, 1+n(5));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 120L;
    }

    /**
     * Basic test of reduce with a user-defined function.
     * <pre>
     *     times(x,y)->x*y;
     *     reduce(@times(), 1+n(5));
     *  120
     * </pre>
     * @throws Throwable
     */
    @Test
    public void testReduceWithUserFunction() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "times(x,y)->x*y;");
        addLine(script, "x := reduce(@times(), 1+n(5));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 120L;
    }

    /**
     * Basci test of the expansion operator
     * <pre>
     *    a. := [1,3,5,7];b. := [1,3,6,7];
     *    expand(@&&, a. == b.);
     * [true, true, false, false]
     * </pre>
     * @throws Throwable
     */
    @Test
    public void testExpandWithOperator() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [1,3,5,7];");
        addLine(script, "b. := [1,3,6,7];");
        addLine(script, "x. := expand(@&&, a. == b.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns [T T F F]
        assert getBooleanValue("x.0", state);
        assert getBooleanValue("x.1", state);
        assert !getBooleanValue("x.2", state);
        assert !getBooleanValue("x.3", state);
    }

    @Test
    public void testReduceWithLogicalOperator() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [1,3,5,7];");
        addLine(script, "b. := [1,3,6,7];");
        addLine(script, "x := reduce(@⋁, a. == b.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        assert getBooleanValue("x", state);
    }

    /**
     * In this test, a function named h is defined and *h() is used as a reference in another
     * function. What <i>should</i> happen is that inside the function, *h resolves to what
     * was passed in, not the global definition. If this test breaks, it is a critical bit
     * of regression.
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionReferenceVisibility() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "h(x)->-x;");
        addLine(script, "s(x)->x^2;");
        addLine(script, "f(@h(),x)->ln(h(x));");
        addLine(script, "y := f(@s(),2);");
        addLine(script, "z := ln(4);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        assert areEqual(getBDValue("y", state), getBDValue("z", state)) : "function reference visibility has changed.";
    }


    /**
     * A fork in the language J is of the form<br/><br/>
     * <pre>
     *     (monad1 dyad monad2) arg
     * </pre>
     * and is interpreted as<br/><br/>
     * <pre>
     *    dyad(monad1(arg), monad2(arg))
     * </pre>
     * This actually covers a very large range of applications. This example computes
     * the average over a list as<br/><br/>
     * <pre>
     *    sum(list.)/size(list.)
     * </pre>
     * using multiple references and shows passing multiple function references is resolved
     * correctly.
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionReferenceJFork() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "sum(x.)->reduce(@+,x.);");
        addLine(script, "fork(@a(),@b(),@c(),x.)->b(a(x.),c(x.));");
        addLine(script, "y := fork(@sum(), @/, @size(), 1+2*n(5));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        assert areEqual(getBDValue("y", state), new BigDecimal("5.0")) : "passing multiple function references failed.";

    }

    /**
     * THis tests that defining a function with a reference and repeatedly calling
     * it with different arguments does indeed do the evaluation (and not get the state
     * confused).
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionReferenceMultipleEvaluations() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "line(x) → x;");
        addLine(script, "square(x)->x^2;");
        addLine(script, "cube(x)->x^3;");
        addLine(script, "f(@z(), x) → z(x);");
        addLine(script, "x := f(@line, 3);");
        addLine(script, "y := f(@square,3);");
        addLine(script, "z := f(@cube,3);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        assert getLongValue("x", state).equals(3L);
        assert getLongValue("y", state).equals(9L);
        assert getLongValue("z", state).equals(27L);
    }

    /**
     * Shows that monadic operators are being resolved correctly
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionReferenceMonad() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "m(@monad(), arg.)->monad(arg.);");
        addLine(script, "x. := m(@!, [false, true, false]);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
        assert stem.getBoolean(0L);
        assert !stem.getBoolean(1L);
        assert stem.getBoolean(2L);
    }

    /**
     * Same as previous, with ¬ tested explicitly
     *
     * @throws Throwable
     */

    @Test
    public void testFunctionReferenceMonad2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "m(@monad(), arg.)->monad(arg.);");
        addLine(script, "x. := m(@¬, [false, true, false]);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
        assert stem.getBoolean(0L);
        assert !stem.getBoolean(1L);
        assert stem.getBoolean(2L);
    }

    /**
     * Tests three cases for visibility of functions in standard if then clause
     * Critical simple use case.
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionDefVisibilityInConditional() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := true;");
        // doesn't exist at all
        addLine(script, "if[a][q(x)->x^2;];");
        addLine(script, "x := is_function(q,1);");
        addLine(script, "q(x,y)->x*y;");
        // there is one, but with different arg count
        addLine(script, "if[a][q(x)->x^2;];");
        addLine(script, "y := is_function(q,1);");
        addLine(script, "q(x)->x^3;");
        addLine(script, "if[a][q(x)->x^2;];");
        addLine(script, "z := is_function(q,1);");
        addLine(script, "w := q(2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert !getBooleanValue("x", state);
        assert !getBooleanValue("y", state);
        assert !getBooleanValue("z", state);
        assert getLongValue("w", state).equals(4L);
    }

    /**
     * Very similar to {@link #testFunctionDefVisibilityInConditional()} , except
     * else clauses are tested.
     * Critical simple use case.
     *
     * @throws Throwable
     */
    @Test
    public void testFunctionDefVisibilityInConditional2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := false;");
        // doesn't exist at all
        addLine(script, "if  [a]\n\n  [q(x)->x^3;]else[q(x)->x^2;];");
        addLine(script, "x := is_function(q,1);");
        addLine(script, "q(x,y)->x*y;");
        addLine(script, "if[a][q(x)->x^3;]else[q(x)->x^2;];");
        addLine(script, "y := is_function(q,1);");
        addLine(script, "q(x)->x^3;");
        addLine(script, "if[a][q(x)->x^3;]else[q(x)->x^2;];");
        addLine(script, "z := is_function(q,1);");
        addLine(script, "w := q(2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        assert !getBooleanValue("x", state);
        assert !getBooleanValue("y", state);
        assert !getBooleanValue("z", state);
        assert getLongValue("w", state).equals(4L);
    }



    /**
     * Case of a simple module. The point is that a function f, is defined in the module and that
     * another module function g calls it. What should happen (assuming no other definitions of these
     * in the state, which would throw a namespace exception) is that g uses f and that is
     * that. If the module functions are not being added only to the module state, then there would be errors.
     * Critical simple use case.
     * <pre>
     *     module['a:a','a'][f(x)->x^2;g(x)->f(x+1);];
     *     module_import('a:a');
     *     g(1);
     *  4
     * </pre>
     * @throws Throwable
     */
    public void testModuleFunctionVisibility() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " module['a:a','a'][f(x)->x^2;g(x)->f(x+1);];");
        addLine(script, "module_import('a:a');");
        addLine(script, "y := g(1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
        assert getLongValue("y", state).equals(4L);
    }

    /**
     * Same as above, with spaces.
     *
     * @throws Throwable
     */
    public void testModuleFunctionVisibilitySpaces() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " module   \n[\n'a:a',\n'a'\n]   \n\n[\n  f(x)->x^2;g(x)->f(x+1);  \n]   \n\n;");
        addLine(script, "module_import('a:a');");
        addLine(script, "y := g(1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
        assert getLongValue("y", state).equals(4L);
    }

    /**
     * trigger that having competing function names between the workspace and a module
     * is flagged as an error.
     *
     * @throws Throwable
     */
    @Test
    public void testBadModuleFunctionVisibility() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "f(x)->x;");
        addLine(script, " module['a:a','a'][f(x)->x^2;g(x)->f(x+1);];");
        addLine(script, "module_import('a:a');");
        addLine(script, "y := g(1);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "the visibility of module functions is incorrect";
        } catch (NamespaceException ix) {
            assert true;
        }
    }

    /**
     * Tests that functions defined in functions are resolved correctly.
     * Here this computes (x+1)^2 as a composition of two functions, t and s.
     *
     * @throws Throwable
     */
    @Test
    public void testNestedFunction() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "define[f(x)][t(x)->x+1;s(x)->x^2;return(s(t(x)));];");
        addLine(script, "x := f(2);"); // This is almost the largest value a long in Java can hold
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state).equals(9L);
    }

    /**
     * Devious test for this. Run it with the join command and check that
     * every value of the result is true as a string. This saves me from having
     * to do a low-level slog looking for any failures.
     */
    @Test
    public void testJoinOnLastAxis() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "q. := [[n(4), 4+n(4)],[8+n(4),12+n(4)], [16+n(5),21+n(5)]];");
        addLine(script, "w. := 100 + q.;");
        addLine(script, "a. := join(q., w., 3);");
        addLine(script, "b. := q.~|w.;");
        addLine(script, "out := to_string(b. == a.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("out", state).indexOf("false") < 0;
        // returns true if any elements are true
        StemVariable stem = getStemValue("x.", state);
    }

    /**
     * Longs in Java have a max value of 9223372036854775806 and a min value of
     * -9223372036854775805. What should happen is that when this is reached, the
     * value is seamlessly converted to a BigDecimal internally.
     * @throws Throwable
     */
    @Test
    public void testOverflowAdd() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "numeric_digits(25);");
        addLine(script, "x := 9223372036854775806 + 6;"); // This is almost the largest value a long in Java can hold
        addLine(script, "y := -9223372036854775805 - 6;"); // This is almost the smallest value a long in Java can hold
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert areEqual(getBDValue("x", state), new BigDecimal("9223372036854775812"));
        assert areEqual(getBDValue("y", state), new BigDecimal("-9223372036854775811"));
    }

    /**
     * Tests various combinations of numbers with E in them.  Also does a few with
     * e (lower case) too, showing case insensitivity.
     * @throws Throwable
     */
    @Test
    public void testEngineeringNotation() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "numeric_digits(25);");
        addLine(script, "x := 1.2E3+2.3E-2;");
        addLine(script, "y := 1.2E3-2.3E-2;");
        addLine(script, "z := 1.2E3*2.3E-2;");
        addLine(script, "w := 1.2e3/2.3e-2;");
        addLine(script, "t := 1.2e3^2.3e-2;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert areEqual(getBDValue("x", state), new BigDecimal("1200.023"));
        assert areEqual(getBDValue("y", state), new BigDecimal("1199.977"));
        assert areEqual(getBDValue("z", state), new BigDecimal("27.6"));
        assert areEqual(getBDValue("w", state), new BigDecimal("52173.913043478260869"));
        assert areEqual(getBDValue("t", state), new BigDecimal("1.17712116537414"));

    }

    /**
     * Tests that executing a couple of transcendental math functions on a 3 rank array
     * actually processes all the elements. Uses sin<sup>2</sup>(x) + cos<sup>2</sup>(x) ==1.
     *
     * @throws Exception
     */
    @Test
    public void testTMathOnArray() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x. := sin(n(2,3,4,[|-pi()/2;pi()/2; 24|]))^2 + cos(n(2,3,4,[|-pi()/2;pi()/2; 24|]))^2;");
        addLine(script, "y := reduce(@+, reduce(@+, reduce(@+, x.)));");  // add em all up
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert areEqual(getBDValue("y", state), new BigDecimal("24.0000000"));

    }

    /**
     * basic regression test for the slice notation
     *
     * @throws Throwable
     */
    public void testSlices() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x := reduce(@∧, [-1;11;2]==[-1,1,3,5,7,9,11]);");
        addLine(script, "y := reduce(@∧, ⟦-1;2;6⟧ == [-1,-0.4,0.2,0.8,1.4,2]);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("x", state);
        assert getBooleanValue("y", state);
    }

    public void testSlicesDefaultValues() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x := reduce(@∧, [;6]==[0,1,2,3,4,5]);");
        addLine(script, "x := reduce(@∧, [;11;2]==[0,2,4,6,8,10]);");
        addLine(script, "y := reduce(@∧, ⟦-1;2;6⟧ == [-1,-0.4,0.2,0.8,1.4,2]);");
        addLine(script, "y := reduce(@∧, ⟦;2;6⟧ == [0,0.4,0.8,1.2,1.6,2]);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("x", state);
        assert getBooleanValue("y", state);
    }

    /**
     *
     * Basic regression test to show that passing in a function definition
     * works.
     * <pre>
     *     g(@f, x)-> f(x)^3
     *     g(v(x)->x^2, 5); //computes (5^2)^3 == 5^6
     * 15625
     * </pre>
     * @throws Throwable
     */
      public void testLambdaAsArgument() throws Throwable {
           State state = testUtils.getNewState();
           StringBuffer script = new StringBuffer();
           addLine(script, "g(@f, x)-> f(x)^3;");
           addLine(script, "ok := g(v(x)->x^2, 5) == 15625;");
           QDLInterpreter interpreter = new QDLInterpreter(null, state);
           interpreter.execute(script.toString());
           assert getBooleanValue("ok", state);
       }

    /**
     * Anonymous lambda function test. Pass in a function with no name as an argument.
     * <pre>
     *     g(@f, x)-> f(x)^4;
     *     g((x)->x^2, 2);  //evaluates to 2^8
     *  256
     * </pre>
     * @throws Throwable
     */
    public void testAnonymousLambdaAsArgument() throws Throwable {
           State state = testUtils.getNewState();
           StringBuffer script = new StringBuffer();
           addLine(script, "g(@f, x)-> f(x)^4;");
           addLine(script, "ok := g((x)->x^2, 2) == 256;");
           QDLInterpreter interpreter = new QDLInterpreter(null, state);
           interpreter.execute(script.toString());
           assert getBooleanValue("ok", state);
       }
       /*
                     reduce((x,y)->x+y, n(10))
45
        */
       public void testAnonymousReduce() throws Throwable {
              State state = testUtils.getNewState();
              StringBuffer script = new StringBuffer();
              addLine(script, "ok := reduce((x,y)->x+y, n(10)) == 45;");
              QDLInterpreter interpreter = new QDLInterpreter(null, state);
              interpreter.execute(script.toString());
              assert getBooleanValue("ok", state);
          }
          /*
            for_each((x,y)->x*y,n(10),[1;11])

           */
          public void testAnonymousForEach() throws Throwable{
              State state = testUtils.getNewState();
              StringBuffer script = new StringBuffer();
              addLine(script, "x. := for_each((x,y)->x*y,n(10),[1;11]);");
              addLine(script, "ok.0 := x.3.4 == 3*5;"); // second arg offset to 1 -10 range
              addLine(script, "ok.1 := x.4.5 == 4*6;");
              addLine(script, "ok.2 := x.5.6 == 5*7;");
              addLine(script, "ok.3 := x.0.7 == 0;");
              addLine(script, "ok.4 := x.1.9 == 10;");
              addLine(script, "ok.5 := x.2.4 == 2*5;");
              addLine(script, "ok.6 := x.9.9 == 90;");
              addLine(script, "ok := reduce(@&&, ok.);");
              QDLInterpreter interpreter = new QDLInterpreter(null, state);
              interpreter.execute(script.toString());
              assert getBooleanValue("ok", state);

          }


    /**
     * Tests visibility of lambda. One version of g is created, then a dummy function
     * named g is passed in. The correct behavior is that the function reference is evaluated
     * locally only. Last check (ok1 == -1) shows that the original function is still
     * available unchanged.
     * <pre>
     *   g(x,y)->x-y
     *   reduce(g(x,y)->x+y, n(10))
     * 45
     *   g(3,4)
     * -1
     * </pre>
     * @throws Throwable
     */
    public void testLambdaFunctionVisibility() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "g(x,y)->x-y;"); // define g outside of function
        addLine(script, "ok0 := reduce(g(x,y)->x+y, n(10))  == 45;");
        addLine(script, "ok1 := g(3,4) == -1;"); // after the evaluation, the function table is not changed
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);

    }


    /**
     * tests that supplying a loop with a list in the for_next function works.
     * @throws Throwable
     */
    public void testLoopOverList() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "j := 0;");
        addLine(script, "ok := true;");
        addLine(script, "while[for_next(i,2*[;5])][ok := ok && (i == 2*j++);];");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getLongValue("j", state).equals(5L); // make sure it did all the elements

    }
    /* The following is an experiment with allowing lists of indices to be used for ste
       access. E.g.
                   a. := n(2,3,4,n(24))
                   nn. := n(2,3,4)
                   a.[1,2,3] == a.1.2.3
              true
                   a.1.2.nn.[1,2,3]  == a.1.2.3
               true

       This has been on the radar for a while, but there were a lot of competing models of how to do it.
        settled on letting . be distributive, i.e. a.[0,1,2] distributes as a.0.1.2 rather than being
        termwise (cf. a. * [0,1,2] which would result in a.[0,1,2] ==  [a.0, a.1, a.2] which is quite difference
        as a. is a higher dimension stem.) This allows for creating indices programatically which is important
        if the structure of a stem has to be interrogated before using it
     */

    /**
     * Tests that list indices are transitive, i.e.
     * <pre>
     *    a.[1,2,3] == a.1.2.3
     * </pre>
     * @throws Throwable
     */
    public void testListsAsIndices0() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := n(2,3,4,n(24));");
        addLine(script, "nn. := n(2,3,4);");
        addLine(script, "ok := a.[1,2,3] == a.1.2.3;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }

    /**
     * Test bad index where a higher dimension stem as part of the index does not resolve
     * correctly. This tells us that if a stem gets another, higher ranked stem as its
     * index, that should throw an index error.
     * @throws Throwable
     */
    public void testBadListsAsIndices0() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := n(2,3,4,n(24));");
        addLine(script, "nn. := n(2,3,4);");
        addLine(script, "a.1.nn.[1,2,2].0;");//nn.1 is a 3 rank stem, so cannot be an index
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "a.1.nn.[1,2,2].0 should have thrown an index error";
        }catch(IndexError ie){
            assert true;
        }
    }
    public void testBadListsAsIndices1() throws Throwable{
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "a. := n(2,3,4,n(24));");
         addLine(script, "nn. := n(2,3,4);");
        addLine(script, "a.0.nn.[1,2,2].1  == a.0.2.1;"); // fails before [1,2,2].1-> 2.

         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         try {
             interpreter.execute(script.toString());
             assert false : "a.0.nn.[1,2,2].1 should have thrown an index error";
         }catch(IndexError ie){
             assert true;
         }
     }

    /**
     * Checks that referncing non-existent elements fails at is should.
     * @throws Throwable
     */
    public void testBadListsAsIndices2() throws Throwable{
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "a. := n(2,3,4,n(24));"); // define g outside of function
         addLine(script, "nn. := n(2,3,4);");
        addLine(script, " a.nn.[1,2,1].2.0  == a.1.2.0;"); // fails because [1,2,1].2.0 does not resolve.

         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         try {
             interpreter.execute(script.toString());
             assert false : "a.nn.[1,2,1].2.0 should have thrown an index error";
         }catch(IndexError ie){
             assert true;
         }
     }

    /**
     * Test lots of simple cases for list indices.
     * @throws Throwable
     */
    public void testListsAsIndices1() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := n(2,3,4,n(24));"); // define g outside of function
        addLine(script, "nn. := n(2,3,4);");

        addLine(script, "ok0 := a.1.2.nn.[1,2,3]  == a.1.2.3;"); // works because nothing to its right
        addLine(script, "ok1 := a.1.2.(nn.[1,2,3])  == a.1.2.3;"); // most basic test
        addLine(script, "ok2 := a.1.2.nn.1.2.3  == a.1.2.3;");   // most basic test

        addLine(script, "ok3 := a.0.(nn.[1,2,2]).1  == a.0.2.1;");
        addLine(script, "ok4 := a.0.nn.1.2.2.1  == a.0.2.1;");

        addLine(script, "ok5 := a.(nn.[1,2,1]).2.0  == a.1.2.0;"); // most basic test
        addLine(script, "ok6 := a.nn.1.2.1.2.0  == a.1.2.0;"); // most basic test
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok0", state);
        assert getBooleanValue("ok1", state);
        assert getBooleanValue("ok2", state);
        assert getBooleanValue("ok3", state);
        assert getBooleanValue("ok4", state);
        assert getBooleanValue("ok5", state);
        assert getBooleanValue("ok6", state);
    }

    /**
     * Tests the list indices by shuffling the indices and accessing
     * one element and setting the another:
     * <pre>
     *     a. := n(2,3,4,n(24))
     *     p. := [2,0,1]
     *     b.shuffle([0,1,2],p.) := a.[0,1,2]
     *     b.
     * {2:[{1:6}]}
     * </pre>
     * @throws Throwable
     */
    public void testSetWithListIndex() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := n(2,3,4,n(24));");
        addLine(script, "p. := [2,0,1];");
        addLine(script,"b.shuffle([0,1,2],p.) := a.[0,1,2];");
        addLine(script, "ok := b.2.0.1 == 6;"); // most basic test
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
    }


    /**
     * Very simple recursion test using lambdas
     * <pre>
     *     sum(n)->(n!=0)?sum(n-1)+n : 0; // recursive function
     *     sum(7)
     * 28
     * </pre>
     * @throws Throwable
     */
    public void testLambdaRecursion() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "sum(n)->(n!=0)?sum(n-1)+n:0;");
        addLine(script, "ok := sum(7)==28;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state) : "Recursion failed for a lambda function";
    }

}
