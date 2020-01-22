package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.StringEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  1:11 PM
 */
public class StringFunctionTests extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testContainsStringString() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StringEvaluator.CONTAINS_TYPE);
        ConstantNode left = new ConstantNode("abcdef", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("de", Constant.STRING_TYPE);

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        assert (Boolean) polyad.getResult();
    }

    @Test
    public void testContainsStringStem() throws Exception {
        // test that the first argument is a string and the second is a stem variable.
        // result should be conformable with the second argument
        String source = "Four score and seven years ago...";
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable snippets = new StemVariable();
        snippets.put("score", "score");
        snippets.put("Four", "Four");
        snippets.put("ago", "woof");
        snippets.put("7", "seven");
        symbolTable.setStemVariable("snippets.", snippets);
        symbolTable.setStringValue("source", source);

        Polyad polyad = new Polyad(StringEvaluator.CONTAINS_TYPE);
        VariableNode left = new VariableNode("source");
        VariableNode right = new VariableNode("snippets.");

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        // A left scalar is propagate to all the elements of the right argument if it is compound.
        assert result.containsKey("score");
        assert (Boolean) result.get("score");
        assert result.containsKey("Four");
        assert (Boolean) result.get("Four");
        assert result.containsKey("7");
        assert (Boolean) result.get("7");
        assert result.containsKey("ago");
        assert !(Boolean) result.get("ago");

    }

    /**
     * Case is concat(stem. string) <br/><br/>
     * Anticipated result is that there will be a stem variable that results with the same keys and
     * booleans telling whether or not the string is included.
     *
     * @throws Exception
     */
    @Test
    public void testContainsStemString() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        String targetString = "One";

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStringValue("targetString", targetString);

        Polyad polyad = new Polyad(StringEvaluator.CONTAINS_TYPE);
        VariableNode left = new VariableNode("sourceStem.");
        VariableNode right = new VariableNode("targetString");

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.containsKey("rule");
        assert result.getBoolean("rule");
        assert result.containsKey("find");
        assert result.getBoolean("find");
        assert result.containsKey("bring");
        assert result.getBoolean("bring");
        assert result.containsKey("bind");
        assert !result.getBoolean("bind");
    }

    @Test
    public void testContainsStemStem() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        StemVariable targetStem = new StemVariable();
        targetStem.put("all", "all");
        targetStem.put("One", "One");
        targetStem.put("bind", "woof");
        targetStem.put("7", "seven");
        symbolTable.setStemVariable("snippets.", targetStem);

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("targetStem.", targetStem);

        Polyad polyad = new Polyad(StringEvaluator.CONTAINS_TYPE);
        VariableNode left = new VariableNode("sourceStem.");
        VariableNode right = new VariableNode("targetStem.");

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        // The contract is that the answer is conformable to the left argument, so only keys from the left
        // argument appear in the result.
        assert result.size() == 1;
        assert result.containsKey("bind");
        assert !result.getBoolean("bind");
    }

    @Test
    public void testStringTrim() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        String arg = "   my   my   my    ";
        symbolTable.setStringValue("arg", arg);
        String result = arg.trim();
        Polyad polyad = new Polyad(StringEvaluator.TRIM_TYPE);
        VariableNode left = new VariableNode("arg");

        polyad.addArgument(left);
        polyad.evaluate(state);
        assert polyad.getResult().equals(result);

    }

    @Test
    public void testNumberTrim() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        Long arg = System.currentTimeMillis();
        symbolTable.setLongValue("arg", arg);
        Polyad polyad = new Polyad(StringEvaluator.TRIM_TYPE);
        VariableNode left = new VariableNode("arg");

        polyad.addArgument(left);
        polyad.evaluate(state);
        assert polyad.getResult().equals(arg); // no change

    }

    @Test
    public void testStemTrim() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

     StemVariable stem = new StemVariable();
        stem.put("1", "  foo");
        stem.put("woof", "      ");
        stem.put("warp", "foo           ");
        stem.put("9", "       foo           ");
        symbolTable.setStemVariable("stem.", stem);
        Polyad polyad = new Polyad(StringEvaluator.TRIM_TYPE);
        VariableNode left = new VariableNode("stem.");

        polyad.addArgument(left);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.get("1").equals("foo");
        assert result.get("woof").equals("");
        assert result.get("warp").equals("foo");
        assert result.get("9").equals("foo");

    }

    @Test
    public void testIndexOfStringString() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StringEvaluator.INDEX_OF_TYPE);
        ConstantNode left = new ConstantNode("abcdef", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("de", Constant.STRING_TYPE);
        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        Long result = (Long) polyad.getResult();
        assert result == 3L;
    }

    @Test
    public void testIndexOfStringStem() throws Exception {
        State state = testUtils.getNewState();

        String source = "Four score and seven years ago...";
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable snippets = new StemVariable();
        snippets.put("score", "score");
        snippets.put("Four", "Four");
        snippets.put("ago", "woof");
        snippets.put("7", "seven");
        symbolTable.setStemVariable("snippets.", snippets);
        symbolTable.setStringValue("source", source);

        Polyad polyad = new Polyad(StringEvaluator.INDEX_OF_TYPE);
        VariableNode left = new VariableNode("source");
        VariableNode right = new VariableNode("snippets.");

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.getLong("score") == 5L;
        assert result.getLong("Four") == 0L;
        assert result.getLong("7") == 15L;
        assert result.getLong("ago") == -1L;
    }

    @Test
    public void testIndexOfStemString() throws Exception {
        State state = testUtils.getTestState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        String targetString = "One";

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStringValue("targetString", targetString);

        Polyad polyad = new Polyad(StringEvaluator.INDEX_OF_TYPE);
        VariableNode left = new VariableNode("sourceStem.");
        VariableNode right = new VariableNode("targetString");

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 4;
        assert result.getLong("rule") == 0L;
        assert result.getLong("find") == 0L;
        assert result.getLong("bind") == -1L;
        assert result.getLong("bring") == 0L;
    }

    @Test
    public void testIndexOfStemStem() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

       StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        StemVariable targetStem = new StemVariable();
        targetStem.put("all", "all");
        targetStem.put("One", "One");
        targetStem.put("bind", "darkness");
        targetStem.put("7", "seven");
        symbolTable.setStemVariable("snippets.", targetStem);
        /**/
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("targetStem.", targetStem);

        Polyad polyad = new Polyad(StringEvaluator.INDEX_OF_TYPE);
        VariableNode left = new VariableNode("sourceStem.");
        VariableNode right = new VariableNode("targetStem.");

        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        // The contract is that the answer is conformable to the left argument, so only keys from the left
        // argument appear in the result.
        assert result.size() == 1;
        assert result.getLong("bind") == 11L;
    }

    @Test
    public void testStringToUpper() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        String arg = "mairzy doats";
        symbolTable.setStringValue("arg", arg);
        String result = "MAIRZY DOATS";
        Polyad polyad = new Polyad(StringEvaluator.TO_UPPER_TYPE);
        VariableNode left = new VariableNode("arg");

        polyad.addArgument(left);
        polyad.evaluate(state);
        assert polyad.getResult().equals(result);

    }

    @Test
    public void testStringToLower() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        String arg = "MAIRZY DOATS";
        symbolTable.setStringValue("arg", arg);
        String result = "mairzy doats";
        Polyad polyad = new Polyad(StringEvaluator.TO_LOWER_TYPE);
        VariableNode left = new VariableNode("arg");

        polyad.addArgument(left);
        polyad.evaluate(state);
        assert polyad.getResult().equals(result);

    }

    @Test
    public void testStemToLower() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        StemVariable stem = new StemVariable();
        String arg1 = "HAlt WHO";
        String arg2 = "GoeS";
        String arg3 = "THeRe";
        String arg4 = "donCHa know?";
        stem.put("1", arg1);
        stem.put("woof", arg2);
        stem.put("warp", arg3);
        stem.put("9", arg4);
        symbolTable.setStemVariable("stem.", stem);
        Polyad polyad = new Polyad(StringEvaluator.TO_LOWER_TYPE);
        VariableNode left = new VariableNode("stem.");

        polyad.addArgument(left);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.get("1").equals(arg1.toLowerCase());
        assert result.get("woof").equals(arg2.toLowerCase());
        assert result.get("warp").equals(arg3.toLowerCase());
        assert result.get("9").equals(arg4.toLowerCase());

    }

    @Test
    public void testStemToUpper() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        StemVariable stem = new StemVariable();
        String arg1 = "HAlt WHO";
        String arg2 = "GoeS";
        String arg3 = "THeRe";
        String arg4 = "donCHa know?";
        stem.put("1", arg1);
        stem.put("woof", arg2);
        stem.put("warp", arg3);
        stem.put("9", arg4);
        symbolTable.setStemVariable("stem.", stem);
        Polyad polyad = new Polyad(StringEvaluator.TO_UPPER_TYPE);
        VariableNode left = new VariableNode("stem.");

        polyad.addArgument(left);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.get("1").equals(arg1.toUpperCase());
        assert result.get("woof").equals(arg2.toUpperCase());
        assert result.get("warp").equals(arg3.toUpperCase());
        assert result.get("9").equals(arg4.toUpperCase());

    }

    @Test
    public void testAllStringReplace() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StringEvaluator.REPLACE_TYPE);
        ConstantNode source = new ConstantNode("abcdef", Constant.STRING_TYPE);
        ConstantNode oldValue = new ConstantNode("de", Constant.STRING_TYPE);
        ConstantNode newValue = new ConstantNode("holy cow", Constant.STRING_TYPE);
        String expectedValue = "abcholy cowf";

        polyad.addArgument(source);
        polyad.addArgument(oldValue);
        polyad.addArgument(newValue);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedValue);
    }

    @Test
    public void testStemStringReplace() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        StemVariable targetStem = new StemVariable();
        targetStem.put("all", "all");
        targetStem.put("One", "One");
        targetStem.put("bind", "darkness");
        targetStem.put("7", "seven");
        symbolTable.setStemVariable("snippets.", targetStem);
        /**/
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("targetStem.", targetStem);

        Polyad polyad = new Polyad(StringEvaluator.REPLACE_TYPE);
        VariableNode source = new VariableNode("sourceStem.");
        VariableNode old = new VariableNode("targetStem.");
        ConstantNode newValue = new ConstantNode("two", Constant.STRING_TYPE);
        polyad.addArgument(source);
        polyad.addArgument(old);
        polyad.addArgument(newValue);

        polyad.evaluate(state);
        String expectedValue = "and in the two bind them";
        StemVariable outStem = (StemVariable) polyad.getResult();
        assert outStem.getString("bind").equals(expectedValue);
    }

    @Test
    public void testInsertStringString() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StringEvaluator.INSERT_TYPE);
        ConstantNode left = new ConstantNode("abcdef", Constant.STRING_TYPE);
        ConstantNode right = new ConstantNode("GAAH!", Constant.STRING_TYPE);
        ConstantNode index = new ConstantNode(new Long(3L), Constant.LONG_TYPE);
        String expectedResult = "abcGAAH!def";
        polyad.addArgument(left);
        polyad.addArgument(right);
        polyad.addArgument(index);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedResult);
    }

    /**
     * This takes a stem and a string and a single index and insert the same
     * String at the same place.
     *
     * @throws Exception
     */
    @Test
    public void testInsertStemString() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        String newS = "GAAH!";
        ConstantNode snippet = new ConstantNode(newS, Constant.STRING_TYPE);
        ConstantNode index = new ConstantNode(new Long(3L), Constant.LONG_TYPE);

        /**/
        symbolTable.setStemVariable("sourceStem.", sourceStem);

        Polyad polyad = new Polyad(StringEvaluator.INSERT_TYPE);
        VariableNode source = new VariableNode("sourceStem.");
        polyad.addArgument(source);
        polyad.addArgument(snippet);
        polyad.addArgument(index);

        polyad.evaluate(state);
        StemVariable stem = (StemVariable) polyad.getResult();
        assert stem.get("rule").toString().startsWith("One" + newS);
        assert stem.get("find").toString().startsWith("One" + newS);
        assert stem.get("bring").toString().startsWith("One" + newS);
        assert stem.get("bind").toString().startsWith("and" + newS);
    }

    /**
     * This takes a set of stems, another set of stems and a set of indices then does insertions
     * Part of the test is that not all stems have all the same keys, so the result is a subset.
     *
     * @throws Exception
     */
    @Test
    public void testInsertStemStem() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");
        StemVariable snippets = new StemVariable();
        snippets.put("all", "all");
        snippets.put("One", "One");
        snippets.put("bind", "darkness");
        snippets.put("7", "seven");
        StemVariable indices = new StemVariable();
        indices.setDefaultValue(new Long(4L));
        String expectedResult = "and darkness";
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("snippets.", snippets);
        symbolTable.setStemVariable("indices.", indices);

        Polyad polyad = new Polyad(StringEvaluator.INSERT_TYPE);
        VariableNode left = new VariableNode("sourceStem.");
        VariableNode snippetVar = new VariableNode("snippets.");
        VariableNode indexVar = new VariableNode("indices.");

        polyad.addArgument(left);
        polyad.addArgument(snippetVar);
        polyad.addArgument(indexVar);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        // The contract is that the answer is conformable to the left argument, so only keys from the left
        // argument appear in the result.
        assert result.size() == 1;
        assert result.getString("bind").startsWith(expectedResult);
    }
}
