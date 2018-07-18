package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl;
import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import edu.uiuc.ncsa.security.util.functor.parser.FunctorParser;
import edu.uiuc.ncsa.security.util.functor.parser.event.EDLBSHandler;
import edu.uiuc.ncsa.security.util.functor.parser.event.EventDrivenFunctorHandler;
import edu.uiuc.ncsa.security.util.functor.parser.event.EventDrivenLogicBlockHandler;
import edu.uiuc.ncsa.security.util.functor.parser.event.EventDrivenParser;
import edu.uiuc.ncsa.security.util.functor.parser.old.JSONHandler;
import org.junit.Test;

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
     * @throws Exception
     */
    @Test
    public void testAND() throws Exception {
        String testString = "and(endsWith(\"the quick brown fox\",\"fox\"),contains(\"foo\",\"zfoo\"))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);
        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert (Boolean) eventDrivenFunctorHandler.getFunctor().getResult();
    }

    /**
     * Test that basic logib block parsing works.
     * @throws Exception
     */
    @Test
    public void testLB() throws Exception {
        String testString = "if[match(\"B\",\"B\")]then[toLowerCase(\"Z\")]";
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler fh = new EventDrivenFunctorHandler(functorFactory);
        EventDrivenLogicBlockHandler lbh = new EventDrivenLogicBlockHandler(fh, functorFactory);
        parser.addBracketListener(lbh);
        parser.addCommaListener(lbh);
        parser.parse(testString);
        lbh.getLogicBlock().execute();
        // Remember that logic blocks can have lists of functors to execute, so the result is an array of each result.
        assert lbh.getLogicBlock().getResults().size() == 1;
        lbh.getLogicBlock().getResults().get(0).equals("z");
    }

    /**
     * Tests that having multiple functors in the consequent is correctly interpreted
     *
     * @throws Exception
     */
    @Test
    public void testLBMultipleThenClause() throws Exception {
        String testString = "if[match(\"B\",\"B\")]then[toLowerCase(\"Z\"),toUpperCase(\"a\"), toLowerCase(\"BcD\")]";
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler fh = new EventDrivenFunctorHandler(functorFactory);
        EventDrivenLogicBlockHandler lbh = new EventDrivenLogicBlockHandler(fh, functorFactory);
        parser.addBracketListener(lbh);
        parser.addCommaListener(lbh);
        parser.parse(testString);
        lbh.getLogicBlock().execute();
        // Remember that logic blocks can have lists of functors to execute, so the result is an array of each result.
        assert lbh.getLogicBlock().getResults().size() == 3;
        lbh.getLogicBlock().getResults().get(0).equals("z");
        lbh.getLogicBlock().getResults().get(1).equals("A");
        lbh.getLogicBlock().getResults().get(2).equals("bcd");
    }

    /**
     * Test that a conditional with an else clause is parsed correctly
     * @throws Exception
     */
    @Test
    public void testLBElseClause() throws Exception {
        String testString = "if[match(\"B\",\"C\")]then[toLowerCase(\"Z\"),toUpperCase(\"a\")]else[toLowerCase(\"BcD\")]";
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler fh = new EventDrivenFunctorHandler(functorFactory);
        EventDrivenLogicBlockHandler lbh = new EventDrivenLogicBlockHandler(fh, functorFactory);
        parser.addBracketListener(lbh);
        parser.addCommaListener(lbh);
        parser.parse(testString);
        lbh.getLogicBlock().execute();
        // Remember that logic blocks can have lists of functors to execute, so the result is an array of each result.
        assert lbh.getLogicBlock().getResults().size() == 1;
        lbh.getLogicBlock().getResults().get(0).equals("bcd");
    }

    /**
     * Test that functor composition ishandled correctly.
     *
     * @throws Exception
     */
    @Test
    public void testFunctorComposition() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        String testString = "toUpperCase(toLowerCase(toUpperCase(\"a\")))";

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("A");
    }

    /**
     * Another test that functor arguments are handled correctly
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        String testString = "drop(toUpperCase(\"a\"),toUpperCase(\"abcda\"))";

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("BCD");
    }

    /**
     * Test that a collection of string only arguments is handled correctly
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerStringArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        String testString = "concat(\"a\",\"b\",\"c\",\"d\")";

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("abcd");
    }

    /**
     * Test that a string or functor arguments is handled correctly
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerMultipleArgumentParsing() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        String testString = "concat(toLowerCase(\"A\"),toLowerCase(\"B\"),toLowerCase(\"C\"),toLowerCase(\"d\"))";

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("abcd");
    }

    /**
     * This tests that the parser can keep straight when commas are inside different arguments
     *
     * @throws Exception
     */
    @Test
    public void testFHandlerCommas() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        String testString = "concat(\"a\",concat(\"b\",concat(\"c\",\"d\")))";

        eventDrivenParser.parse(testString);
        assert eventDrivenFunctorHandler.areDelimitersBalanced();
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("abcd");
    }

    @Test
    public void testFHandler2() throws Exception {
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        String testString = "drop(\"A\",toUpperCase(\"abcda\"))";

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("BCD");
    }

    @Test
    public void testFHandlerWhitespace() throws Exception {
        String testString = "drop(\n" +
                "     \"A\",\n" +
                "     toUpperCase(\n" +
                "            \"abcda  \"\n" +
                "     )\n" +
                ")";
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("BCD  ");
    }

    /**
     * Test that running the parser against the handler works, then changing the input
     * can reuse the handler again.
     *
     * @throws Exception
     */
    @Test
    public void testFHandler2Reset() throws Exception {
        String testString = "drop(\"A\",toUpperCase(\"abcda\"))";

        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);

        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();

        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("BCD");
        eventDrivenFunctorHandler.reset();
        testString = "drop(\"A\",toUpperCase(\"aspqra\"))";
        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("SPQR");

    }

    /**
     * An experimental parser to turn the basic simple notation into JSON code.
     *
     * @throws Exception
     */
    @Test
    public void testJSONParser() throws Exception {
        String testString = "if[and(A(\"B\"),zz(W))]then[Q(R(\"Z\"))]";
        FunctorParser parser = new FunctorParser();
        JSONHandler handler = new JSONHandler();
        parser.parse(testString, handler);
    }

    /**
     * This does a simple logic block test of a single conditional. Note that the conditional uses the special
     * functor "true" which must be edge cased.
     *
     * @throws Exception
     */
    @Test
    public void testSimpleLogicBlocks() throws Exception {
        String testString = "and{if[true]then[toLowerCase(\"ABC\")]}";
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler fh = new EventDrivenFunctorHandler(functorFactory);
        EventDrivenLogicBlockHandler lbh = new EventDrivenLogicBlockHandler(fh, functorFactory);
        EDLBSHandler lbs = new EDLBSHandler(lbh, functorFactory);
        parser.addBraceListener(lbs);
        parser.addCommaListener(lbs);
        parser.parse(testString);
        lbs.getLogicBlocks().execute();
        assert lbs.getLogicBlocks().getFunctorMap().size() == 1;
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).size() == 1;
        assert lbs.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.TO_LOWER_CASE.getValue()).get(0).getResult().equals("abc");
    }

    /**
     * This does a simple logic block test of a single conditional. Note that the conditional uses the special
     * functor "true" which must be edge cased.
     *
     * @throws Exception
     */
    @Test
    public void testANDLogicBlocks() throws Exception {
        String testString = "and{if[true]then[toLowerCase(\"ABC\")],if[true]then[toLowerCase(\"DEF\")]}";
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler fh = new EventDrivenFunctorHandler(functorFactory);
        EventDrivenLogicBlockHandler lbh = new EventDrivenLogicBlockHandler(fh, functorFactory);
        EDLBSHandler lbs = new EDLBSHandler(lbh, functorFactory);
        parser.addBraceListener(lbs);
        parser.addCommaListener(lbs);
        parser.parse(testString);
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
                "          contains(toLowerCase(\"FOO\"),\"zfoo\")," +
                "         endsWith(\"abc\",\"c\")" +
                "        )";
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);
        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert (Boolean) eventDrivenFunctorHandler.getFunctor().getResult();
    }

    /**
     * This hits up that the functor parser can handle depth (lots of nesting) and
     * breadth (lots of arguments) and keep them straight.
     *
     * @throws Exception
     */
    @Test
    public void testFunctorDepthAndBreadth() throws Exception {
        String testString = "concat(\"a\",concat(\"b\",concat(\"c\",toUpperCase(\"d\"))),toLowerCase(\"EFG\"),toUpperCase(drop(\"Q\",concat(\"hQ\",\"iQ\",\"Qj\"))))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);
        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("abcDefgHIJ") : "Got " + eventDrivenFunctorHandler.getFunctor().getResult();
    }

    @Test
    public void testescapeCharacter() throws Exception {
        String testString = "concat(\"\\\"\\\"\\{\\}\",concat(\"\\[\\]\",concat(\"\\(\\)\",toUpperCase(\"d\"))),toLowerCase(\"EFG\"),toUpperCase(drop(\"Q\",concat(\"hQ\",\"iQ\",\"Qj\"))))";
        EventDrivenParser eventDrivenParser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler eventDrivenFunctorHandler = new EventDrivenFunctorHandler(functorFactory);
        eventDrivenParser.addParenthesisListener(eventDrivenFunctorHandler);
        eventDrivenParser.addDoubleQuoteListener(eventDrivenFunctorHandler);
        eventDrivenParser.addCommaListener(eventDrivenFunctorHandler);
        eventDrivenParser.parse(testString);
        eventDrivenFunctorHandler.getFunctor().execute();
        assert eventDrivenFunctorHandler.getFunctor().getResult().equals("\"\"{}[]()DefgHIJ") : "Got " + eventDrivenFunctorHandler.getFunctor().getResult();
    }

    @Test
    public void testNestedConditionals() throws Exception {
//                String testString = "if[contains(\"foo\",\"zfoo\")]then[if[endsWith(\"abc\",\"c\")]then[concat(\"eppn\",\"A\")]]";
        String testString = "" +
                " or{" +
                "   if[" +
                "      and(" +
                "          contains(toLowerCase(\"FOO\"),\"zfoo\")," +
                "         endsWith(\"abc\",\"c\")" +
                "        )" +
                "     ]then[" +
                "      concat(\"p\",\"q\")" +
                "     ]," +
                "   if[" +
                "    and(" +
                "        contains(\"foo\",\"zfoo\")," +
                "       endsWith(\"abc\",\"d\")" +
                "      )" +
                "    ]then[" +
                "  concat(\"x\",\"y\")" +
                "   ]" +
                " }";
        // All this should result in the string "pq"
        EventDrivenParser parser = new EventDrivenParser();
        JFunctorFactory functorFactory = createFunctorFactory();
        EventDrivenFunctorHandler fh = new EventDrivenFunctorHandler(functorFactory);
        EventDrivenLogicBlockHandler lbh = new EventDrivenLogicBlockHandler(fh, functorFactory);
        EDLBSHandler eh = new EDLBSHandler(lbh, functorFactory);
        parser.addBraceListener(eh);
        parser.addCommaListener(eh);
        parser.parse(testString);
        eh.getLogicBlocks().execute();
        assert eh.getLogicBlocks().getFunctorMap().containsKey(FunctorTypeImpl.CONCAT.getValue());
        assert eh.getLogicBlocks().getFunctorMap().get(FunctorTypeImpl.CONCAT.getValue()).get(0).getResult().equals("pq");

    }
}
