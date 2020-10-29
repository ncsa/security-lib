package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.MathEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  10:51 AM
 */
public class MathFunctionsTest extends AbstractQDLTester {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testAbsoluteValue() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(MathEvaluator.ABS_VALUE);
        ConstantNode left = new ConstantNode(new Long(-5), Constant.LONG_TYPE);
        polyad.addArgument(left);
        polyad.evaluate(state);
        assert (Long) polyad.getResult() == 5L;
    }

    @Test
    public void testAbsoluteValueStem() throws Exception {

        State state = testUtils.getNewState();

        SymbolTable symbolTable = state.getSymbolStack();
        StemVariable arg = new StemVariable();
        arg.put("0", new Long(-12345L));
        arg.put("1", new Long(2468L));
        arg.put("2", new Long(-1000000L));
        arg.put("3", new Long(987654321L));

        symbolTable.setStemVariable("arg.", arg);
        VariableNode argNode = new VariableNode("arg.");
        Polyad polyad = new Polyad(MathEvaluator.ABS_VALUE);
        polyad.addArgument(argNode);
        polyad.evaluate(state);
        StemVariable r = (StemVariable) polyad.getResult();
        assert r.size() == 4;
        assert r.getLong("0").equals(12345L);
        assert r.getLong("1").equals(2468L);
        assert r.getLong("2").equals(1000000L);
        assert r.getLong("3").equals(987654321L);
    }

    @Test
    public void testRandomValue() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(MathEvaluator.RANDOM);
        polyad.evaluate(state);
        assert polyad.getResult() instanceof Long;
    }

    @Test
    public void testRandomValueWithArg() throws Exception {
        long count = 5L;
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(MathEvaluator.RANDOM);
        ConstantNode left = new ConstantNode(new Long(count), Constant.LONG_TYPE);
        polyad.addArgument(left);
        polyad.evaluate(state);
        StemVariable r = (StemVariable) polyad.getResult();
        assert r.size() == count;
    }

    @Test
    public void testRandomString() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(MathEvaluator.RANDOM_STRING);
        polyad.evaluate(state);
        assert polyad.getResult() instanceof String;
    }

    @Test
    public void testRandomStringWithArg() throws Exception {
        State state = testUtils.getNewState();

        Long size = 32L;
        Polyad polyad = new Polyad(MathEvaluator.RANDOM_STRING);
        ConstantNode arg = new ConstantNode(new Long(size), Constant.LONG_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult() instanceof String;
        assert polyad.getResult().toString().length() == 43;
    }

    @Test
    public void testHash() throws Exception {
        State state = testUtils.getNewState();

        String expectedResult = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
        Polyad polyad = new Polyad(MathEvaluator.HASH);
        ConstantNode arg = new ConstantNode("The quick brown fox jumps over the lazy dog", Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testHashStem() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("0", "One Ring to rule them all");
        sourceStem.put("1", "One Ring to find them");
        sourceStem.put("2", "One Ring to bring them all");
        sourceStem.put("3", "and in the darkness bind them");

        StemVariable expected = new StemVariable();
        expected.put("0", "40d006b6b2e8bea7bf3e9aad679e13b5246f87a2");
        expected.put("1", "15ac553ccb88f34f3c2a4f9ac0460d0fde29c8a8");
        expected.put("2", "5fd8b1f8f66de0848eca4dfc468fc15e147e4670");
        expected.put("3", "830b0c398047d0d3ac4834508eb1bb87ea7f9ba9");
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        VariableNode arg = new VariableNode("sourceStem.");
        Polyad polyad = new Polyad(MathEvaluator.HASH);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 4;
        for (String key : result.keySet()) {
            assert result.get(key).equals(expected.get(key));
        }
    }

    @Test
    public void testB64Encode() throws Exception {
        State state = testUtils.getNewState();

        String original = "The quick brown fox jumps over the lazy dog";
        String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZw";
        Polyad polyad = new Polyad(MathEvaluator.ENCODE_B64);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testB64Decode() throws Exception {
        State state = testUtils.getNewState();

        String expectedResult = "The quick brown fox jumps over the lazy dog";
        String original = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZw";
        Polyad polyad = new Polyad(MathEvaluator.DECODE_B64);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testToHex() throws Exception {
        State state = testUtils.getNewState();

        String original = "The quick brown fox jumps over the lazy dog";
        String expectedResult = "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f67";
        Polyad polyad = new Polyad(MathEvaluator.TO_HEX);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testFromHex() throws Exception {
        State state = testUtils.getNewState();

        String expectedResult = "The quick brown fox jumps over the lazy dog";
        String original = "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f67";
        Polyad polyad = new Polyad(MathEvaluator.FROM_HEX);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult().equals(expectedResult);
    }

    /**
     * There was a bug in the parser that leading minus signs were not handled correctly.
     * This is because tutorials on writing parsers gave some bad advice (having unary minus
     * outrank other operators rather than being in their normal place in the hierarchy). This test
     * shows that is fixed.
     *
     * @throws Exception
     */
    @Test
    public void testSignedNumbers() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a.0 := -1;");
        addLine(script, "a.1 := -2^2;");
        addLine(script, "a.2 := -2^3;");
        addLine(script, "a.30 := -.1;");
        addLine(script, "a.3 := -.1^2;");
        addLine(script, "a.4 := -.1^3;");
        addLine(script, "a.5 := -3*4;");
        addLine(script, "a.6 := -3*(-4);");
        addLine(script, "a.7 := 3*(-4);");
        addLine(script, "a.8 := (3)*(4);");
        addLine(script, "a.9 := -.3*(-4);");
        addLine(script, "a.10 := 0-.3*(-4);");
        addLine(script, "a.11 := .3*(4);");
        addLine(script, "a.12 := -3*(-.4);");
        addLine(script, "a.13 := 0-3*(-.4);");
        addLine(script, "a.14 := 3*(-.4);");
        addLine(script, "a.15 := -.3*(-.4);");
        addLine(script, "a.16 := .3*(-.4);");
        addLine(script, "a.17 := 0-.3*(-.4);");
        addLine(script, "b. := -3 + indices(6);"); // {-3, -2, -1, 0, 1, 2}
        addLine(script, "c. := -b.;"); // {3, 2, 1, 0, -1, -2}
        addLine(script, "d. := -b.^2;"); // {-9, -4, -1, 0, -1, -4}
        addLine(script, "e. := -b.^3;"); // {27, 8, 1, 0, -1, -8}
        addLine(script, "f. := -3*b.;"); // {9, 6, 3, 0, -3, -6}
        addLine(script, "g. := -.3*b.;"); // {.9, .6, .3, 0.0, -.3, -.6}
        addLine(script, "h. := -.3 + b.;"); // {-3.3, -2.3, -1.3, -0.3, .7, 1.7}
        addLine(script, "i. := -.3 - b.;"); // {2.7, 1.7, .7, -.3, -1.3, -2.3}


        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a.0", state) == -1L;
        assert getLongValue("a.1", state) == -4L;
        assert getLongValue("a.2", state) == -8L;

        assert areEqual(getBDValue("a.30", state), new BigDecimal("-.1"));
        assert areEqual(getBDValue("a.3", state), new BigDecimal("-.01"));
        assert areEqual(getBDValue("a.4", state), new BigDecimal("-.001"));

        assert getLongValue("a.5", state) == -12L;
        assert getLongValue("a.6", state) == 12L;
        assert getLongValue("a.7", state) == -12L;
        assert getLongValue("a.8", state) == 12L;

        assert areEqual(getBDValue("a.9", state), new BigDecimal("1.2"));
        assert areEqual(getBDValue("a.10", state), new BigDecimal("1.2"));
        assert areEqual(getBDValue("a.11", state), new BigDecimal("1.2"));
        assert areEqual(getBDValue("a.12", state), new BigDecimal("1.2"));

        assert areEqual(getBDValue("a.13", state), new BigDecimal("1.2"));
        assert areEqual(getBDValue("a.14", state), new BigDecimal("-1.2"));
        assert areEqual(getBDValue("a.15", state), new BigDecimal(".12"));
        assert areEqual(getBDValue("a.16", state), new BigDecimal("-.12"));
        assert areEqual(getBDValue("a.17", state), new BigDecimal(".12"));

        assert areEqual(getStemValue("b.",state), arrayToStem(new int[]{-3, -2, -1, 0, 1, 2}));
        assert areEqual(getStemValue("c.",state), arrayToStem(new int[] {3, 2, 1, 0, -1, -2}));
        assert areEqual(getStemValue("d.",state), arrayToStem(new int[] {-9, -4, -1, 0, -1, -4}));
        assert areEqual(getStemValue("e.",state), arrayToStem(new int[] {27, 8, 1, 0, -1, -8}));
        assert areEqual(getStemValue("f.",state), arrayToStem(new int[] {9, 6, 3, 0, -3, -6}));
        assert areEqual(getStemValue("g.",state), arrayToStem(new double[] {.9, .6, .3, 0.0, -.3, -.6}));
        assert areEqual(getStemValue("h.",state), arrayToStem(new double[] {-3.3, -2.3, -1.3, -0.3, .7, 1.7}));
        assert areEqual(getStemValue("i.",state), arrayToStem(new double[] {2.7, 1.7, .7, -.3, -1.3, -2.3}));

    }

}
