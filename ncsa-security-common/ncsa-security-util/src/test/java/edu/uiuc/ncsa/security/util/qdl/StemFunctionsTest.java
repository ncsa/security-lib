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
 * on 1/17/20 at  12:51 PM
 */
public class StemFunctionsTest extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testSizeStem() throws Exception {
        SymbolTable symbolTable = testUtils.getSymbolTable();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);

        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        VariableNode arg = new VariableNode("sourceStem.", symbolTable);

        polyad.setOperatorType(FunctionEvaluator.SIZE_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert ((Long) polyad.getResult()) == 4L;
    }

    @Test
    public void testSizeString() throws Exception {
        String input = "One Ring to rule them all, One Ring to find them";

        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        ConstantNode arg = new ConstantNode(input, Constant.STRING_TYPE);

        polyad.setOperatorType(FunctionEvaluator.SIZE_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert ((Long) polyad.getResult()) == input.length();
    }

    @Test
    public void testSizeLong() throws Exception {
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        ConstantNode arg = new ConstantNode(new Long(123456L), Constant.LONG_TYPE);
        polyad.setOperatorType(FunctionEvaluator.SIZE_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        assert ((Long) polyad.getResult()) == 0L;
    }

    @Test
    public void testSizeKeys() throws Exception {
        SymbolTable symbolTable = testUtils.getSymbolTable();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        VariableNode arg = new VariableNode("sourceStem.", symbolTable);
        polyad.setOperatorType(FunctionEvaluator.KEYS_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        StemVariable keys = (StemVariable) polyad.getResult();
        assert keys.size() == 4;
        assert keys.containsKey("0");
        assert keys.containsKey("1");
        assert keys.containsKey("2");
        assert keys.containsKey("3");
        for (String key : keys.keySet()) {
            System.out.println("  var." + key + " == " + keys.get(key));
            assert sourceStem.containsKey(keys.get(key));
        }
    }

    @Test
    public void testmakeIndex() throws Exception {
        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        ConstantNode arg = new ConstantNode(new Long(4L), Constant.LONG_TYPE);
        polyad.setOperatorType(FunctionEvaluator.MAKE_INDEX_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate();
        StemVariable indices = (StemVariable) polyad.getResult();
        assert indices.containsKey("0");
        assert indices.containsKey("1");
        assert indices.containsKey("2");
        assert indices.containsKey("3");
        assert indices.getLong("0").equals(0L);
        assert indices.getLong("1").equals(1L);
        assert indices.getLong("2").equals(2L);
        assert indices.getLong("3").equals(3L);
    }

    @Test
    public void testToStem() throws Exception {
        SymbolTable symbolTable = testUtils.getSymbolTable();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setLongValue("int", new Long(5L));

        Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
        polyad.setOperatorType(FunctionEvaluator.TO_STEM_TYPE);
        polyad.addArgument(new VariableNode("sourceStem.", symbolTable));
        polyad.addArgument(new VariableNode("int", symbolTable));
        polyad.addArgument(new ConstantNode("foo", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("bar", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("baz", Constant.STRING_TYPE));
        polyad.evaluate();
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 8;
        for (String key : result.keySet()) {
            System.out.println("  var." + key + " == " + result.get(key));
        }
    }
    @Test
    public void testRemoveStem() throws Exception{
        SymbolTable symbolTable = testUtils.getSymbolTable();

             StemVariable sourceStem = new StemVariable();
             sourceStem.put("rule", "One Ring to rule them all");
             sourceStem.put("find", "One Ring to find them");
             sourceStem.put("bring", "One Ring to bring them all");
             sourceStem.put("bind", "and in the darkness bind them");

             symbolTable.setStemVariable("sourceStem.", sourceStem);

             Polyad polyad = new Polyad(testUtils.getOpEvaluator(), testUtils.getFunctionEvaluator());
             VariableNode arg = new VariableNode("sourceStem.", symbolTable);

             polyad.setOperatorType(FunctionEvaluator.REMOVE_TYPE);
             polyad.addArgument(arg);
             polyad.evaluate();
             assert !symbolTable.isDefined("sourceStem.");
    }
}
