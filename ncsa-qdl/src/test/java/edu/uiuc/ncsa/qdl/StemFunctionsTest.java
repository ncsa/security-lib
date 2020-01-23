package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
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
 * on 1/17/20 at  12:51 PM
 */
public class StemFunctionsTest extends TestBase {
    TestUtils testUtils = TestUtils.newInstance();

    @Test
    public void testSizeStem() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);

        Polyad polyad = new Polyad(StemEvaluator.SIZE_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");

        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert ((Long) polyad.getResult()) == 4L;
    }

    @Test
    public void testSizeString() throws Exception {
        String input = "One Ring to rule them all, One Ring to find them";

        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StemEvaluator.SIZE_TYPE);
        ConstantNode arg = new ConstantNode(input, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert ((Long) polyad.getResult()) == input.length();
    }

    @Test
    public void testSizeLong() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StemEvaluator.SIZE_TYPE);
        ConstantNode arg = new ConstantNode(new Long(123456L), Constant.LONG_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert ((Long) polyad.getResult()) == 0L;
    }

    @Test
    public void testSizeKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.GET_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        polyad.addArgument(arg);
        polyad.evaluate(state);
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
       public void testCommonKeys() throws Exception {
           State state = testUtils.getNewState();
           SymbolTable symbolTable = state.getSymbolStack();


           StemVariable sourceStem = new StemVariable();
           sourceStem.put("rule", "One Ring to rule them all");
           sourceStem.put("find", "One Ring to find them");
           sourceStem.put("bring", "One Ring to bring them all");
           sourceStem.put("bind", "and in the darkness bind them");

        StemVariable sourceStem2 = new StemVariable();
        sourceStem2.put("rule", "mairzy doats");
        sourceStem2.put("find", "and dozey");
        sourceStem2.put("bring", "doats");
        sourceStem2.put("3", "and in the darkness bind them");
        sourceStem2.put("5", "whatever");



           symbolTable.setStemVariable("sourceStem.", sourceStem);
           symbolTable.setStemVariable("sourceStem2.", sourceStem2);
           Polyad polyad = new Polyad(StemEvaluator.COMMON_KEYS_TYPE);
           VariableNode arg = new VariableNode("sourceStem.");
           VariableNode arg2 = new VariableNode("sourceStem2.");
           polyad.addArgument(arg);
           polyad.addArgument(arg2);
           polyad.evaluate(state);
           StemVariable keys = (StemVariable) polyad.getResult();
           assert keys.size() == 3;
           assert keys.containsValue("rule");
           assert keys.containsValue("find");
           assert !keys.containsValue("bind");
           assert keys.containsValue("bring");
           for (String key : keys.keySet()) {
               System.out.println("  var." + key + " == " + keys.get(key));
           }
       }

    @Test
    public void testmakeIndex() throws Exception {
        State state = testUtils.getNewState();
        Polyad polyad = new Polyad(StemEvaluator.TO_LIST_TYPE);
        ConstantNode arg = new ConstantNode(new Long(4L), Constant.LONG_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
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
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setLongValue("int", new Long(5L));

        Polyad polyad = new Polyad(StemEvaluator.TO_STEM_TYPE);
        polyad.addArgument(new VariableNode("sourceStem."));
        polyad.addArgument(new VariableNode("int"));
        polyad.addArgument(new ConstantNode("foo", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("bar", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("baz", Constant.STRING_TYPE));
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 8;
        for (String key : result.keySet()) {
            System.out.println("  var." + key + " == " + result.get(key));
        }
    }

    @Test
    public void testRemoveStem() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();
        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);

        Polyad polyad = new Polyad(StemEvaluator.REMOVE_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");

        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert !symbolTable.isDefined("sourceStem.");
    }
}
