package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.LogicBlock;
import edu.uiuc.ncsa.security.util.functor.logic.*;
import edu.uiuc.ncsa.security.util.functor.strings.jToLowerCase;
import edu.uiuc.ncsa.security.util.functor.strings.jToUpperCase;
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
     public void testTrue() throws Exception{
        jTrue jTrue= new jTrue();
        assert !jTrue.isExecuted();
        jTrue.execute();
        assert jTrue.getBooleanResult() : "jTrue test fails";

     }

    @Test
     public void testFalse() throws Exception{
        jFalse jFalse = new jFalse();
        assert !jFalse.isExecuted();
        jFalse.execute();
        assert !jFalse.getBooleanResult();
     }

    @Test
    public void testContains() throws Exception {
        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("zfoo");
        jContains.execute();
        assert jContains.getBooleanResult() : "contains test fails";
    }

    @Test
    public void testNotContains() throws Exception {
        jContains jContains = new jContains();
        jContains.addArg("foo");
        jContains.addArg("argle");
        jContains.execute();
        assert !jContains.getBooleanResult() : "test where A not a substring of B should fail and it passed.";
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
    }

    @Test
    public void testBadMatch() throws Exception {
        jMatch jMatch = new jMatch();
        jMatch.addArg("fox");
        jMatch.addArg("fox1");
        jMatch.execute();
        assert !jMatch.getBooleanResult();
    }

    @Test
    public void testEndsWith() throws Exception {
        jEndsWith ff = new jEndsWith();
        ff.addArg("the quick brown fox");
        ff.addArg(" fox");
        ff.execute();
        assert ff.getBooleanResult();
    }

    @Test
    public void testBadEndsWith() throws Exception {
        jEndsWith ff = new jEndsWith();
        ff.addArg("the quick brown fox");
        ff.addArg(" fox!");
        ff.execute();
        assert !ff.getBooleanResult();
    }

    @Test
    public void testStartsWith() throws Exception {
        jStartsWith ff = new jStartsWith();
        ff.addArg("the quick brown fox");
        ff.addArg("the ");
        ff.execute();

        assert ff.getBooleanResult();
    }

    @Test
    public void testBadStartsWith() throws Exception {
        jEndsWith ff = new jEndsWith();
        ff.addArg("the quick brown fox");
        ff.addArg("he");
        ff.execute();
        assert !ff.getBooleanResult();
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
        System.out.println(ff.toJSON().toString(2));
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
    }

    public void testExists() throws Exception {
        jExists ff = new jExists();
        ff.addArg("");
        ff.execute();
        assert ff.getBooleanResult();
        ff.reset();
        ff.addArg((String) null);
        ff.execute();
        assert ff.getBooleanResult();
        ff.reset();
        ff.addArg(" ");
        ff.execute();
        assert !ff.getBooleanResult();
    }

    public void testCase() throws Exception {
        jToLowerCase jToLowerCase = new jToLowerCase();
        String testString = "THE QUICK BROWN FOX";
        jToLowerCase.addArg(testString);
        jToLowerCase.execute();
        assert jToLowerCase.getStringResult().equals(testString.toLowerCase());

        testString = "jumped over the lazy dog";
        jToUpperCase jToUpperCase = new jToUpperCase();
        jToUpperCase.addArg(testString);
        jToUpperCase.execute();
        assert jToUpperCase.getStringResult().equals(testString.toUpperCase());
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
        ifBlock.put("$if", jContains.toJSON() );


        jToLowerCase jToLowerCase = new jToLowerCase();
        String testString = "THE QUICK BROWN FOX";
        jToLowerCase.addArg(testString);
        jToLowerCase.execute();
        assert jToLowerCase.getStringResult().equals(testString.toLowerCase());

        ifBlock.put("$then", jToLowerCase.toJSON());
        array.add(ifBlock);
        List<LogicBlock> bloxx = functorFactory.createLogicBlock(array);
        assert bloxx.size() == 1;
        bloxx.get(0).execute();
        System.out.println(bloxx.get(0).getResults());
        assert testString.toLowerCase().equals(bloxx.get(0).getResults().get(0));
    }
}
