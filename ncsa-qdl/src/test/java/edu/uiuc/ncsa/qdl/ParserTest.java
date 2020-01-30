package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.parsing.QDLParser;
import edu.uiuc.ncsa.qdl.state.State;
import org.junit.Test;

/**
 * A class for testing the parser. Write little scripts, test that the state is what is should be.
 * This is to let us know if, for instance, changing the lexer blows up.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  11:27 AM
 */
public class ParserTest extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

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
