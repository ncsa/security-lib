package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.core.exceptions.FunctorRuntimeException;
import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.AbstractHandler;
import edu.uiuc.ncsa.security.util.functor.parser.FunctorScript;
import edu.uiuc.ncsa.security.util.functor.parser.event.ConditionalHandler;
import edu.uiuc.ncsa.security.util.functor.parser.event.EventDrivenParser;
import edu.uiuc.ncsa.security.util.functor.parser.event.FunctorHandler;
import edu.uiuc.ncsa.security.util.functor.parser.event.SwitchHandler;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/12/18 at  4:52 PM
 */
public class FunctorParserTest extends TestBase {
    protected JFunctorFactory createFunctorFactory() {
        return createFunctorFactory(null);
    }

    protected JFunctorFactory createFunctorFactory(Object initObject) {
        return new JFunctorFactory();
    }

    /**
     * tests the and functor, which requires that multiple arguments be parsed correctly then evaluated.
     *
     * @throws Exception
     */
    @Test
    public void testAND() throws Exception {
        String testString = "and(endsWith('the quick brown fox','fox'),contains('foo','zfoo'))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        AbstractHandler abstractHandler = eventDrivenParser.parse(testString);
        assert abstractHandler.getHandlerType() == AbstractHandler.FUNCTOR_TYPE;
        FunctorHandler eh = (FunctorHandler) abstractHandler;
        assert (Boolean) eh.getFunctor().getResult();
    }

    /**
     * Test that basic logib block parsing works.
     *
     * @throws Exception
     */
    @Test
    public void testLB() throws Exception {
        String testString = "if[match('B','B')]then[toLowerCase('Z')]";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
        ConditionalHandler conditionalHandler = (ConditionalHandler) parser.parse(testString);
        assert conditionalHandler.getLogicBlock().getResults().size() == 1;
        assert conditionalHandler.getLogicBlock().getConsequent().getListResult().get(0).equals("z");
    }

/*    @Test
    public void testOLDLB() throws Exception {
        String testString = "if[match('B','B')]then[toLowerCase('Z')]";
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        FunctorHandler fh = new FunctorHandler(functorFactory);
        ConditionalHandler lbh = new ConditionalHandler(fh, functorFactory);
        parser.addBracketListener(lbh);
        parser.addCommaListener(lbh);
        parser.parse(testString);
        lbh.getLogicBlock().execute();
        // Remember that logic blocks can have lists of functors to execute, so the result is an array of each result.
        assert lbh.getLogicBlock().getResults().size() == 1;
        lbh.getLogicBlock().getResults().get(0).equals("z");
    }*/

    /**
     * Tests that having multiple functors in the consequent is correctly interpreted
     *
     * @throws Exception
     */
    @Test
    public void testLBMultipleThenClause() throws Exception {
        String testString = "if[match('B','B')]then[toLowerCase('Z'),toUpperCase('a'), toLowerCase('BcD')]";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
        ConditionalHandler conditionalHandler = (ConditionalHandler) parser.parse(testString);
        List list = conditionalHandler.getLogicBlock().getConsequent().getListResult();
        assert list.size() == 3;
        assert list.get(0).equals("z");
        assert list.get(1).equals("A");
        assert list.get(2).equals("bcd");

    }

    /**
     * Test that a conditional with an else clause is parsed correctly
     *
     * @throws Exception
     */
    @Test
    public void testLBElseClause() throws Exception {
        String testString = "if[match('B','C')]then[toLowerCase('Z'),toUpperCase('a')]else[toLowerCase('BcD')]";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
        ConditionalHandler conditionalHandler = (ConditionalHandler) parser.parse(testString);

        // Remember that logic blocks can have lists of functors to execute, so the result is a list of each result.
        List list = conditionalHandler.getLogicBlock().getConsequent().getListResult();
        assert list.size() == 1;
        assert list.get(0).equals("bcd");
    }

    /**
     * Test that functor composition is handled correctly.
     *
     * @throws Exception
     */
    @Test
    public void testFunctorComposition() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        String testString = "toUpperCase(toLowerCase(toUpperCase('a')))";

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFunctor().getResult().equals("A");

    }

    /**
     * Another test that functor arguments are handled correctly
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        String testString = "drop(toUpperCase('a'),toUpperCase('abcda'))";

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("BCD");
    }

    /**
     * Test that a collection of string only arguments is handled correctly
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerStringArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        String testString = "concat('a','b','c','d')";

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("abcd");
    }

/* Really low level way to use the parse. kept for now for reference.

  @Test
    public void testOLDFHandlerMultipleArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        FunctorHandler functorHandler = new FunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(functorHandler);
        eventDrivenParser.addDoubleQuoteListener(functorHandler);
        eventDrivenParser.addCommaListener(functorHandler);

        String testString = "concat('a','b','c','d')";

        eventDrivenParser.parse(testString);
        functorHandler.getFunctor().execute();
    }*/

    /**
     * Test that a string or functor arguments is handled correctly
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerMultipleArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        String testString = "concat(toLowerCase('A'),toLowerCase('B'),toLowerCase('C'),toLowerCase('d'))";

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("abcd");
    }

    /**
     * This tests that the parser can keep straight when commas are inside different arguments
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerCommas() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        String testString = "concat('a',concat('b',concat('c','d')))";

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("abcd");
    }

    @Test
    public void testFHandler2() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());

        String testString = "drop('A',toUpperCase('abcda'))";

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("BCD");
    }

    @Test
    public void testFHandlerWhitespace() throws Exception {
        String testString = "drop(\n" +
                "     'A',\n" +
                "     toUpperCase(\n" +
                "            'abcda  '\n" +
                "     )\n" +
                ")";
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());


        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("BCD  ");
    }

    /**
     * Test that running the parser against the handler works, then changing the input
     * can reuse the handler again.
     *
     * @throws Exception
     */
    @Test
    public void testFHandler2Reset() throws Exception {
        String testString = "drop('A',toUpperCase('abcda'))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());
        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFunctor().getResult().equals("BCD");


        eventDrivenParser.reset();
        testString = "drop('A',toUpperCase('aspqra'))";
        functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFunctor().getResult().equals("SPQR");
    }


    /**
     * This does a simple logic block test of a single conditional. Note that the conditional uses the special
     * functor "true" which must be edge cased.
     *
     * @throws Exception
     */
    @Test
    public void testSimpleLogicBlocks() throws Exception {
        String testString = "and{if[true]then[toLowerCase('ABC')]}";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());

        parser.parse(testString);
        SwitchHandler lbs = parser.getSwitchHandler();
        assert lbs.getLogicBlocks().getFunctorMap().size() == 1;
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).size() == 1;
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).get(0).getResult().equals("abc");
    }


 /*   @Test
       public void testOLDSimpleLogicBlocks() throws Exception {
           String testString = "and{if[true]then[toLowerCase('ABC')]}";
           EventDrivenParser parser = new EventDrivenParser();
           JFunctorFactory functorFactory = createFunctorFactory();
           FunctorHandler fh = new FunctorHandler(functorFactory);
           ConditionalHandler lbh = new ConditionalHandler(fh, functorFactory);
           SwitchHandler edlbs = new SwitchHandler(lbh, functorFactory);
           parser.addBraceListener(edlbs);
           parser.addCommaListener(edlbs);
           parser.parse(testString);
           edlbs.getLogicBlocks().execute();
           assert edlbs.getLogicBlocks().getFunctorMap().size() == 1;
           assert edlbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).size() == 1;
           assert edlbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).get(0).getResult().equals("abc");
       }*/

    /**
     * This does a simple logic block test of a single conditional. Note that the conditional uses the special
     * functor "true" which must be edge cased.
     *
     * @throws Exception
     */
    @Test
    public void testANDLogicBlocks() throws Exception {
        String testString = "and{if[true]then[toLowerCase('ABC')],if[true]then[toLowerCase('DEF')]}";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());

        parser.parse(testString);
        SwitchHandler lbs = parser.getSwitchHandler();
        lbs.getLogicBlocks().execute();
        assert lbs.getLogicBlocks().getFunctorMap().size() == 1; // in the functor map, both these have the same key, toLowerCase
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).size() == 2;
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).get(0).getResult().equals("abc");
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).get(1).getResult().equals("def");
    }

    /**
     * tests that nested functors can be resolved. E.g. A(B(X),Y(Z)) should resolve correctly at the comma.
     */
    @Test
    public void testFunctorComma() throws Exception {
        String testString = "      and(" +
                "          contains(toLowerCase('FOO'),'zfoo')," +
                "         endsWith('abc','c')" +
                "        )";
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert (Boolean) functorHandler.getFResult();
    }

    /**
     * This hits up that the functor parser can handle depth (lots of nesting) and
     * breadth (lots of arguments) and keep them straight.
     *
     * @throws Exception
     */
    @Test
    public void testFunctorDepthAndBreadth() throws Exception {
        String testString = "concat('a',concat('b',concat('c',toUpperCase('d'))),toLowerCase('EFG'),toUpperCase(drop('Q',concat('hQ','iQ','Qj'))))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("abcDefgHIJ") : "Got " + functorHandler.getFResult();
    }

    /**
     * Characters may be escaped if they are preceeded with a backslash. \. This allows you to e.g. concatenate strings
     * that contain what are otherwise control characters like [, ( and {.
     *
     * @throws Exception
     */
    @Test
    public void testescapeCharacter() throws Exception {
        String testString = "concat('\\'\\'\\{\\}',concat('\\[\\]',concat('\\(\\)',toUpperCase('d'))),toLowerCase('EFG'),toUpperCase(drop('Q',concat('hQ','iQ','Qj'))))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser(createFunctorFactory());

        FunctorHandler functorHandler = (FunctorHandler) eventDrivenParser.parse(testString);
        assert functorHandler.getFResult().equals("''{}[]()DefgHIJ") : "Got " + functorHandler.getFResult();
    }

    @Test
    public void testORSwitch() throws Exception {
//                String testString = "if[contains('foo','zfoo')]then[if[endsWith('abc','c')]then[concat('eppn','A')]]";
        String testString = "" +
                " or{" +
                "   if[" +
                "      and(" +
                "          contains(toLowerCase('FOO'),'zfoo')," +
                "         endsWith('abc','c')" +
                "        )" +
                "     ]then[" +
                "      concat('p','q')" +
                "     ]," +
                "   if[" +
                "    and(" +
                "        contains('foo','zfoo')," +
                "       endsWith('abc','d')" +
                "      )" +
                "    ]then[" +
                "  concat('x','y')" +
                "   ]" +
                " }";
        // All this should result in the string "pq"
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());

        SwitchHandler switchHandler = (SwitchHandler) parser.parse(testString);

        SwitchHandler lbh = parser.getSwitchHandler();
        assert lbh.getResultAt(0, 0).equals("pq");
    }

    @Test
    public void testJSONSwitch() throws Exception {
        //                String testString = "if[contains('foo','zfoo')]then[if[endsWith('abc','c')]then[concat('eppn','A')]]";
        JSONObject jsonScript = new JSONObject();
        JSONArray array = new JSONArray();
        array.add("# Comment that ought to be ignored.");
        array.add(" or{");
        array.add("   if[");
        array.add("      and(");
        array.add("          contains(toLowerCase('FOO'),'zfoo'),");
        array.add("         endsWith('abc','c')");
        array.add("        )");
        array.add("     ]then[");
        array.add("      concat('p','q')");
        array.add("     ],");
        array.add("   if[");
        array.add("    and(");
        array.add("        contains('foo','zfoo'),");
        array.add("       endsWith('abc','d')");
        array.add("      )");
        array.add("    ]then[");
        array.add("  concat('x','y')");
        array.add("   ]");
        array.add(" };");
        jsonScript.put("script", array);
        FunctorScript script = new FunctorScript(createFunctorFactory(), jsonScript);
        script.execute();
        assert script.getHandlers().size() == 1;
        SwitchHandler switchHandler = (SwitchHandler) script.getHandlers().get(0);
        assert switchHandler.getResultAt(0, 0).equals("pq");
    }

    /**
     * Just like the above, except the very final ; is missing, which should cause the script to throw an exception.
     * @throws Exception
     */

    @Test
      public void testJSONBADSwitch() throws Exception {
          //                String testString = "if[contains('foo','zfoo')]then[if[endsWith('abc','c')]then[concat('eppn','A')]]";
          JSONObject jsonScript = new JSONObject();
          JSONArray array = new JSONArray();
          array.add("# Comment that ought to be ignored.");
          array.add(" or{");
          array.add("   if[");
          array.add("      and(");
          array.add("          contains(toLowerCase('FOO'),'zfoo'),");
          array.add("         endsWith('abc','c')");
          array.add("        )");
          array.add("     ]then[");
          array.add("      concat('p','q')");
          array.add("     ],");
          array.add("   if[");
          array.add("    and(");
          array.add("        contains('foo','zfoo'),");
          array.add("       endsWith('abc','d')");
          array.add("      )");
          array.add("    ]then[");
          array.add("  concat('x','y')");
          array.add("   ]");
          array.add(" }"); // <--- ONLY difference with previous test initialization.
          jsonScript.put("script", array);
          FunctorScript script = new FunctorScript(createFunctorFactory(), jsonScript);
        try {
            script.execute();
            assert false : "Missing line termination should have caused reading JSON to fail.";
        }catch(Throwable s){
            assert true;
        }

      }

    /**
     * Test that running the parser against the handler works, and that the returned handler can resolve its type.
     *
     * @throws Exception
     */
    @Test
    public void testLBHandler() throws Exception {
        String testString = "drop('A',toUpperCase('abcda'))";

        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
        AbstractHandler abstractHandler = parser.parse(testString);
        assert abstractHandler.getHandlerType() == AbstractHandler.FUNCTOR_TYPE;
        FunctorHandler functorHandler = (FunctorHandler) abstractHandler;
        assert functorHandler.getFResult().equals("BCD") : "Got " + functorHandler.getFResult();


    }

    /**
     * Take a simple expression and the parser interpet it correctly..
     *
     * @throws Exception
     */
    @Test
    public void testSimpleFunctor1() throws Exception {
        String raw = "concat(toLowerCase('FOO'),'bar')";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());

        FunctorHandler functorHandler = (FunctorHandler) parser.parse(raw);
        assert functorHandler.getFResult().equals("foobar");

    }

    /**
     * Check that accessing the result via the functor map has the functor map populated correctly
     *
     * @throws Exception
     */
    @Test
    public void testFunctorMap() throws Exception {
        String raw = "if[true]then[concat(toLowerCase('FOO'),'bar')]";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
        AbstractHandler abstractHandler = parser.parse(raw);
        assert abstractHandler.getHandlerType() == AbstractHandler.CONDITIONAL_TYPE;
        ConditionalHandler conditionalHandler = (ConditionalHandler) abstractHandler;
        assert conditionalHandler.getLogicBlock().getConsequent().getFunctorMap().containsKey(FunctorTypeImpl.CONCAT.getValue());
        assert conditionalHandler.getLogicBlock().getConsequent().getFunctorMap().get(FunctorTypeImpl.CONCAT.getValue()).get(0).getResult().equals("foobar");

    }

    @Test
    public void testNoElse() throws Exception {
        String raw = "if[false]then[concat(toLowerCase('FOO'),'bar')]";
        EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
        AbstractHandler abstractHandler = parser.parse(raw);
        assert abstractHandler.getHandlerType() == AbstractHandler.CONDITIONAL_TYPE;
        ConditionalHandler conditionalHandler = (ConditionalHandler) abstractHandler;
        assert !conditionalHandler.getLogicBlock().hasConsequent();

    }

    /**
     * test that if a condition is met that raises an error, the error is created and propagates as it should.
     * This also shows it works in scripting too.
     * @throws Exception
     */
    @Test
     public void testError() throws Exception {
        String message = "error " + Long.toHexString(System.currentTimeMillis());
         String testString = "if[match('A','B')]then[toLowerCase('Z')]else[raiseError('" + message+ "')]";
         EventDrivenParser parser = new EventDrivenParser(createFunctorFactory());
         try {
            ConditionalHandler conditionalHandler = (ConditionalHandler) parser.parse(testString);
            assert false : "Did not raise errpor in test";
         }catch(FunctorRuntimeException frx){
             assert frx.getMessage().equals(message);
         }
     }

    @Test
    public void testScript1() throws Exception {
        File f = new File("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/test/resources/test-script1.cmd");
        if (!f.exists()) {
            System.out.print("Warning: test file '" + f.getAbsolutePath() + "' does not exist. Skipping test");
        }
        FileReader fileReader = new FileReader(f);
        JFunctorFactory functorFactory = createFunctorFactory();
        functorFactory.setVerboseOn(true); //enable output to console.
        FunctorScript script = new FunctorScript(functorFactory);
        script.execute(fileReader);
        assert functorFactory.getEnvironment().containsKey("key0") : "Missing key";
        assert functorFactory.getEnvironment().get("key0").equals("value0") : "Incorrect sotred value. Expected \"value0\" and got \"" + functorFactory.getEnvironment().get("key0") + "\"";
    }
    @Test
       public void testScript2() throws Exception {
           File f = new File("/home/ncsa/dev/ncsa-git/security-lib/ncsa-security-common/ncsa-security-util/src/test/resources/test-script2.cmd");
           if (!f.exists()) {
               System.out.print("Warning: test file '" + f.getAbsolutePath() + "' does not exist. Skipping test");
           }
           FileReader fileReader = new FileReader(f);
           JFunctorFactory functorFactory = createFunctorFactory();
           functorFactory.setVerboseOn(true); //enable output to console.
           FunctorScript script = new FunctorScript(functorFactory);
           script.execute(fileReader);
           assert functorFactory.getEnvironment().containsKey("foo") : "Missing key";
           assert functorFactory.getEnvironment().get("fnord").equals("baz") : "Incorrect sotred value. Expected \"baz\" and got \"" + functorFactory.getEnvironment().get("fnord") + "\"";
       }
}
