package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.exceptions.ImportException;
import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * A class for testing the parser. Write little scripts, test that the state is what is should be.
 * This is to let us know if, for instance, changing the lexer blows up.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:27 AM
 */
public class ParserTest extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();
    /*



    cf[x_]:=1+(1/(1+1/(1+1/(1+1/(x^2+1)))))
    For[i=1,i<5,i++,Print[N[cf[2/i],15]]]
    1.64705882352941
    1.625
    1.6140350877193
    1.60869565217391

    cf2[x_,y_]:=1/(x+y/(x+y/(x+y/(x+y/(x^2+y^2+1)))))

    In[55]:= For[i=1,i<6,i++,Print[N[cf2[2/i,-3/i],15]]]
    0.415492957746479
    3.35135135135135
    -2.80739299610895
    -1.30201342281879
    -0.925538758443229
     */

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
        addLine(script, "v := (x^3 + x*y^2 - 3*x*y + 1)/(x^4 + y^2 +2);");
        addLine(script, "return(v);");
        addLine(script, "];");
        QDLParser interpreter = new QDLParser(null, state);
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
            assert compare(c, bd, comparisonTolerance);
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
        QDLParser interpreter = new QDLParser(null, state);
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
            assert compare(d, bd, comparisonTolerance);
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
        addLine(script, "define[");
        addLine(script, "f(x,y)");
        addLine(script, "]body[");
        addLine(script, "return(" + cf + "-" + cf2 + ");");
        addLine(script, "];");
        QDLParser interpreter = new QDLParser(null, state);
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
            assert compare(d, BigDecimal.ZERO, comparisonTolerance);
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
     *        3 h(y)
     * 2 g(x) + --------------------------------------------
     *            5 h(y)
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
        QDLParser interpreter = new QDLParser(null, state);
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
            assert compare(d, bd, comparisonTolerance);
        }
    }

    BigDecimal comparisonTolerance = new BigDecimal(".0000000000001");

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
        QDLParser interpreter = new QDLParser(null, state);
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

        QDLParser interpreter = new QDLParser(null, state);
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
        String import_g = "import('a:a');";
        String import_h = "import('b:b');";
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        // all that is set up. Now put in some values and try to evaluate it
        script = new StringBuffer();
        addLine(script, "x :=-3;");
        addLine(script, "y := 5;");
        addLine(script, "z := f(x,y);");
        try {
            interpreter.execute(script.toString());
            System.out.println( state.getValue("z"));
            assert false : "Was able to access imported functions inside another function without importing it";
        } catch (ImportException ufx) {
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
        String import_g = "import('a:a');";
        String import_h = "import('b:b');";
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());


        for (int i = 1; i < 1 + results.length; i++) {
            script = new StringBuffer();
            addLine(script, "x :=-3/" + i + ";");
            addLine(script, "y := 5/" + i + ";");
            addLine(script, "z := f(x,y);");
            interpreter.execute(script.toString());
            BigDecimal bd = results[i - 1];
            BigDecimal d = getBDValue("z", state);
            assert compare(d, bd, comparisonTolerance);
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
        String import_g = "import('a:a');";
        String import_g1 = "import('a:a', 'b');";
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, g_module);
        addLine(script, import_g);
        addLine(script, import_g1);
        addLine(script, "a#q:=10;");
        addLine(script, "b#q:=11;");
        // It will ingest the function fine. It is attempting to use it later that will cause the error
        QDLParser interpreter = new QDLParser(null, state);
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
        QDLParser interpreter = new QDLParser(null, state);
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
            assert compare(d, r, comparisonTolerance);
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
        addLine(script, "import('a:a');");
        addLine(script, "import('a:b');");
        addLine(script, "d := a#list.b#i;");
        addLine(script, "e := b#list.#i;");


        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        Long d = getLongValue("d", state);
        Long e = getLongValue("e", state);
        assert d.equals(-9L);
        assert e.equals(-20l);
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
        addLine(script, "import('a:a');");
        addLine(script, "import('a:b');");
        addLine(script, "p := i;");
        addLine(script, "q := list.0;");
        addLine(script, "r := j;");
        addLine(script, "s := list2.0;");
        // Note that if we want to change the value, we need to qualify it still, however.
        // Since an unqualified name gets created in the local state, not the module.
        // May want to revisit how this is done in the design though....
        addLine(script, "a#i := 5;");


        QDLParser interpreter = new QDLParser(null, state);
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
        addLine(script, "import('a:a');");
        addLine(script, "import('b:b');");
        addLine(script, "import('a:b', 'd');");
        addLine(script, "d := d#list.b#i;");
        addLine(script, "e := b#list.d#i;");


        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        Long d = getLongValue("d", state);
        Long e = getLongValue("e", state);
        assert d.equals(1L);
        assert e.equals(-19l);
    }

    /*
    Conenience getters for testing
     */
    protected BigDecimal getBDValue(String variable, State state) {
        return (BigDecimal) state.getValue(variable);
    }

    protected Long getLongValue(String variable, State state) {
        return (Long) state.getValue(variable);
    }

    protected String getStringValue(String variable, State state) {
        return (String) state.getValue(variable);
    }

    protected Boolean getBooleanValue(String variable, State state) {
        return (Boolean) state.getValue(variable);
    }

    protected StemVariable getStemValue(String variable, State state) {
        return (StemVariable) state.getValue(variable);
    }

    protected boolean compare(BigDecimal x, BigDecimal y, BigDecimal comparisonTolerance) {
        return x.subtract(y).abs().compareTo(comparisonTolerance) < 0;
    }

    @Test
    public void testAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=5;");
        addLine(script, "b := -10;");
        addLine(script, "c:=a+b;");
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a").equals(new Long(5L));
        assert state.getValue("b").equals(new Long(-10L));
        assert state.getValue("c").equals(new Long(-5L));
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
        addLine(script, "a. := indices(6);");
        addLine(script, "b := 3;");
        addLine(script, "a.0+=b;"); // 0 + 3 = 3
        addLine(script, "a.1-=b;"); // 1 - 3 = -2
        addLine(script, "a.2*=b;"); // 2 * 6 =  6
        addLine(script, "a.3/=b;"); //   3/3 =  1
        addLine(script, "a.4%=b;"); //   4%3 =  1
        addLine(script, "a.5^=b;"); //   5^3 =  125
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a.0").equals(new Long(3L));
        assert state.getValue("a.1").equals(new Long(-2L));
        assert state.getValue("a.2").equals(new Long(6L));
        assert state.getValue("a.3").equals(new Long(1L));
        assert state.getValue("a.4").equals(new Long(1L));
        assert state.getValue("a.5").equals(new Long(125L));
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

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("q").equals("abc");
        assert state.getValue("A").equals("abc");
        assert state.getValue("B").equals("bc");
        assert state.getValue("C").equals("c");
        assert state.getValue("D").equals("c");
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
        addLine(script, "a.3 := a!=b;"); // T
        addLine(script, "a.4 := a<=a;"); //T
        addLine(script, "a.5 := a=<a;"); //T
        addLine(script, "a.6 := b>=b;"); //T
        addLine(script, "a.7 := b=>b;"); //T
        addLine(script, "a.8 := a==a;"); //T
        addLine(script, "a.9 := a!=a;"); //F
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a.0").equals(Boolean.FALSE);
        assert state.getValue("a.1").equals(Boolean.TRUE);
        assert state.getValue("a.2").equals(Boolean.FALSE);
        assert state.getValue("a.3").equals(Boolean.TRUE);
        assert state.getValue("a.4").equals(Boolean.TRUE);
        assert state.getValue("a.5").equals(Boolean.TRUE);
        assert state.getValue("a.6").equals(Boolean.TRUE);
        assert state.getValue("a.7").equals(Boolean.TRUE);
        assert state.getValue("a.8").equals(Boolean.TRUE);
        assert state.getValue("a.9").equals(Boolean.FALSE);
    }


    @Test
    public void testLogic() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a:=5;");
        addLine(script, "b:=10;");
        addLine(script, "c:=-5;");
        addLine(script, "d:=a<b&&c<a;");
        addLine(script, "e:=!(a<b&&c<a);");
        QDLParser interpreter = new QDLParser(null, state);

        interpreter.execute(script.toString());
        assert state.getValue("a").equals(new Long(5L));
        assert state.getValue("b").equals(new Long(10L));
        assert state.getValue("c").equals(new Long(-5L));
        assert state.getValue("d").equals(Boolean.TRUE);
        assert state.getValue("e").equals(Boolean.FALSE);
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a").equals(new Long(1L)); // got incremented
        assert state.getValue("b").equals(new Long(-1L)); // got decremented twice
        assert state.getValue("c").equals(new Long(2L));
        assert state.getValue("d").equals(Boolean.FALSE);
        assert state.getValue("e").equals(Boolean.TRUE);
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
        addLine(script, "say(a);");
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
    }

    /**
     * test that a language with lots of diacriticals gets handled right in encode and decoding.
     * @throws Throwable
     */
    @Test
    public void testUTF8StringDecodeEncode() throws Throwable{
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        // First line of the Vietnamese epic, the Tale of Kieu
        addLine(script, "a :='Trăm năm trong cõi người ta, Chữ tài chữ mệnh khéo là ghét nhau.';");
        addLine(script, "b := (a == vdecode(vencode(a)));");
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("b").equals(Boolean.TRUE);
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a").equals(new Long(-4L));
        assert state.getValue("b").equals(new Long(11L));
        assert state.getValue("c").equals(new Long(-3L));
        assert state.getValue("d.0").equals(new Long(0L));
        assert state.getValue("d.1").equals(new Long(-1L));
        assert state.getValue("d.2").equals(new Long(-2L));
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a").equals(new Long(1L));
        assert state.getValue("b").equals(new Long(-6L));
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a").equals(new Long(1L));
        assert state.getValue("b").equals(new Long(-24L));
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        // so the first 5 entries are true, the next 5 are false.
        for (int i = 0; i < 5; i++) {
            assert (Boolean) state.getValue("z." + i);
        }

        for (int i = 5; i < 10; i++) {
            assert !(Boolean) state.getValue("z." + i);
        }

    }

    @Test
    public void testBadAssignment() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a = 3");
        QDLParser interpreter = new QDLParser(null, state);
        try {
            interpreter.execute(script.toString());
            System.out.println(state.getValue("a"));
            assert false : "Was able to make an assignment with = not :=";
        } catch (Throwable t) {
            assert true;
        }

    }

    @Test
    public void testCheckSymbolTableAssignment() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a := 'aaa';");
        addLine(script, "a += 'qqq';");
        QDLParser interpreter = new QDLParser(null, state);
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
        addLine(script, "my_stem.help := 'this is my stem';");
        addLine(script, "list_append(my_stem., indices(5));");
        addLine(script, "say(my_stem., true);");

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

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

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a") == null;
        assert state.getValue("a.") == null;
        assert (Boolean) state.getValue("a0");
        assert (Boolean) state.getValue("a1");

    }

    @Test
    public void testExecute() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "    execute('var := \\'abc\\' + \\'def\\';');");
        addLine(script, "    say(var);");
        System.out.println("\n\n" + script.toString());
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("var").equals("abcdef");

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

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());

        assert state.getValue("a") != null;
        assert ((Long) state.getValue("a")).equals(2L);
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
        String rawJSON = "{\n" +
                " \"sub\": \"jeff\",\n" +
                " \"aud\": \"ashigaru:command.line2\",\n" +
                " \"Jäger-Groß\": \"test value\",\n" +
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
                "}\n";

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "j := '" + rawJSON + "';");
        addLine(script, "claims. := from_json(j);");
        addLine(script, "j2 := to_json(from_json(to_json(from_json(to_json(from_json(j))))));");
        QDLParser interpreter = new QDLParser(null, state);
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
     * @throws Throwable
     */
    @Test
    public void testNestedVariableImport() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "module['a:/a','a']body[q:=1;];");
        addLine(script, "import('a:/a');");
        addLine(script, "import('a:/a','b');");
        addLine(script, "module['q:/q','w']body[import('a:/a');zz:=a#q+2;];");
        addLine(script, "a#q:=10;");
        addLine(script, "b#q:=11;");
        // Make sure that some of the state has changed to detect state management issues.
        addLine(script, "import('q:/q');");
        addLine(script, "w#a#q:=3;");
        State state = testUtils.getNewState();

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a#q").equals(10L);
        assert state.getValue("b#q").equals(11L);
        assert state.getValue("w#a#q").equals(3L);

    }
      /*
      Define a function then define variants in modules.  Values are checked to track whether the state
      gets corrupted. This can be put into a QDL workspace to check manually
      
          define[f(x)]body[return(x+100);];
          module['a:/t','a']body[define[f(x)]body[return(x+1);];];
          module['q:/z','w']body[import('a:/t');define[g(x)]body[return(a#f(x)+3);];];
          import('q:/z');
          w#a#f(3)
          w#g(2)
       */
    @Test
    public void testNestedFunctionImport() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "define[f(x)]body[return(x+100);];");
        addLine(script, "module['a:/t','a']body[define[f(x)]body[return(x+1);];];");
        addLine(script, "module['q:/z','w']body[import('a:/t');define[g(x)]body[return(a#f(x)+3);];];");
        addLine(script, "test_f:=f(1);");
        addLine(script, "import('a:/t');");
        addLine(script, "test_a:=a#f(1);");
        // Make sure that some of the state has changed to detect state management issues.
        addLine(script, "import('q:/z');");
        addLine(script, "test_waf := w#a#f(2);");
        addLine(script, "test_wg := w#g(2);");
        State state = testUtils.getNewState();

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("test_f").equals(101L);
        assert state.getValue("test_a").equals(2L);
        assert state.getValue("test_waf").equals(3L);
        assert state.getValue("test_wg").equals(6L);

    }
}
