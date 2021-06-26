package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.variables.*;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  12:51 PM
 */
public class StemFunctionsTest extends AbstractQDLTester {
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

        Polyad polyad = new Polyad(StemEvaluator.SIZE);
        VariableNode arg = new VariableNode("sourceStem.");

        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert ((Long) polyad.getResult()) == 4L;
    }

    @Test
    public void testListKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);

        Polyad polyad = new Polyad(StemEvaluator.LIST_KEYS);
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
    public void testKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();

        StemVariable sourceStem = new StemVariable();
        sourceStem.put("rule", "One Ring to rule them all");
        sourceStem.put("find", "One Ring to find them");
        sourceStem.put("bring", "One Ring to bring them all");
        sourceStem.put("bind", "and in the darkness bind them");

        symbolTable.setStemVariable("sourceStem.", sourceStem);

        Polyad polyad = new Polyad(StemEvaluator.KEYS);
        VariableNode arg = new VariableNode("sourceStem.");

        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert polyad.getResult() instanceof StemVariable;

        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == sourceStem.size();
        for (String key : sourceStem.keySet()) {
            assert result.containsKey(result.get(key));
            assert result.get(key).equals(key);
        }
    }

    @Test
    public void testSizeString() throws Exception {
        String input = "One Ring to rule them all, One Ring to find them";

        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StemEvaluator.SIZE);
        ConstantNode arg = new ConstantNode(input, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.evaluate(state);
        assert ((Long) polyad.getResult()) == input.length();
    }

    @Test
    public void testSizeLong() throws Exception {
        State state = testUtils.getNewState();

        Polyad polyad = new Polyad(StemEvaluator.SIZE);
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
        Polyad polyad = new Polyad(StemEvaluator.LIST_KEYS);
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
        Polyad polyad = new Polyad(StemEvaluator.COMMON_KEYS);
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

    }

    @Test
    public void testIncludeKeys() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        int j = 0;
        QDLCodec codec = new QDLCodec();
        for (int i = 0; i < 2 * count; i++) {
            String key = geter();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, geter());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.INCLUDE_KEYS);
        VariableNode arg = new VariableNode("sourceStem.");
        VariableNode arg2 = new VariableNode("keys.");
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count;
        for (int i = 0; i < count; i++) {
            assert result.containsKey(keys.getString(Integer.toString(i)));
        }
    }

    /**
     * Test the keys() commands for filtering using the parser.
     *
     * @throws Throwable
     */
    @Test
    public void testParserKeyFiltering() throws Throwable {
        String cf = " a. := ['a',null,['x','y'],2]~{'p':123.34, 'q': -321, 'r':false};";
        String cf2;
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, cf);
        addLine(script, "b. := keys(a., 0);");    // null
        addLine(script, "c. := keys(a., 1);");    // boolean
        addLine(script, "d. := keys(a., 2);");    // integer
        addLine(script, "e. := keys(a., 3);");    // string
        addLine(script, "f. := keys(a., 4);");    // stem
        addLine(script, "g. := keys(a., 5);");    //decimal
        addLine(script, "h. := keys(a., true);");    //scalars only
        addLine(script, "i. := keys(a., false);");    // stems only
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getStemValue("b.", state).size() == 1;
        assert (Long) getStemValue("b.", state).get(1L) == 1L;

        assert getStemValue("c.", state).size() == 1;
        assert getStemValue("c.", state).get("r").equals("r");

        assert getStemValue("d.", state).size() == 2;
        assert (Long) getStemValue("d.", state).get(3L) == 3L;
        assert getStemValue("d.", state).get("q").equals("q");

        assert getStemValue("e.", state).size() == 1;
        assert (Long) getStemValue("e.", state).get(0L) == 0L;

        assert getStemValue("f.", state).size() == 1;
        assert (Long) getStemValue("f.", state).get(2L) == 2L;

        assert getStemValue("g.", state).size() == 1;
        assert getStemValue("g.", state).get("p").equals("p");

        assert getStemValue("h.", state).size() == 6;
        assert getStemValue("h.", state).get("p").equals("p");
        assert getStemValue("h.", state).get("q").equals("q");
        assert getStemValue("h.", state).get("r").equals("r");
        assert (Long) getStemValue("h.", state).get(0L) == 0L;
        assert (Long) getStemValue("h.", state).get(1L) == 1L;
        assert (Long) getStemValue("h.", state).get(3L) == 3L;

        assert getStemValue("i.", state).size() == 1;
        assert (Long) getStemValue("i.", state).get(2L) == 2L;

    }

    public void testParserListKeyFiltering() throws Throwable {
        String cf = " a. := ['a',null,['x','y'],2]~{'p':123.34, 'q': -321, 'r':false};";
        String cf2;
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, cf);
        addLine(script, "b. := list_keys(a., 0);");    // null
        addLine(script, "c. := list_keys(a., 1);");    // boolean
        addLine(script, "d. := list_keys(a., 2);");    // integer
        addLine(script, "e. := list_keys(a., 3);");    // string
        addLine(script, "f. := list_keys(a., 4);");    // stem
        addLine(script, "g. := list_keys(a., 5);");    //decimal
        addLine(script, "h. := list_keys(a., true);");    //scalars only
        addLine(script, "i. := list_keys(a., false);");    // stems only
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());

        assert getStemValue("b.", state).size() == 1;
        assert (Long) getStemValue("b.", state).get(0L) == 1L;

        assert getStemValue("c.", state).size() == 1;
        assert getStemValue("c.", state).get(0L).equals("r");

        assert getStemValue("d.", state).size() == 2;
        assert (Long) getStemValue("d.", state).get(0L) == 3L;
        assert getStemValue("d.", state).get(1L).equals("q");

        assert getStemValue("e.", state).size() == 1;
        assert (Long) getStemValue("e.", state).get(0L) == 0L;

        assert getStemValue("f.", state).size() == 1;
        assert (Long) getStemValue("f.", state).get(0L) == 2L;

        assert getStemValue("g.", state).size() == 1;
        assert getStemValue("g.", state).get(0L).equals("p");

        assert getStemValue("h.", state).size() == 6;
        assert (Long) getStemValue("h.", state).get(0L) == 0L;
        assert (Long) getStemValue("h.", state).get(1L) == 1L;
        assert (Long) getStemValue("h.", state).get(2L) == 3L;
        assert getStemValue("h.", state).get(3L).equals("p");
        assert getStemValue("h.", state).get(4L).equals("q");
        assert getStemValue("h.", state).get(5L).equals("r");

        assert getStemValue("i.", state).size() == 1;
        assert (Long) getStemValue("i.", state).get(0L) == 2L;

    }

    @Test
    public void testRenameKeys() throws Exception {
        // Take a stem and a list of
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        int count = 5;
        QDLCodec codec = new QDLCodec();
        randomStem(sourceStem, 2 * count);
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.RENAME_KEYS);
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
            String key = geter();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, geter());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.EXCLUDE_KEYS);
        VariableNode arg = new VariableNode("sourceStem.");
        VariableNode arg2 = new VariableNode("keys.");
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count;
        for (int i = 0; i < count; i++) {
            assert !result.containsKey(keys.getString(Integer.toString(i)));
        }
    }

    @Test
    public void testExcludeScalarKey() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();
        StemVariable keys = new StemVariable();
        String targetKey = geter();

        sourceStem.put(targetKey, geter());
        int count = 5;
        for (int i = 0; i < count; i++) {
            String key = geter();
            sourceStem.put(key, geter());
        }


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.EXCLUDE_KEYS);
        VariableNode arg = new VariableNode("sourceStem.");
        ConstantNode arg2 = new ConstantNode(targetKey, Constant.STRING_TYPE);
        polyad.addArgument(arg);
        polyad.addArgument(arg2);
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == count; // we added one, then removed it.
        assert !result.containsKey(targetKey);
    }

    /**
     * Make a new stem filled with legal variable keys and values.
     *
     * @param count
     * @return
     */
    protected StemVariable randomStem(int count) {
        StemVariable s = new StemVariable();
        randomStem(s, count);
        return s;
    }

    /**
     * Add a given number of random legal varaables to a stem
     *
     * @param s
     * @param count
     * @return
     */
    protected StemVariable randomStem(StemVariable s, int count) {
        for (int i = 0; i < count; i++) {
            s.put(geter(), geter());
        }
        return s;
    }


    @Test
    public void testIncludeScalarKey() throws Exception {
        State state = testUtils.getNewState();
        SymbolTable symbolTable = state.getSymbolStack();


        StemVariable sourceStem = new StemVariable();

        String targetKey = geter();

        sourceStem.put(targetKey, geter());
        int count = 5;
        randomStem(sourceStem, count);


        symbolTable.setStemVariable("sourceStem.", sourceStem);
        Polyad polyad = new Polyad(StemEvaluator.INCLUDE_KEYS);
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
        int count = 5;
        StemVariable keys = new StemVariable();
        int j = 0;
        for (int i = 0; i < 2 * count; i++) {
            String key = geter();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, geter());
        }
        // add a few that aren't in the target stem.
        for (int i = 0; i < count; i++) {
            keys.put(Integer.toString(j++), geter());
        }
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.HAS_KEYS);
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
        String targetKey = geter();
        sourceStem.put(targetKey, geter());
        int j = 0;
        for (int i = 0; i < count; i++) {
            String key = geter();
            if (0 == i % 2) {
                keys.put(Integer.toString(j++), key);
            }
            sourceStem.put(key, geter());
        }
        // add a few that aren't in the target stem.
        for (int i = 0; i < count; i++) {
            keys.put(Integer.toString(j++), geter());
        }
        symbolTable.setStemVariable("sourceStem.", sourceStem);
        symbolTable.setStemVariable("keys.", keys);
        Polyad polyad = new Polyad(StemEvaluator.HAS_KEYS);
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
        Polyad polyad = new Polyad(StemEvaluator.MAKE_INDICES);
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

        Polyad polyad = new Polyad(StemEvaluator.TO_LIST);
        polyad.addArgument(new VariableNode("sourceStem."));
        polyad.addArgument(new VariableNode("int"));
        polyad.addArgument(new ConstantNode("foo", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("bar", Constant.STRING_TYPE));
        polyad.addArgument(new ConstantNode("baz", Constant.STRING_TYPE));
        polyad.evaluate(state);
        StemVariable result = (StemVariable) polyad.getResult();
        assert result.size() == 5;
        assert result.get("0") instanceof StemVariable;
        assert result.getLong("1") == 5L;
        assert result.getString("2").equals("foo");
        assert result.getString("3").equals("bar");
        assert result.getString("4").equals("baz");
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

        Polyad polyad = new Polyad(StemEvaluator.REMOVE);
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
        Polyad polyad = new Polyad(StemEvaluator.SET_DEFAULT);
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
        Polyad polyad = new Polyad(StemEvaluator.SET_DEFAULT);
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
        Polyad polyad = new Polyad(StemEvaluator.SET_DEFAULT);
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

    /**
     * Regression test for converting stems to JSON where there is a stem list of stems.
     * Extra elements were being added.
     *
     * @throws Exception
     */
    @Test
    public void testJSONArray() throws Exception {
        String rawJSON = "{\n" +
                "  \"isMemberOf\":   [" +
                "  {\n" +
                "      \"name\": \"all_users\",\n" +
                "      \"id\": 13002\n" +
                "    },\n" +
                "        {\n" +
                "      \"name\": \"staff_reporting\",\n" +
                "      \"id\": 16405\n" +
                "    },\n" +
                "        {\n" +
                "      \"name\": \"list_allbsu\",\n" +
                "      \"id\": 18942\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        StemVariable stemVariable = new StemVariable();
        stemVariable.fromJSON(JSONObject.fromObject(rawJSON));

        JSON j = stemVariable.toJSON();
        StemVariable x = (StemVariable) stemVariable.get("isMemberOf.");
        assert x.size() == 3;
        assert x.containsKey("0") : "Spurious element added to stem on serialization to JSON.";
        assert x.containsKey("1") : "Spurious element added to stem on serialization to JSON.";
        assert x.containsKey("2") : "Spurious element added to stem on serialization to JSON.";

    }

    /**
     * Critical test. This shows that a stem with integer indices (so it looks like a list)
     * gets converted completely faithfully.
     * This is a different structure than a JSON object in that the list elements 0,1,2,3 are
     * just entries, so it is necessary to show the entries end up as part of the JSON object and
     * are not stashed in a list someplace.
     *
     * @throws Exception
     */
    @Test
    public void testMixedJSON() throws Exception {
        StemVariable s = new StemVariable();
        String name = "bob";
        String issuer = "https://localhost:9443/oauth2";
        String tokenID = "https://localhost:9443/oauth2/idToken/7e3318d9e03b19a2a38ba88542abab0a/1591271860588";
        s.put("sub", name);
        s.put("iss", issuer);
        s.put("token_id", tokenID);
        s.put(0L, 3L);
        s.put(1L, "foo");
        s.put(2L, new BigDecimal("23.4"));
        s.put(3L, Boolean.TRUE);
        JSON json = s.toJSON();
        assert json instanceof JSONObject;
        JSONObject jo = (JSONObject) json;
        assert jo.size() == s.size();
        assert jo.getString("sub").equals(name);
        assert jo.getString("iss").equals(issuer);
        assert jo.getString("token_id").equals(tokenID);
        assert jo.getLong("0") == 3L;
        assert jo.getString("1").equals("foo");
        assert jo.getDouble("2") == 23.4;
        assert jo.getBoolean("3");
    }

    /**
     * Test that arrays are faithfully translated to and from stems
     *
     * @throws Exception
     */
    @Test
    public void testJSONArray2() throws Exception {
        JSONArray array = new JSONArray();
        for (int i = 0; i < 2 * 2; i++) {
            array.add(makeRandomArray(i));
        }
        verifyJSONArrayRoundtrip(array);
    }

    protected void verifyJSONArrayRoundtrip(JSONArray array) {
        // options are this has single strings as elements or JSON Arrays of strings
        StemVariable stemVariable = new StemVariable();
        stemVariable.fromJSON(array);
        JSON json = stemVariable.toJSON();
        assert json instanceof JSONArray : "Did not get back a JSON array";
        JSONArray array2 = (JSONArray) json;
        for (int i = 0; i < array.size(); i++) {
            Object temp = array.get(i);
            if (temp instanceof JSONArray) {
                JSONArray innerA = (JSONArray) temp;
                for (int j = 0; j < innerA.size(); j++) {
                    assert array2.getJSONArray(i).getString(j).equals(innerA.getString(j));
                }

            } else {
                assert temp.equals(array2.get(i));
            }
        }
    }

    /**
     * This tests a mixture of arrays of arrays and single items to show that order in
     * the original list is preserved.
     *
     * @throws Exception
     */
    @Test
    public void testJSONArrayOrder() throws Exception {
        JSONArray array = new JSONArray();
        for (int i = 0; i < 2 * count; i++) {
            if (0 == i % 2) {
                array.add(getRandomString());
            } else {
                array.add(makeRandomArray(i));
            }
        }
        verifyJSONArrayRoundtrip(array);
    }

    protected JSONArray makeRandomArray(int n) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < n; i++) {
            array.add(getRandomString());
        }
        return array;
    }


    @Test
    public void testAddList() throws Throwable {
        StemVariable s = new StemVariable();
        ArrayList<Object> list = new ArrayList<>();
        int max = 6;
        for (int i = 0; i < max; i++) {
            list.add(i + 10L); // so we have different values from keys
        }
        s.addList(list);
        for (int i = 0; i < max; i++) {
            Long v = i + 10L;
            assert s.get(Integer.toString(i)).equals(v) : "expected " + v + " and got " + s.get(Integer.toString(i));
        }

    }

    @Test
    public void testListAppend() throws Throwable {
        StemList<StemEntry> stemList1 = new StemList();
        StemList<StemEntry> stemList2 = new StemList();
        long count1 = 10L;
        long count2 = 5L;
        for (long i = 0L; i < count1; i++) {
            stemList1.add(new StemEntry(i, i / 10.0));
        }
        for (long i = 0L; i < count2; i++) {
            stemList2.add(new StemEntry(i, i * i));
        }
        StemVariable stem1 = new StemVariable();
        StemVariable stem2 = new StemVariable();
        stem1.setStemList(stemList1);
        stem2.setStemList(stemList2);
        stem1.listAppend(stem2);
        StemList<StemEntry> result = stem1.getStemList();
        // should return sorted set
        Object expectedValues[] = new Object[]{0.0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 0L, 1L, 4L, 9L, 16L};
        assert stem1.size() == count1 + count2;
        for (int i = 0; i < expectedValues.length; i++) {
            assert result.get(i).equals(expectedValues[i]);
        }
    }

    @Test
    public void testListCopy() throws Throwable {
        StemList<StemEntry> stemList1 = new StemList();
        StemList<StemEntry> stemList2 = new StemList();
        long count1 = 10L;
        long count2 = 5L;
        for (long i = 0L; i < count1; i++) {
            stemList1.add(new StemEntry(i, i / 10.0));
        }
        for (long i = 0L; i < count2; i++) {
            stemList2.add(new StemEntry(i, i * i));
        }
        StemVariable stem1 = new StemVariable();
        StemVariable stem2 = new StemVariable();
        stem1.setStemList(stemList1);
        stem2.setStemList(stemList2);
        stem1.listCopy(3, 5, stem2, 2);
        StemList<StemEntry> result = stem2.getStemList();
        // should return sorted set
        Object expectedValues[] = new Object[]{0L, 1L, .3, .4, .5, .6, .7};
        for (int i = 0; i < expectedValues.length; i++) {
            assert result.get(i).equals(expectedValues[i]);
        }
    }

    @Test
    public void oldTestListInsert() throws Throwable {
        StemList<StemEntry> stemList1 = new StemList();
        StemList<StemEntry> stemList2 = new StemList();
        long count1 = 10L;
        long count2 = 5L;
        for (long i = 0L; i < count1; i++) {
            stemList1.add(new StemEntry(i, i / 10.0));
        }
        for (long i = 0L; i < count2; i++) {
            stemList2.add(new StemEntry(i, i * i));
        }
        StemVariable stem1 = new StemVariable();
        StemVariable stem2 = new StemVariable();
        stem1.setStemList(stemList1);
        stem2.setStemList(stemList2);
        stem1.oldListInsertAt(stem2, 4, 5);
        StemList<StemEntry> result = stem1.getStemList();
        // should return sorted set
        Object expectedValues[] = new Object[]{.0, .1, .2, .3, 0L, 1L, 4L, 9L, 16L, .4, .5, .6, .7, .8, .9};
        assert result.size() == count1 + count2;
        for (int i = 0; i < expectedValues.length; i++) {
            assert result.get(i).equals(expectedValues[i]);
        }
    }

    @Test
    public void testListInsert() throws Throwable {
        StemList<StemEntry> sourceSL = new StemList();
        StemList<StemEntry> targetSL = new StemList();
        long count1 = 10L;
        long count2 = 5L;
        for (long i = 0L; i < count1; i++) {
            sourceSL.append(i / 10.0);
        }
        for (long i = 0L; i < count2; i++) {
            targetSL.append(i * i);
        }
        StemVariable sourceStem = new StemVariable();
        StemVariable targetStem = new StemVariable();
        sourceStem.setStemList(sourceSL);
        targetStem.setStemList(targetSL);
        sourceStem.listInsertAt(2, 5, targetStem, 3);
        StemList<StemEntry> result = targetStem.getStemList();
        // should return sorted set
        Object expectedValues[] = new Object[]{0L, 1L, 4L, .2, .3, .4, .5, .6, 9L, 16L};
        assert result.size() == count2 + 5; // original plus number inserted
        for (int i = 0; i < expectedValues.length; i++) {
            assert result.get(i).equals(expectedValues[i]);
        }
    }


    @Test
    public void testListSubset() throws Throwable {
        StemList<StemEntry> sourceSL = new StemList();
        long count1 = 10L;
        for (long i = 0L; i < count1; i++) {
            sourceSL.append(i + 20);
        }
        StemVariable sourceStem = new StemVariable();
        sourceStem.setStemList(sourceSL);
        StemVariable targetStem = sourceStem.listSubset(2, 3);
        StemList<StemEntry> result = targetStem.getStemList();
        // should return sorted set
        Object expectedValues[] = new Object[]{22L, 23L, 24L};
        assert result.size() == 3; // original plus number inserted
        for (int i = 0; i < expectedValues.length; i++) {
            assert result.get(i).equals(expectedValues[i]);
        }
    }

    @Test
    public void testListSubset2() throws Throwable {
        StemList<StemEntry> sourceSL = new StemList();
        long count1 = 10L;
        for (long i = 0L; i < count1; i++) {
            sourceSL.append(i + 20);
        }
        StemVariable sourceStem = new StemVariable();
        sourceStem.setStemList(sourceSL);
        // Test copying the tail of the list from the given index.
        StemVariable targetStem = sourceStem.listSubset(7);
        StemList<StemEntry> result = targetStem.getStemList();
        // should return sorted set
        Object expectedValues[] = new Object[]{27L, 28L, 29L};
        assert result.size() == 3; // original plus number inserted
        for (int i = 0; i < expectedValues.length; i++) {
            assert result.get(i).equals(expectedValues[i]);
        }
    }


    @Test
    public void testobjectAppend() throws Throwable {
        StemVariable stemVariable = new StemVariable();
        stemVariable.listAppend("foo");
        assert stemVariable.size() == 1;
        assert stemVariable.get(0L).equals("foo");
    }
    //      x := 0; y.0 := 1; z.1 := 2; w.2 := 3; w.2.0 :='a'; w.2.1 :='b';

    /**
     * Thse two tests make sure that w.z.y.x and w.z.y.x. (so long non-stem and long stem) resolutions work.
     * This creates two variables
     * <pre>
     *      w.2
     *      w.2.
     *  </pre>
     * and has a complicated resolution to get this. This requires that the multi-indices work
     * and keep track of a fair amount of state.
     *
     * @throws Throwable
     */
    @Test
    public void testMultiIndex() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "x := 0; y.0 := 1; z.1 := 2;  w.2.0 :='a'; w.2.1 :='b';");
        // Point with this is that the stem resolution knows that y.0 is a stem and looks up the value of 1
        // in the resolution, so z.1 can resolve to the right index.
        addLine(script, "test. := w.z.y.x.;"); // resolves to w.2 which is a stem
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStemValue("test.", state).size() == 2;
    }

    @Test
    public void testMultiIndex2() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "x := 0; y.0 := 1; z.1 := 2; w.2 := 3;");
        addLine(script, "ok := w.z.y.x == 3;"); // resolves to w.2 which is an integer here.
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);

    }

    /**
     * The contract for stem evaluation is that w.z.y.x uses stems unles told otherwise.
     * This is an example that tests that. Passing in (y) tells the system to
     * evaluate that and use it rather than fall back on the default. 
     * @throws Throwable
     */
    @Test
    public void testMultiIndexOverride() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "x := 0; y.0 := 1; z.1 := 2; w.2 := 3; w.3 := -1; z.7.0 := 3; y :=7;");
        addLine(script, "ok := w.z.(y).x == -1;"); // resolves to w.2 which is an integer here.
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);

    }

    /**
     * Test the has_value function. This test checks for conformability as well as results.
     * It is a bit long but it is critical that this work and in particular, if there is regression
     * it is found immediately.
     *
     * @throws Throwable
     */
    @Test
    public void testHasValue() throws Throwable {
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := indices(3); b.:=2+indices(5);c.foo:=1;c.bar:='arf';");
        addLine(script, "test_a_b. := has_value(a.,b.);");
        addLine(script, "test_b_a. := has_value(b.,a.);");
        addLine(script, "test_a_c. := has_value(a.,c.);");
        addLine(script, "test_c_a. := has_value(c.,a.);");
        addLine(script, "test_c := has_value('arf',c.);"); // scalar result
        addLine(script, "test_1 := has_value(1,a.);"); // scalar result
        addLine(script, "test_bad := has_value(42,b.);"); // scalar result
        addLine(script, "test_bad2 := has_value(42,'woof');"); // scalar result

        State state = testUtils.getNewState();

        // really detailed tests since this is probably one of the most used functions.
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        // tests for scalar left arg:
        assert getBooleanValue("test_c", state);
        assert getBooleanValue("test_1", state);
        assert !getBooleanValue("test_bad", state);
        assert !getBooleanValue("test_bad2", state);

        StemVariable test_a_b = getStemValue("test_a_b.", state);
        assert test_a_b.size() == 3;
        assert !test_a_b.getBoolean(0L);
        assert !test_a_b.getBoolean(1L);
        assert test_a_b.getBoolean(2L);

        StemVariable test_b_a = getStemValue("test_b_a.", state);
        assert test_b_a.size() == 5;
        assert test_b_a.getBoolean(0L);
        assert !test_b_a.getBoolean(1L);
        assert !test_b_a.getBoolean(2L);
        assert !test_b_a.getBoolean(3L);
        assert !test_b_a.getBoolean(4L);

        StemVariable test_a_c = getStemValue("test_a_c.", state);
        assert test_a_c.size() == 3;
        assert !test_a_c.getBoolean(0L);
        assert test_a_c.getBoolean(1L);
        assert !test_a_c.getBoolean(2L);

        StemVariable test_c_a = getStemValue("test_c_a.", state);
        assert test_c_a.size() == 2;
        assert test_c_a.getBoolean("foo");
        assert !test_c_a.getBoolean("bar");

    }

    /*
       Test presets
       j(n)->n;j:=2;k:=1;p:=4;q:=5;r:=6;a. := [i(4),i(5),i(6)];
     Not working
     i(3).0 -- gives parser error for .0 since it thinks it is a decimal.

  The following are working and have tests below:
      (i(4)^2-5).j(3)
     [i(5),-i(6)].j(1).j(3)
     [i(5),i(4)].k
     [2+3*i(5),10 - i(4)].(k.0)
     {'a':'b','c':'d'}.j('a')
     {'a':'b','c':'d'}.'a'
     i(3).i(4).j
     i(3).i(4).i(5).i(6).i(7).i(8).j;
     i(3).i(4).i(5).i(6).j(2);
     i(3).i(4).i(5).(a.k).i(6).i(7).j
    (4*i(5)-21).(i(3).i(4).j(2))
     3 rank exx.
     [[-i(4),3*i(5)],[11+i(6), 4-i(5)^2]].j(0).j(1).j(2)
     (b.).j(0).j(1).j(2)

     Embedded stem example. This get a.1.2 in a very roundabout way
    k:=1;j:=2;a.:=[-i(4),3*i(5),11+i(6)];
    x := i(12).i(11).i(10).(a.k).i(6).i(7).j;
    x==6;

       */
    /* *********** Here below are test for functional stem notation e.g. f(x).j(n) ******* */

    @Test
    public void testSimpleSF0() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "j(n)->n;");
        addLine(script, "x := {'a':'b','c':'d'}.j('a');");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("x", state).equals("b");
    }

    @Test
    public void testSimpleSF1() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x := {'a':'b','c':'d'}.'a';");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStringValue("x", state).equals("b");
    }

    @Test
    public void testSimpleSF2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "k := 1;");
        addLine(script, "a := (n(4)^2-5).i(3);");
        // Previous version required parentheses. Now they actually work
        //addLine(script, "b := [2+3*n(5),10 - n(4)].(k.0);");
        addLine(script, "b := [2+3*n(5),10 - n(4)].k.0;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("a", state) == 4L;
        assert getLongValue("b", state) == 10L;
    }

    @Test
    public void testSimpleSF3() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "j :=2;");
        addLine(script, "x :=n(3).n(4).j;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 2L;

    }

    @Test
    public void testSimpleSFReturnsStem() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "k := 1;");
        addLine(script, "x. := [n(5),n(4)].k;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getStemValue("x.", state).size() == 4; // Noit a great check, but sufficient.
    }

    @Test
    public void testSFEmbeddedStem() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [-n(4),3*n(5),11+n(6)];");
        addLine(script, " k := 1;");
        addLine(script, " j := 2;");
        addLine(script, " x := n(12).n(11).n(10).(a.k).n(6).n(7).j;");
        addLine(script, " y := x == a.1.2;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 6L;
        assert getBooleanValue("y", state);
    }


    /**
     * In this case, the stem variable resolves correctly to the value of 6, but
     * there is no 6th index, so an error condition should be raised.
     *
     * @throws Throwable
     */
    @Test
    public void testBadSFEmbeddedStem() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := [-n(4),3*n(5),11+n(6)];");
        addLine(script, " k := 1;");
        addLine(script, " j := 2;");
        addLine(script, " x := n(2).n(3).n(4).(a.k).n(6).n(7).j;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        try {
            interpreter.execute(script.toString());
        } catch (Throwable t) {
            assert true;
        }
    }

    /**
     * Test that supplying the stem as the left most argument can be resolved.
     *
     * @throws Throwable
     */
    @Test
    public void testInitialStem() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " x := (4*n(5)-21).(n(3).n(4).i(2));");
        addLine(script, " y := -13 == (4*n(5)-21).(n(3).n(4).i(2));");
        // redundant check is to ensure that everything is being run right as expressions
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == -13L;
        assert getBooleanValue("y", state);
    }

    @Test
    public void testThreeRankStem() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " x := [[-n(4),3*n(5)],[11+n(6), 4-n(5)^2]].i(0).i(1).i(2);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 6L;
    }

    /**
     * In the manual, the example given is
     * <pre>
     *       x := 0;
     *     y.0 := 1;
     *     z.1 := 2;
     *     w.2 := 3;
     *     w.z.y.x
     *  3
     * </pre>
     * This next test does that with functions, showing that tail resolution works.
     *
     * @throws Throwable
     */
    @Test
    public void testTailResolution() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " x := (1+n(4)).(1+n(3)).(1+n(2)).i(0);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 3L;
    }

    @Test
    public void testTailResolution2() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " b. := [[ -n(4)^2,2+n(5)],[10 - 3*n(6),4+5*n(7)]];");
        addLine(script, " x := (b.).(0).i(1).(3);");
        addLine(script, " y := b.0.1.3 == (b.).(0).i(1).(3);");

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 5L;
        assert getBooleanValue("y", state);
    }

    /**
     * Test heavily parenthesized tail.
     *
     * @throws Throwable
     */
    @Test
    public void testTailParentheses() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        // Expr is (A).((B).(C).(D)))
        addLine(script, " x := 3 == (1+n(4)).((1+n(3)).((1+n(2)).(i(0))));");
        // Expr is (A).((B).(C)).(D))
        addLine(script, " y := 3 == (1+n(4)).((1+n(3)).((1+n(2))).(i(0)));");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("x", state);
        assert getBooleanValue("y", state);
    }

    /*
      a. := n(2,3,4,5, n(120))
      a.1.2.3.4 := 0
      b. := 10+n(3,3,n(9))
      b.a.1.2.3.4.1; // same as b.0.1 == 11
      b.(a.1.2.3.4).1; // same as b.0.1 == 11
      b.a.1.2.3.4.2 := -1;
      b.0.2 == -1;

     */

    /**
     * Tests that in stem resolution, only what is understood by a stem is consumed,
     * @throws Throwable
     */
    public void testMidTailResolution() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, " x := (1+n(4)).(1+n(3)).(1+n(2)).i(0);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getLongValue("x", state) == 3L;
    }
    /**
     * Test that a list of indices like ['a.b.c'] can be used to access stems. This permits passing around
     * indices
     *
     * @throws Throwable
     */
    @Test
    public void testEmbeddedIndex() throws Throwable {
        /*
               x. := ['store.bicycle.price','store.book.0.price','store.book.1.price','store.book.2.price','store.book.3.price']
  test.:={'comment':'This is taken from the JSON Path spec https://tools.ietf.org/id/draft-goessner-dispatch-jsonpath-00.html for testing', 'store':{'bicycle':{'color':'red', 'price':19.95}, 'book':[{'author':'Nigel Rees', 'price':8.95, 'category':'reference', 'title':'Sayings of the Century'},{'author':'Evelyn Waugh', 'price':12.99, 'category':'fiction', 'title':'Sword of Honour'},{'author':'Herman Melville', 'price':8.99, 'isbn':'0-553-21311-3', 'category':'fiction', 'title':'Moby Dick'},{'author':'J. R. R. Tolkien', 'price':22.99, 'isbn':'0-395-19395-8', 'category':'fiction', 'title':'The Lord of the Rings'}]}, 'expensive':10}
  test.x.0
19.95
  test.x.1
8.95
         */
        StringBuffer script = new StringBuffer();
        addLine(script, "test.:={'comment':'This is taken from the JSON Path spec https://tools.ietf.org/id/draft-goessner-dispatch-jsonpath-00.html for testing', " +
                "'store':{'bicycle':{'color':'red', 'price':19.95}, " +
                "'book':[" +
                "{'author':'Nigel Rees', 'price':8.95, 'category':'reference', 'title':'Sayings of the Century'}," +
                "{'author':'Evelyn Waugh', 'price':12.99, 'category':'fiction', 'title':'Sword of Honour'}," +
                "{'author':'Herman Melville', 'price':8.99, 'isbn':'0-553-21311-3', 'category':'fiction', 'title':'Moby Dick'}," +
                "{'author':'J. R. R. Tolkien', 'price':22.99, 'isbn':'0-395-19395-8', 'category':'fiction', 'title':'The Lord of the Rings'}" +
                "]}, " +
                "'expensive':10};");
        addLine(script, " x. := ['`store`bicycle`price','`store`book`0`price','`store`book`1`price','`store`book`2`price','`store`book`3`price'];");
        addLine(script, "a := test.x.0;"); // resolves to test.store.bicycle.price
        addLine(script, "b := test.x.1;"); // resolves to test.store.book.0.price
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert areEqual(getBDValue("a", state), new BigDecimal("19.95"));
        assert areEqual(getBDValue("b", state), new BigDecimal("8.95"));


    }

    public void testJPathQuery() throws Throwable{
        StringBuffer script = new StringBuffer();
        addLine(script, "a. := {'p':'x', 'q':'y', 'r':5, 's':[2,4,6], 't':{'m':true,'n':345.345}};");
        addLine(script, "x. := query(a., '$..m');");
        addLine(script, "ndx. := query(a., '$..m',true);");
        State state = testUtils.getNewState();

        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        StemVariable x = getStemValue("x.",state);
        assert x.size() == 1;
        assert x.getBoolean(0L);
        StemVariable ndx = getStemValue("ndx.",state);
        assert ndx.size() == 1;
        assert ndx.getString("0").equals("·t·m");
    }

    /**
     * Critical regression test that tests that {@link edu.uiuc.ncsa.qdl.expressions.ExpressionStemNode}s
     * resolve constants on the LHS, so
     * <pre>
     * (a.).(0) := 1 <=> a.0 := 1
     * </pre>
     *
     * @throws Throwable
     */
    public void testBasicExpressionStemNodeAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "(a.).(0) := 1;");
        addLine(script, "x := a.0 == 1;");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("x", state);
    }

    /**
     * Probabilistic test for for_each applied to a dyadic argument.
     * @throws Throwable
     */
    public void testForEach2() throws Throwable {
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "z(x,y)->x^2+y^2;");
         addLine(script, "a. := for_each(@z, n(5),n(7));");
         // The test is that a.i.j == w(i,j) testing all of them is a pain, so we test some
         addLine(script, "b.0 := a.0.0 == z(0,0);");
         addLine(script, "b.1 := a.1.1 == z(1,1);");
         addLine(script, "b.2 := a.2.2 == z(2,2);");
         addLine(script, "b.3 := a.3.3 == z(3,3);");
         addLine(script, "b.4 := a.4.4 == z(4,4);");
         addLine(script, "b.5 := a.0.5 == z(0,5);");
         addLine(script, "b.6 := a.1.6 == z(1,6);");
         addLine(script, "ok:= reduce(@∧, b.);");

         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
     }

    public void testForEach3() throws Throwable {
         State state = testUtils.getNewState();
         StringBuffer script = new StringBuffer();
         addLine(script, "w(x,y,z)->x^2+y^2 + z^3;");
         addLine(script, "a. := for_each(@w, n(5),n(7),n(9));");
         // The test is that a.i.j == w(i,j) testing all of them is a pain, so we test some
         addLine(script, "b.0 := a.0.0.0 == w(0,0,0);");
         addLine(script, "b.1 := a.1.1.1 == w(1,1,1);");
         addLine(script, "b.2 := a.2.2.2 == w(2,2,2);");
         addLine(script, "b.3 := a.3.3.3 == w(3,3,3);");
         addLine(script, "b.4 := a.4.4.4 == w(4,4,4);");
         addLine(script, "b.5 := a.0.5.5 == w(0,5,5);");
         addLine(script, "b.6 := a.1.6.6 == w(1,6,6);");
         addLine(script, "b.7 := a.1.6.7 == w(1,6,7);");
         addLine(script, "b.8 := a.3.4.5 == w(3,4,5);");
         addLine(script, "b.9 := a.4.2.3 == w(4,2,3);");
         addLine(script, "b.10 := a.2.0.1 == w(2,0,1);");
         addLine(script, "ok:= reduce(@∧, b.);");

         QDLInterpreter interpreter = new QDLInterpreter(null, state);
         interpreter.execute(script.toString());
         assert getBooleanValue("ok", state);
     }
     /*
      a.user.b.4.foo.5 := 2
  a.
{user:{b:{4:{foo:{5:2}}}}}
  a.'0' := 1
  a.
[1]~{user:{b:{4:{foo:{5:2}}}}}
      */

    /**
     * Create a stem in two ways and verify that they are the same
     * @throws Throwable
     */
    public void testExpressionStemNodeAssignment() throws Throwable {
        State state = testUtils.getNewState();
        StringBuffer script = new StringBuffer();
        addLine(script, "x. := {'user':{'b':{'4':{'foo':{'5':2}}}}};");
        addLine(script, " a.user.b.4.foo.5 := 2;");
        addLine(script, " ok := a.user.b.4.foo.5 == x.user.b.4.foo.5;");
        addLine(script, " ok0 := size(a.) == size(x.);");
        QDLInterpreter interpreter = new QDLInterpreter(null, state);
        interpreter.execute(script.toString());
        assert getBooleanValue("ok", state);
        assert getBooleanValue("ok0", state);
    }

}
