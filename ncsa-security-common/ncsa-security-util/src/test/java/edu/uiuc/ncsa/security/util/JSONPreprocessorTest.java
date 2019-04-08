package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.exceptions.CircularReferenceException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.util.json.JSONEntry;
import edu.uiuc.ncsa.security.util.json.JSONStore;
import edu.uiuc.ncsa.security.util.json.PreProcessor;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static edu.uiuc.ncsa.security.util.json.PreProcessor.IMPORT_DIRECTIVE;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/18/19 at  1:39 PM
 */
public class JSONPreprocessorTest extends TestBase {
    public static class TestMemStore<V extends JSONEntry> extends HashMap<Identifier, V> implements JSONStore<V> {

        @Override
        public List<V> getAll() {
            LinkedList<V> returnValues = new LinkedList<>();
            returnValues.addAll(values());
            return returnValues;
        }

        @Override
        public V create() {
            return null;
        }

        @Override
        public void update(V value) {

        }

        @Override
        public void register(V value) {

        }

        @Override
        public void save(V value) {

        }

        @Override
        public XMLConverter<V> getXMLConverter() {
            return null;
        }

        @Override
        public List<V> search(String key, String condition, boolean isRegEx) {
            return null;
        }
    }

    JSONStore store = null;
    Identifier ID_OBJECT = BasicIdentifier.newID("id:object/1");
    Identifier ID_ARRAY = BasicIdentifier.newID("id:array/1");
    Identifier ID_CIRCULAR = BasicIdentifier.newID("id:circular");
    Identifier ID_DEF = BasicIdentifier.newID("id:def");


    public void testDef() throws Exception{

    }

    protected void populateStore(JSONStore store) {
        JSONObject x = new JSONObject();
        x.put("X", "x");
        x.put("Y", "y");
        JSONEntry jex = new JSONEntry(ID_OBJECT);
        jex.setType(JSONEntry.TYPE_JSON_OBJECT);
        jex.setRawContent(x.toString());
        store.put(ID_OBJECT, jex);
        JSONArray array = new JSONArray();
        array.add("A");
        array.add("B");
        JSONEntry jey = new JSONEntry(ID_ARRAY);
        jey.setType(JSONEntry.TYPE_JSON_ARRAY);
        jey.setRawContent(array.toString());
        store.put(ID_ARRAY, jey);
        JSONObject circular = new JSONObject();
        JSONObject circular0 = new JSONObject();
        circular.put("A", "a");
        circular0.put("B", "b");
        circular0.put(IMPORT_DIRECTIVE, ID_CIRCULAR.toString());
        circular.put("X", circular0);
        JSONEntry jec = new JSONEntry(ID_CIRCULAR);
        jec.setType(JSONEntry.TYPE_JSON_OBJECT);
        jec.setRawContent(circular.toString());
        store.put(ID_CIRCULAR, jec);
        JSONArray jsonArray= new JSONArray();
        jsonArray.add("def test(A,B);");
        jsonArray.add("echo(getEnv('A'));");
        jsonArray.add("echo(getEnv('B'));");
        jsonArray.add("end_def;");
        JSONEntry jec2 = new JSONEntry(ID_DEF);
        jec2.setType(JSONEntry.TYPE_PROCEDURE);
        store.put(ID_DEF,jec2);
    }

    protected JSONStore getStore() {
        if (store == null) {
            store = new TestMemStore();
            populateStore(store);
        }
        return store;
    }

    @Test
    public void testBasicArray() throws Exception {
        JSONArray array = new JSONArray();
        array.add("C");
        array.add(createImportStmt(ID_ARRAY));
        PreProcessor pp = createPP();
        JSON jj = pp.execute(array);
        assert jj.isArray();
        JSONArray newArray = (JSONArray) jj;
        assert newArray.contains("A");
        assert newArray.contains("B");
        assert newArray.contains("C");
        assert !newArray.contains(IMPORT_DIRECTIVE) : "Error: import directive statmemt not removed";

    }

    public void testBasicObject() throws Exception {
        PreProcessor pp = createPP();
        JSONObject test = new JSONObject();
        test.put("Z", "z");
        // control the level this is put in. If it is a key/value pair, all the value are imported to the top level
        test.put(IMPORT_DIRECTIVE, ID_OBJECT.toString());
        JSONObject zz = (JSONObject) pp.execute(test);
        assert zz.containsKey("X");
        assert zz.containsKey("Y");
        assert zz.containsKey("Z");
        assert !zz.containsKey(IMPORT_DIRECTIVE) : "Error: import directive statmemt not removed";
    }

    /**
     * A JSON object that has an array imported as the value of its key
     *
     * @throws Exception
     */
    public void testArrayInJSON() throws Exception {
        PreProcessor pp = createPP();
        JSONObject test = new JSONObject();
        test.put("Z", createImportStmt(ID_ARRAY));

        JSONObject zz = (JSONObject) pp.execute(test);
        assert zz.containsKey("Z");
        JSONArray array = zz.getJSONArray("Z");
        assert array.contains("A");
        assert array.contains("B");
        assert !zz.containsKey(IMPORT_DIRECTIVE) : "Error: import directive statmemt not removed";
    }

    /**
     * An array that has a JSON object as one of its elements.
     *
     * @throws Exception
     */
    public void testJSONInArray() throws Exception {
        JSONArray array = new JSONArray();
        array.add(createImportStmt(ID_OBJECT));
        PreProcessor pp = createPP();
        JSON output = pp.execute(array);
        assert output.isArray();
        JSONArray returnedArray = (JSONArray) output;
        assert returnedArray.size() == 1;
        JSONObject returnedObject = returnedArray.getJSONObject(0);
        assert returnedObject.containsKey("X");
        assert returnedObject.containsKey("Y");
    }

    public void testObjectNesting() throws Exception {
        String key0 = "A";
        String key1 = "B";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put(IMPORT_DIRECTIVE, ID_OBJECT.toString());
        jsonObject1.put(key1, jsonObject2);
        jsonObject.put(key0, jsonObject1);
        System.out.println(jsonObject.toString(2));
        PreProcessor pp = createPP();
        JSONObject returnedObject = (JSONObject) pp.execute(jsonObject);
        assert returnedObject.containsKey(key0);
        JSONObject obj1 = returnedObject.getJSONObject(key0);
        assert obj1.containsKey(key1);
        JSONObject obj2 = obj1.getJSONObject(key1);
        assert obj2.containsKey("X");
        assert obj2.containsKey("Y");
        System.out.println(returnedObject.toString(2));
    }

    /**
     * Shows that an import nested inside arrays is resolved correctly. So [[[{#import:id}]]]
     * yields [[[A,B]]]
     *
     * @throws Exception
     */
    public void testArrayNesting() throws Exception {
        JSONArray array0 = new JSONArray();
        JSONArray array1 = new JSONArray();
        JSONArray array2 = new JSONArray();
        array2.add(createImportStmt(ID_ARRAY));
        array1.add(array2);
        array0.add(array1);
        System.out.println(array0.toString(2));
        PreProcessor pp = createPP();

        JSONArray outputArray = (JSONArray) pp.execute(array0);
        System.out.println(outputArray.toString(2));
        JSONArray z = outputArray.getJSONArray(0).getJSONArray(0); // nested 2 deep.
        assert z.contains("A");
        assert z.contains("B");
    }

    /**
     * In this test, a directive for a non-existent ID is sent. The effect should be that there is no substitution made.
     *
     * @throws Exception
     */
    public void testNoEntryInObject() throws Exception {
        String key0 = "A";
        String key1 = "B";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        String fakeID = "fake:id";
        jsonObject2.put(IMPORT_DIRECTIVE, fakeID);
        jsonObject1.put(key1, jsonObject2);
        jsonObject.put(key0, jsonObject1);
        System.out.println(jsonObject.toString(2));
        PreProcessor pp = createPP();
        JSONObject returnedObject = (JSONObject) pp.execute(jsonObject);
        assert returnedObject.containsKey(key0);
        JSONObject obj1 = returnedObject.getJSONObject(key0);
        assert obj1.containsKey(key1);
        JSONObject obj2 = obj1.getJSONObject(key1);
        assert obj2.containsKey(IMPORT_DIRECTIVE);
        assert obj2.getString(IMPORT_DIRECTIVE).equals(fakeID);
        System.out.println(returnedObject.toString(2));
    }

    /**
     * Similar to the above, there is no entry for the given id in an array, so the directive is returned unaltered
     *
     * @throws Exception
     */
    public void testNoEntryInArray() throws Exception {
        JSONArray array0 = new JSONArray();
        JSONArray array1 = new JSONArray();
        JSONArray array2 = new JSONArray();
        String fakeID = "fake:id";
        array2.add(createImportStmt(BasicIdentifier.newID(fakeID)));
        array1.add(array2);
        array0.add(array1);
        System.out.println(array0.toString(2));
        PreProcessor pp = createPP();

        JSONArray outputArray = (JSONArray) pp.execute(array0);
        System.out.println(outputArray.toString(2));
        JSONObject z = outputArray.getJSONArray(0).getJSONArray(0).getJSONObject(0); // nested 2 deep.
        assert z.containsKey(IMPORT_DIRECTIVE);
        assert z.getString(IMPORT_DIRECTIVE).equals(fakeID);
    }

    /**
     * This test has a cycle in it, i.e., the id to be resolved against the store has a reference to itself
     * embedded in it. The proper behavior is to throw an exception when this happens.
     *
     * @throws Exception
     */
    public void testObjectCycle() throws Exception {
        String key0 = "A";
        String key1 = "B";
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put(IMPORT_DIRECTIVE, ID_CIRCULAR.toString());
        jsonObject1.put(key1, jsonObject2);
        jsonObject.put(key0, jsonObject1);
        System.out.println(jsonObject.toString(2));
        PreProcessor pp = createPP();
        try {
            JSONObject returnedObject = (JSONObject) pp.execute(jsonObject);
            assert false : "Should have found a cycle but did not.";
        } catch (CircularReferenceException cx) {
            assert true;
        }
    }

    /**
     * Similar to the object cycle test, this checks that there are no cycles in any stored JSON.
     *
     * @throws Exception
     */
    public void testArrayCycle() throws Exception {
        JSONArray array0 = new JSONArray();
        JSONArray array1 = new JSONArray();
        JSONArray array2 = new JSONArray();
        array2.add(createImportStmt(ID_CIRCULAR));
        array1.add(array2);
        array0.add(array1);
        System.out.println(array0.toString(2));
        PreProcessor pp = createPP();

        try {
            JSONArray outputArray = (JSONArray) pp.execute(array0);
            assert false : "Should have found a cycle but did not.";
        } catch (CircularReferenceException cx) {
            assert true;
        }
    }

    public void testObjectOverride() throws Exception {
        PreProcessor pp = createPP();
        JSONObject test = new JSONObject();

        test.put("X", "a");
        test.put("Z", "z");
        // Do this last since if the values are going to get over written by the stored object, it will be
        // because the stored object was retrieved after all other processing.
        test.put(IMPORT_DIRECTIVE, ID_OBJECT.toString());
        // control the level this is put in. If it is a key/value pair, all the value are imported to the top level
        JSONObject zz = (JSONObject) pp.execute(test);
        assert zz.containsKey("X");
        assert zz.getString("X").equals("a");
        assert zz.containsKey("Y");
        assert zz.containsKey("Z");
        assert !zz.containsKey(IMPORT_DIRECTIVE) : "Error: import directive statmemt not removed";
    }

    /**
     * the way arrays handle this is that, being lists, the redundant values are just added.
     * In this case, the result after import is <code>[A,B,A,B]</code>
     * @throws Exception
     */
    public void testArrayOverride() throws Exception {
          JSONArray array = new JSONArray();
          array.add("A");
          array.add("B");
          array.add(createImportStmt(ID_ARRAY));
          PreProcessor pp = createPP();
          JSON jj = pp.execute(array);
          assert jj.isArray();
          JSONArray newArray = (JSONArray) jj;
          assert newArray.size() == 4;
          for(int i = 0; i < 4; i++){
              assert newArray.getString(i).equals("A") || newArray.getString(i).equals("B");
        }
          assert !newArray.contains(IMPORT_DIRECTIVE) : "Error: import directive statmemt not removed";

      }
    protected JSONObject createImportStmt(Identifier id) {
        JSONObject importStmt = new JSONObject();
        importStmt.put(IMPORT_DIRECTIVE, id.toString());
        return importStmt;
    }

    protected PreProcessor createPP() {
        return new PreProcessor(getStore());
    }
}
