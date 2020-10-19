package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import org.junit.Test;

/**
 * tests for "glomming" i.e. using the new tilde, ~, operator.  There are apt to be a lot of these ultimately , so
 * they should get their own test.
 * <p>Created by Jeff Gaynor<br>
 * on 10/17/20 at  7:10 AM
 */
public class GlomTest extends AbstractQDLTester {

    @Test
    public void testContinuedFraction1() throws Throwable {
        //Repeat this test from same named one in the ParserTest
        // with new notation. x:= [-5/8, -5/7, -5/6, -1, -5/4]
        // and y := [8/17, 7/16, 6/19, 5/11, 3/7]
        // Aim is to show that having a bunch of hard-coded lists gets interpreted right

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
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable out1 = getStemValue("out1.", state);
        StemVariable out2 = getStemValue("out2.", state);
        assert out1.size() == 5;
        assert out2.size() == 5;
        for (long i = 0L; i < 5; i++) {
            assert areEqual(out1.getDecimal(i), out2.getDecimal(i));
        }
    }

     /*
     What follows are a bunch of finger and toes checks for the tilde operator, initially these are to prove it
     works right, but are also the regression tests.
      */

    @Test
    public void testScalarGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := 1~2~3;");

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 3;
        assert stem.getLong("0") == 1L;
        assert stem.getLong("1") == 2L;
        assert stem.getLong("2") == 3L;
    }

    @Test
    public void testArrayGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [1,2]~[3,4];");

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 4;
        assert stem.getLong("0") == 1L;
        assert stem.getLong("1") == 2L;
        assert stem.getLong("2") == 3L;
        assert stem.getLong("3") == 4L;
    }

    @Test
    public void testConcatenateGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := (1~'ab')+(3~'cd');");
        // should result in [4,abcd]
        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 2;
        assert stem.getLong("0") == 4L;
        assert stem.getString("1").equals("abcd");
    }

    @Test
    public void testMondicGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := ~1;");

        QDLParser interpreter = new QDLParser(null, state);
        try {
            interpreter.execute(script.toString());
            assert false : "Was able to execute a monadic glom ~1. It should have failed.";
        } catch (Throwable t) {
            assert true;
        }
    }

    @Test
    public void testStemGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := {'a':'p'}~{'b':'r'};");

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 2;
        assert stem.getString("a").equals("p");
        assert stem.getString("b").equals("r");
    }

        /**
         * In this test, two stems with the same keys are glommed. The contract is that the one on the right
         * has the final say.
         * @throws Throwable
         */
        @Test
    public void testNonUniqueStemGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := {'a':'p'}~{'a':'r'};");

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 1;
        assert stem.getString("a").equals("r");
    }
        @Test
    public void testMixedGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := 1~'abc'~{'a':'b'};");

        QDLParser interpreter = new QDLParser(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 3;
        assert stem.getLong("0") == 1L;
        assert stem.getString("1").equals("abc");
        assert stem.getString("a").equals("b");
    }

}
