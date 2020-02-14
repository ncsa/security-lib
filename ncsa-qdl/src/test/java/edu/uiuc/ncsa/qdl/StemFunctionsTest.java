package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

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
    public void testGetKeys() throws Exception {
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
        assert polyad.getResult() instanceof StemVariable;
        StemVariable result = (StemVariable) polyad.getResult();
        for (int i = 0; i < 4; i++) {
            String key = Integer.toString(i);
            assert sourceStem.containsKey(result.get(key));
        }
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
    public void testIncludeKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        int j = 0;
        for (int i = 0; i < 2 * count; i++) {
            String key = getRandomString();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, getRandomString());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.INCLUDE_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        VariableNode arg2 = new VariableNode("keys.");
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count;
        for (int i = 0; i < count; i++) {
            System.out.println(" i = " + i);

            assert result.containsKey(keys.getString(Integer.toString(i)));
        }
    }

    @Test
    public void testRenameKeys() throws Exception {
        // Take a stem and a list of
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        for (int i = 0; i < 2 * count; i++) {
            String key = getRandomString();
            sourceStem.put(key, getRandomString());
            keys.put(key, getRandomString());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.RENAME_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        VariableNode arg2 = new VariableNode("keys.");
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 2 * count;
        for (String key : keys.keySet()) {
            assert result.containsKey(keys.get(key));
            assert result.get(key) == sourceStem.get(key);
        }

    }

    @Test
    public void testExcludeKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        int j = 0;
        for (int i = 0; i < 2 * count; i++) {
            String key = getRandomString();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, getRandomString());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.EXCLUDE_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        VariableNode arg2 = new VariableNode("keys.");
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count;
        for (int i = 0; i < count; i++) {
            System.out.println(" i = " + i);

            assert !result.containsKey(keys.getString(Integer.toString(i)));
        }
    }

    @Test
    public void testExcludeScalarKey() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        String targetKey = getRandomString();

        sourceStem.put(targetKey, getRandomString());
        int count = 5;
        for (int i = 0; i < count; i++) {
            String key = getRandomString();
            sourceStem.put(key, getRandomString());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.EXCLUDE_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        ConstantNode arg2 = new ConstantNode(targetKey, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count; // we added one, then removed it.
        assert !result.containsKey(targetKey);
    }

    @Test
    public void testIncludeScalarKey() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        String targetKey = getRandomString();

        sourceStem.put(targetKey, getRandomString());
        int count = 5;
        for (int i = 0; i < count; i++) {
            String key = getRandomString();
            sourceStem.put(key, getRandomString());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.INCLUDE_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        ConstantNode arg2 = new ConstantNode(targetKey, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 1;
        assert result.containsKey(targetKey);
    }

    @Test
    public void testHasKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        int j = 0;
        for (int i = 0; i < 2 * count; i++) {
            String key = getRandomString();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, getRandomString());
        }
        // add a few that aren't in the target stem.
        for (int i = 0; i < count; i++) {
            keys.put(Integer.toString(j++), getRandomString());
        }
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.HAS_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        VariableNode arg2 = new VariableNode("keys.");
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count * 2;
        j = 0;
        for (int i = 0; i < count; i++) {
            assert result.getBoolean(Integer.toString(j++));
        }
        for (int i = 0; i < count; i++) {
            assert !result.getBoolean(Integer.toString(j++));
        }

    }

    @Test
    public void testHasScalarKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        String targetKey = getRandomString();
        sourceStem.put(targetKey, getRandomString());
        int j = 0;
        for (int i = 0; i < count; i++) {
            String key = getRandomString();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, getRandomString());
        }
        // add a few that aren't in the target stem.
        for (int i = 0; i < count; i++) {
            keys.put(Integer.toString(j++), getRandomString());
        }
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.HAS_KEYS_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        ConstantNode arg2 = new ConstantNode(targetKey, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        assert (Boolean) polyad.getResult();
    }

    @Test
    public void testmakeIndex() throws Exception {
        State state = testUtils.getNewState();
        Polyad polyad = new Polyad(StemEvaluator.MAKE_INDICES_TYPE);
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

        Polyad polyad = new Polyad(StemEvaluator.TO_LIST_TYPE);
        polyad.addArgument(new VariableNode("sourceStem."));
        polyad.addArgument(new VariableNode("int"));
        polyad.addArgument(new ConstantNode("foo", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("bar", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("baz", Constant.STRING_TYPE));
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 5;
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

    @Test
    public void testdefaultValue() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.SET_DEFAULT_TYPE);
        Long expectedResult = new Long(42L);
        VariableNode arg = new VariableNode("sourceStem.");
        ConstantNode arg2 = new ConstantNode(expectedResult, Constant.LONG_TYPE);
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        Long defaultValue = (Long) polyad.getResult();
        assert defaultValue.equals(expectedResult);
        assert sourceStem.getDefaultValue().equals(expectedResult);
        assert sourceStem.get("foo").equals(expectedResult);
    }

    @Test
    public void testBadValue() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.SET_DEFAULT_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        polyad.addArgument(arg);
        polyad.addArgument(arg); // set second to be a stem so it fails
        try {
            polyad.evaluate(state);
            assert false;
        } catch (IllegalArgumentException x) {
            assert true;
        }
    }

    @Test
    public void testDefault_NoStem() throws Exception {
        State state = testUtils.getNewState();
        Long expectedResult = new Long(42L);
        Polyad polyad = new Polyad(StemEvaluator.SET_DEFAULT_TYPE);
        VariableNode arg = new VariableNode("sourceStem.");
        ConstantNode arg2 = new ConstantNode(expectedResult, Constant.LONG_TYPE);

        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable sourceStem = (StemVariable) state.getSymbolStack().resolveValue("sourceStem.");
        Long defaultValue = (Long) polyad.getResult();
        assert defaultValue.equals(expectedResult);
        assert sourceStem.getDefaultValue().equals(expectedResult);
        assert sourceStem.get("foo").equals(expectedResult);
        assert !sourceStem.containsKey("foo");

    }
    @Test
    public void testJSON() throws Exception{
        // This is not a test. It is me debugging whether I want to try and figure out how to convert stems to
        // JSON objects. Could be handy, but it's quite a rats nest.
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONArray array = new JSONArray();
        int count = 10;
        for(int i = 0; i< count; i++){
            array.add(getRandomString(4));
        }
        jsonObject2.put("test_stem", array);
        jsonObject1.put("date", new Date());
        jsonObject1.put("long", System.currentTimeMillis());
        jsonObject1.put("float", new Float("12.34"));
        jsonObject.put("stem1", jsonObject1);
        jsonObject.put("stem2", jsonObject2);
        StemVariable stemVariable = new StemVariable();
        stemVariable.fromJSON(jsonObject);
        System.out.println(stemVariable.toString());

    }

    @Test
    public void testAddList() throws Throwable{
        StemVariable s = new StemVariable();
        ArrayList<Object> list = new ArrayList<>();
        int max = 6;
        for(int i = 0; i < max; i++){
               list.add(i+10L); // so we have different values from keys
        }
        s.addList(list);
        for(int i = 0; i < max; i++){
            Long v = i + 10L;
               assert s.get(Integer.toString(i)).equals(v) : "expected " + v + " and got " + s.get(Integer.toString(i));
        }

    }
}
