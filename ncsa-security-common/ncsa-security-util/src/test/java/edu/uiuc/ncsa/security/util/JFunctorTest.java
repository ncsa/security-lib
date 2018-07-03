package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.functor.*;
import edu.uiuc.ncsa.security.util.functor.logic.*;
import edu.uiuc.ncsa.security.util.functor.strings.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  11:34 AM
 */
public class JFunctorTest extends TestBase {

    @Test
    public void testTrue() throws Exception {
        jTrue jTrue = new jTrue();
        assert !jTrue.isExecuted();
        jTrue.execute();
        assert jTrue.getBooleanResult() : "jTrue test fails";
        assert reTestIt(jTrue).getBooleanResult() : "jTrue test fails";
    }

    /**
     * Takes an existing functor -- one created manually -- and recreates it from the raw JSON and the factory,
     * then executes it and returns it. You supply the functor and re-run your assertions afainst the returned
     * functor. This shows the factory knows about the functor and can reliably deserialize it from JSON.
     * <b>NOTE</b> this works for {@link JFunctorImpl} inheritors only!
     *
     * @param x
     * @return
     * @throws Exception
     */
    protected JFunctorImpl reTestIt(JFunctor x) throws Exception {
        return reTestIt(x, new JFunctorFactory());
    }

    protected JFunctorImpl reTestIt(JFunctor x, JFunctorFactory ff) throws Exception {
        JFunctorImpl y = (JFunctorImpl) ff.fromJSON(x.toJSON());
        y.execute();
        return y;
    }


    @Test
    public void testFalse() throws Exception {
        jFalse jFalse = new jFalse();
        assert !jFalse.isExecuted();
        jFalse.execute();
        assert !jFalse.getBooleanResult();
        assert !reTestIt(jFalse).getBooleanResult();
    }

    @Test
    public void testContains() throws Exception {
        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");
        jContains.execute();
        assert jContains.getBooleanResult() : "contains test fails";
        assert reTestIt(jContains).getBooleanResult() : "contains test fails";
    }

    @Test
    public void testNotContains() throws Exception {
        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("argle");
        jContains.execute();
        assert !jContains.getBooleanResult() : "test where A not a substring of B should fail and it passed.";
        assert !reTestIt(jContains).getBooleanResult() : "test where A not a substring of B should fail and it passed.";
    }

    @Test
    public void testContainsHasBadArgs() throws Exception {
        jContains jContains = new jContains();
        jContains.addArg("foo");
        try {
            jContains.execute();
            assert false : "Error: incorrect number of arguments accepted. Should fail";
        } catch (IllegalStateException isx) {
            assert true;
        }
    }

    @Test
    public void testMatch() throws Exception {
        jMatch jMatch = new jMatch();
        jMatch.addArg("fox");
        jMatch.addArg("fox");
        jMatch.execute();
        assert jMatch.getBooleanResult();
        assert reTestIt(jMatch).getBooleanResult();
    }


    @Test
    public void testEquals() throws Exception {
        jEquals jEquals = new jEquals();
        jEquals.addArg("fox");
        jEquals.addArg("fox");
        jEquals.execute();
        assert jEquals.getBooleanResult();
        assert reTestIt(jEquals).getBooleanResult();
        jEquals.reset();
        // screw up case
        jEquals.addArg("fox");
        jEquals.addArg("Fox");
        jEquals.execute();
        assert !jEquals.getBooleanResult();
        assert !reTestIt(jEquals).getBooleanResult();
        jEquals.reset();
        // edge case
        jEquals.addArg((String) null);
        jEquals.addArg((String) null);
        jEquals.execute();
        assert jEquals.getBooleanResult();
        // NOTE no reTestIt here since rendering null into JSON does not really work -- the library does screwy things with
        // the edge case of a JSON array of nulls.

    }

    @Test
    public void testBadMatch() throws Exception {
        jMatch jMatch = new jMatch();
        jMatch.addArg("fox");
        jMatch.addArg("fox1");
        jMatch.execute();
        assert !jMatch.getBooleanResult();
        assert !reTestIt(jMatch).getBooleanResult();
    }

    @Test
    public void testEndsWith() throws Exception {
        jEndsWith ff = new jEndsWith();
        ff.addArg("the quick brown fox");
        ff.addArg(" fox");
        ff.execute();
        assert ff.getBooleanResult();
        assert reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testBadEndsWith() throws Exception {
        jEndsWith ff = new jEndsWith();
        ff.addArg("the quick brown fox");
        ff.addArg(" fox!");
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testStartsWith() throws Exception {
        jStartsWith ff = new jStartsWith();
        ff.addArg("the quick brown fox");
        ff.addArg("the ");
        ff.execute();

        assert ff.getBooleanResult();
        assert reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testReplace() throws Exception {
        jReplace jReplace = new jReplace();
        String testString = "mairzy dotes and dozey dotes";
        jReplace.addArg(testString);
        jReplace.addArg("stoats");
        jReplace.addArg("dotes");
        jReplace.execute();
        assert jReplace.isExecuted();
        testString.replace("dotes", "stoats");
        assert jReplace.getStringResult().equals(testString);
        assert reTestIt(jReplace).getStringResult().equals(testString);

    }

    /**
     * Note that this tests concatenation and it does so by passing in another concatenation functor, showing that
     * functors can be used with this as well.
     *
     * @throws Exception
     */
    @Test
    public void testConcat() throws Exception {
        jConcat jc = new jConcat();
        jConcat jc2 = new jConcat();
        jc.addArg("the");
        jc.addArg(" ");
        jc.addArg("quick");
        jc.addArg(" ");
        jc2.addArg("brown");
        jc2.addArg(" ");
        jc2.addArg("fox");
        jc.addArg(jc2);
        jc.execute();
        assert jc.getStringResult().equals("the quick brown fox");
        assert reTestIt(jc).getStringResult().equals("the quick brown fox");
    }

    @Test
    public void testBadStartsWith() throws Exception {
        jEndsWith ff = new jEndsWith();
        ff.addArg("the quick brown fox");
        ff.addArg("he");
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testAnd() throws Exception {
        jAnd ff = new jAnd();
        jEndsWith jEndsWith = new jEndsWith();
        jEndsWith.addArg("the quick brown fox");
        jEndsWith.addArg(" fox");

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");

        ff.addArg(jEndsWith);
        ff.addArg(jContains);
        ff.execute();
        assert ff.getBooleanResult();
        assert reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testBadAnd() throws Exception {
        jAnd ff = new jAnd();
        jEndsWith jEndsWith = new jEndsWith();
        jEndsWith.addArg("the quick brown fox");
        jEndsWith.addArg("he ");

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");

        ff.addArg(jEndsWith);
        ff.addArg(jContains);
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testNot() throws Exception {
        jNot ff = new jNot();

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");

        ff.addArg(jContains);
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testOr() throws Exception {
        jOr ff = new jOr();
        jEndsWith jEndsWith = new jEndsWith();
        jEndsWith.addArg("the quick brown fox");
        jEndsWith.addArg("he ");

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");

        ff.addArg(jEndsWith);
        ff.addArg(jContains);
        ff.execute();
        assert ff.getBooleanResult();
        assert reTestIt(ff).getBooleanResult();
    }

    @Test
    public void testXOr() throws Exception {
        jXOr ff = new jXOr();
        // first is false, second is true, so this should reflect that
        jEndsWith jEndsWith = new jEndsWith();
        jEndsWith.addArg("the quick brown fox");
        jEndsWith.addArg("he ");

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");

        ff.addArg(jEndsWith);
        ff.addArg(jContains);
        ff.execute();
        assert ff.getBooleanResult();
        assert reTestIt(ff).getBooleanResult();
        assert ff.getIndex() != -1;
        Object obj = ff.getArgs().get(ff.getIndex());
        assert obj instanceof jContains;
        assert ((jContains) obj).getBooleanResult();

        assert ff.getArgs().get(0) instanceof jEndsWith;
        assert !((jEndsWith) ff.getArgs().get(0)).getBooleanResult();


    }

    @Test
    public void testNeither() throws Exception {
        // Neither is true, so result should be false
        jOr ff = new jOr();
        jEndsWith jEndsWith = new jEndsWith();
        jEndsWith.addArg("the quick brown fox");
        jEndsWith.addArg("he ");

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zzzz");

        ff.addArg(jEndsWith);
        ff.addArg(jContains);
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
    }

    public void testIf() throws Exception {
        jIf jif = new jIf();

        jAnd ff = new jAnd();
        jEndsWith jEndsWith = new jEndsWith();
        jEndsWith.addArg("the quick brown fox");
        jEndsWith.addArg(" fox");

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");

        ff.addArg(jEndsWith);
        ff.addArg(jContains);
        jif.addArg(ff);
        jif.execute();
        assert jif.getBooleanResult();
        assert reTestIt(jif).getBooleanResult();
    }

    public void testExists() throws Exception {
        jExists ff = new jExists();
        // no args means it is not there
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
        ff.reset();
        // test for null string
        ff.addArg((String) null);
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();
        ff.reset();

        // test for empty string
        ff.addArg("");
        ff.execute();
        assert !ff.getBooleanResult();
        assert !reTestIt(ff).getBooleanResult();

        ff.reset();
        // test for blank string (which
        ff.addArg(" ");
        ff.execute();
        assert ff.getBooleanResult();
        assert reTestIt(ff).getBooleanResult();
    }

    public void testCase() throws Exception {
        jToLowerCase jToLowerCase = new jToLowerCase();
        String testString = "THE QUICK BROWN FOX";
        jToLowerCase.addArg(testString);
        jToLowerCase.execute();
        assert jToLowerCase.getStringResult().equals(testString.toLowerCase());
        assert reTestIt(jToLowerCase).getStringResult().equals(testString.toLowerCase());

        testString = "jumped over the lazy dog";
        jToUpperCase jToUpperCase = new jToUpperCase();
        jToUpperCase.addArg(testString);
        jToUpperCase.execute();
        assert jToUpperCase.getStringResult().equals(testString.toUpperCase());
        assert reTestIt(jToUpperCase).getStringResult().equals(testString.toUpperCase());
    }

    @Test
    public void testLBCreation() throws Exception {
        JFunctorFactory functorFactory = new JFunctorFactory();
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject ifBlock = new JSONObject();

        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");
        ifBlock.put("$if", jContains.toJSON());


        jToLowerCase jToLowerCase = new jToLowerCase();
        String testString = "THE QUICK BROWN FOX";
        jToLowerCase.addArg(testString);
        jToLowerCase.execute();
        assert jToLowerCase.getStringResult().equals(testString.toLowerCase());

        ifBlock.put("$then", jToLowerCase.toJSON());
        array.add(ifBlock);
        JSONObject j = new JSONObject();
        j.put(FunctorTypeImpl.OR.getValue(), array);
        LogicBlocks<? extends LogicBlock> bloxx = functorFactory.createLogicBlock(j);
        assert bloxx instanceof ORLogicBlocks;
        assert bloxx.size() == 1;
        LogicBlock logicBlock = bloxx.get(0);
        logicBlock.execute();
        assert testString.toLowerCase().equals(logicBlock.getResults().get(0));

        // Check that clearing state works as it should.
        logicBlock.clearState();
        assert !logicBlock.isExecuted();
        assert logicBlock.getResults().isEmpty();
        assert !logicBlock.getThenBlock().isExecuted();

        logicBlock.execute();
        assert testString.toLowerCase().equals(logicBlock.getResults().get(0));

    }

    /**
     * Tests the case that the if and then functors are supplied in a constructor so that no parsing is needed to start the
     * execution.
     *
     * @throws Exception
     */

    @Test
    public void testLBCreation2() throws Exception {
        JFunctorFactory ff = new JFunctorFactory();

        jIf jif = new jIf();
        jif.addArg(ff.create("{\"$equals\":[\"foo\",\"foo\"]}"));

        ;
        jThen jthen = new jThen();
        jthen.addArg(ff.create("{\"$toUpperCase\":[\"foo\"]}"));

        jElse jelse = new jElse();
        jelse.addArg(ff.create("{\"$toUpperCase\":[\"bar\"]}"));
        LogicBlock logicBlock = new LogicBlock(ff, jif, jthen, jelse);
        logicBlock.execute();
        System.out.println(logicBlock.toJSON());
        // The logic block's result is the same as the if clause.
        assert (Boolean) logicBlock.getResult();
        // since the cre can be an array of possible consequents, the result is an array that has
        // to be unpacked a wee bit.
        Object result = logicBlock.getConsequent().getResult();
        List<String> results = (List<String>) result;
        assert results.get(0).equals("FOO");
        System.out.println(logicBlock.toJSON());
        System.out.println(logicBlock);

    }

    @Test
    public void testDrop() throws Exception {
        jDrop jDrop = new jDrop();
        jDrop.addArg("ab");
        jDrop.addArg("abc");
        jDrop.execute();
        assert jDrop.getResult().equals("c");
        assert reTestIt(jDrop).getResult().equals("c");


        jDrop = new jDrop();
        jDrop.addArg("ab");
        jDrop.addArg("abcancdeabab");
        jDrop.execute();
        assert jDrop.getResult().equals("cancde");
        assert reTestIt(jDrop).getResult().equals("cancde");


        jDrop = new jDrop();
        jDrop.addArg("@bigstate.edu");
        jDrop.addArg("bob.smith@bigstate.edu");
        jDrop.execute();
        assert jDrop.getResult().equals("bob.smith");
        assert reTestIt(jDrop).getResult().equals("bob.smith");
    }
}
