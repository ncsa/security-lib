package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
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



     /*
     What follows are a bunch of finger and toes checks for the tilde operator, initially these are to prove it
     works right, but are also the regression tests.
      */

    @Test
    public void testScalarGlom() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := 1~2~3;");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
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

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
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
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
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

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
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

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
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

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
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

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable stem = getStemValue("a.", state);
        assert stem.size() == 3;
        assert stem.getLong("0") == 1L;
        assert stem.getString("1").equals("abc");
        assert stem.getString("a").equals("b");
    }

}
