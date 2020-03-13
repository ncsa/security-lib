package edu.uiuc.ncsa.qdl;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.expressions.ConstantNode;
import edu.uiuc.ncsa.qdl.expressions.Polyad;
import edu.uiuc.ncsa.qdl.expressions.VariableNode;
import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.SymbolTable;
import edu.uiuc.ncsa.qdl.util.StemEntry;
import edu.uiuc.ncsa.qdl.util.StemList;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.qdl.variables.Constant;
import edu.uiuc.ncsa.qdl.variables.QDLCodec;
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
    protected String geter(){
        return enc(getRandomString());
    }

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
        QDLCodec codec = new QDLCodec();
        randomStem(sourceStem, 2*count);
      /*  for (int i = 0; i < 2 * count; i++) {
            String key = getRandomString();
            sourceStem.put(key, getRandomString());
            keys.put(key, codec.encode(getRandomString()));
        }
*/

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
            sourceStem.put(key,geter());
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

    QDLCodec codec = new QDLCodec();
    protected String enc(String x){
        return codec.encode(x);
    }
    protected String dec(String x){
        return codec.decode(x);
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

    @Test
    public void testJSON() throws Exception {
        // This is not a test. It is me debugging whether I want to try and figure out how to convert stems to
        // JSON objects. Could be handy, but it's quite a rats nest.
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        JSONArray array = new JSONArray();
        int count = 10;
        for (int i = 0; i < count; i++) {
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
        System.out.println(result);
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
        System.out.println(result);
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
        System.out.println(result);
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
        System.out.println(result);
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
}
