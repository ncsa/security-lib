package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
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
     * f(3, -5) ==   1.37037037037037
     * f(3/2, -5/2) ==  1.87793427230047
     * f(3/3, -5/3) ==   1.69230769230769
     * f(3/4, -5/4) ==  1.39375629405841
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
            assert compare(c, bd, new BigDecimal(".0000000000001"));
        }


    }

    /**
     * Test the rational function of three variables <br/><br/>
     * f(x,y,z) = (x*y^2*z^3 - x/y^2 + z^4)/(1-(x*y*(1-z))^2)
     * <p>
     * In[48]:= For[i=1,i<5,i++,Print[N[q2[-5/i,3/i,5/i ],15]]]
     * f(-5, 3, 5) == 1.38912043468865
     * f(-5/2, 3/2, 5/2) == 1.55731202901014
     * f(-5/3, 1, 5/3) == -7.10526315789474
     * f(-5/4, 3/4, 5/4) == 3.48158672751801
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
            assert compare(d, bd, new BigDecimal(".0000000000001"));
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
            assert compare(d, BigDecimal.ZERO, new BigDecimal(".0000000000001"));
        }


    }

    /**
     * We test the Fibonacci continued (partial) fraction
     *  f(x):=1+(1/(1+1/(1+1/(1+1/(x^2+1)))))
     *
     *   = (8 + 5*x^2)/(5 + 3*x^2)
     *
     *                 2
     *          8 + 5 x
     *       = --------
     *                 2
     *          5 + 3 x

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
            addLine(script, "return(" + cf  + ");");
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
                BigDecimal d = (BigDecimal) state.getValue("z");
                BigDecimal r = results[i-1];
                assert compare(d, r, new BigDecimal(".0000000000001"));
            }


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
     * In order to handle "-expression" in the parser, there has to be a wrapper. This'
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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        assert state.getValue("a").equals(new Long(-4L));
        assert state.getValue("b").equals(new Long(11L));
        assert state.getValue("c").equals(new Long(-3L));
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
        // This tests multiple decrements. NOTE that the values are multipled as it decrements,
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
        addLine(script, "w. := to_list(10);");
        addLine(script, "z. := has_keys(var., w.;");
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
    public void testExecute() throws Throwable {

        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "execute('var := \\'abc\\' + \\'def\\');'");
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        System.out.println(state.getValue("var"));
        assert state.getValue("var").equals("abcdef");

    }
}
