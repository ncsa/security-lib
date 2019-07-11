package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.CircularReferenceException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * This is a pre-processor for JSON objects.
 * The contract of this is as follows. A JSON object may have an attribute that is one of these directives, the
 * argument is then applied.
 * <h3>Example</h3>
 * <pre>
 *     {
 *         "foo":"bar",
 *         "#import":"id"
 *     }
 * </pre>
 * This means that the JSON with "id" is imported and 
 * <p>Created by Jeff Gaynor<br>
 * on 2/15/19 at  4:11 PM
 */
public class PreProcessor {
    public static String IMPORT_DIRECTIVE = "#import";
    JSONStore<? extends JSONEntry> store;

    public JSONStore<? extends JSONEntry> getStore() {
        return store;
    }

    public PreProcessor(JSONStore<? extends JSONEntry> store) {
        this.store = store;
    }

    /**
     * This will track the ids and throw an exception if there is cycle.
     *
     * @param array
     * @param ids
     * @return
     */
    protected JSON execute(JSONArray array, Set<String> ids) {
        if (array == null) {
            return null;
        }
        JSONArray newArray = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            Object obj = array.get(i);
            if (obj instanceof JSONObject) {
                JSON json = execute((JSONObject) obj, ids);
                if (json.isArray()) {

                    // if it's an array, add it in at this level.
                    JSONArray jsonArray = (JSONArray) json;
                    for (int j = 0; j < jsonArray.size(); j++) {
                        newArray.add(jsonArray.get(j));
                    }
                } else {
                    // just a JSON object, so add it
                    newArray.add(json);
                }
            } else {

                if (obj instanceof JSONArray) {
                    newArray.add(execute((JSONArray) obj, ids));
                } else {
                    newArray.add(obj);
                }
            }
        }
        return newArray;
    }

    /*
    The assumption is that this has a JSONObject of the form {directive:id}. If this resolves to an
    array, it is put into place.
     */
    public JSON execute(JSONArray array) {
        return execute(array, new HashSet<>());
    }

    public JSON execute(JSONObject object) {
        return execute(object, new HashSet<>());
    }

    public JSON execute(JSONObject object, Set<String> ids) {
        JSONObject newObject = new JSONObject();
        for (Object key : object.keySet()) {
            if (key.toString().equals(IMPORT_DIRECTIVE)) {
                Object storeKeyObject = object.get(key);
                Identifier storeKey = null;
                if (storeKeyObject instanceof Identifier) {
                    storeKey = (Identifier) storeKeyObject;
                } else {
                    storeKey = BasicIdentifier.newID(storeKeyObject.toString());
                }
                if(ids.contains(storeKey.toString())){
                    throw new CircularReferenceException("Error: The JSON object with id \"" + storeKey + "\" has already been referenced. This will cause cycle.");
                }
                ids.add(storeKey.toString());
                JSONEntry entry = getStore().get(storeKey);
                if (entry == null) {
                    // This implies that there is a directive but no corresponding store entry. 
                    // The contract is to return the object unaltered in that case.
                    return object;
                } else {
                    // then there is something to do...
                    if (entry.isArray()) {
                        return entry.getArray();
                    } else {
                        JSONObject storedObj = entry.getObject();
                        // The next snippet allows for over-rides: If there is already an existing key/value then
                        // it is preserved. The stored object never has the right of way.
                        storedObj = (JSONObject) execute(storedObj, ids); // return JSON object since that cannot change here.
                        for(Object key2 : storedObj.keySet()){
                            if(!newObject.containsKey(key2)){
                                newObject.put(key2, storedObj.get(key2));
                            }
                        }
                        //newObject.putAll(storedObj);
                    }
                }
            } else {
                Object temp = object.get(key);
                if (temp instanceof JSONObject) {
                    newObject.put(key, execute((JSONObject) temp, ids));

                } else {
                    newObject.put(key, temp);
                }
            }
        }
        return newObject;
    }

}
