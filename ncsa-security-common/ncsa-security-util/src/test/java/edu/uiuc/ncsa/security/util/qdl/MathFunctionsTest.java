package edu.uiuc.ncsa.security.util.qdl;

import edu.uiuc.ncsa.security.util.TestBase;
import edu.uiuc.ncsa.security.util.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.security.util.qdl.expressions.FunctionEvaluator;
import edu.uiuc.ncsa.security.util.qdl.expressions.Polyad;
import edu.uiuc.ncsa.security.util.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.security.util.qdl.util.StemVariable;
import edu.uiuc.ncsa.security.util.qdl.util.SymbolTable;
import edu.uiuc.ncsa.security.util.qdl.variables.Constant;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  10:51 AM
 */
public class MathFunctionsTest extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testAbsoluteValue() throws Exception {
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        ConstantNode left = new ConstantNode(new Long(-5), Constant.LONG_TYPE);
        polyad.setOperatorType(FunctionEvaluator.ABS_VALUE_TYPE);
        polyad.addArgument(left);
        polyad.evaluate();
        assert (Long) polyad.getResult() == 5L;
    }

    @Test
    public void testAbsoluteValueStem() throws Exception {
        SymbolTable symbolTable = testUtils.getSymbolTable();
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        StemVariable arg = new StemVariable();
        arg.put("0", new Long(-12345L));
        arg.put("1", new Long(2468L));
        arg.put("2", new Long(-1000000L));
        arg.put("3", new Long(987654321L));

        symbolTable.setStemVariable("arg.", arg);
        VariableNode argNode = new VariableNode("arg.", symbolTable);
        polyad.setOperatorType(FunctionEvaluator.ABS_VALUE_TYPE);
        polyad.addArgument(argNode);
        polyad.evaluate();
        StemVariable r = (StemVariable) polyad.getResult();
        assert r.size() == 4;
        assert r.getLong("0").equals(12345L);
        assert r.getLong("1").equals(2468L);
        assert r.getLong("2").equals(1000000L);
        assert r.getLong("3").equals(987654321L);
    }

    @Test
    public void testRandomValue() throws Exception {
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.RANDOM_TYPE);
        polyad.evaluate();
        System.out.println("A single generated random number = " + polyad.getResult());
        assert polyad.getResult() instanceof Long;
    }

    @Test
    public void testRandomValueWithArg() throws Exception {
        long count = 5L;
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        ConstantNode left = new ConstantNode(new Long(count), Constant.LONG_TYPE);
        polyad.setOperatorType(FunctionEvaluator.RANDOM_TYPE);
        polyad.addArgument(left);
        polyad.evaluate();
        StemVariable r = (StemVariable) polyad.getResult();
        assert r.size() == count;
        System.out.println("Here are " + count + " random numbers:");
        for (String key : r.keySet()) {
            System.out.println("  var." + key + " == " + r.getLong(key));
        }
    }

    @Test
    public void testRandomString() throws Exception {
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.RANDOM_STRING_TYPE);
        polyad.evaluate();
        System.out.println("A single generated random string = " + polyad.getResult());
        assert polyad.getResult() instanceof String;
    }

    @Test
    public void testRandomStringWithArg() throws Exception {
        Long size = 32L;
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.RANDOM_STRING_TYPE);
        ConstantNode arg = new ConstantNode(new Long(size), Constant.LONG_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        System.out.println("A random string that is " + size + " bytes long = " + polyad.getResult());
        assert polyad.getResult() instanceof String;
    }

    @Test
    public void testHash() throws Exception {
        String expectedResult = "2fd4e1c67a2d28fced849ee1bb76e7391b93eb12";
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.HASH_TYPE);
        ConstantNode arg = new ConstantNode("The quick brown fox jumps over the lazy dog", Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testHashStem() throws Exception {
        SymbolTable symbolTable = testUtils.getSymbolTable();

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
        VariableNode arg = new VariableNode("sourceStem.", symbolTable);
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.HASH_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 4;
        for (String key : result.keySet()) {
            assert result.get(key).equals(expected.get(key));
        }
    }

    @Test
    public void testB64Encode() throws Exception {
        String original = "The quick brown fox jumps over the lazy dog";
        String expectedResult = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZw";
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.ENCODE_B64_TYPE);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testB64Decode() throws Exception {
        String expectedResult = "The quick brown fox jumps over the lazy dog";
        String original = "VGhlIHF1aWNrIGJyb3duIGZveCBqdW1wcyBvdmVyIHRoZSBsYXp5IGRvZw";
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.DECODE_B64_TYPE);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testToHex() throws Exception {
        String original = "The quick brown fox jumps over the lazy dog";
        String expectedResult = "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f67";
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.TO_HEX_TYPE);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert polyad.getResult().equals(expectedResult);
    }

    @Test
    public void testFromHex() throws Exception {
        String expectedResult = "The quick brown fox jumps over the lazy dog";
        String original = "54686520717569636b2062726f776e20666f78206a756d7073206f76657220746865206c617a7920646f67";
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.FROM_HEX_TYPE);
        ConstantNode arg = new ConstantNode(original, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        System.out.println(polyad.getResult());
        assert polyad.getResult().equals(expectedResult);
    }
}
