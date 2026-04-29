package edu.uiuc.ncsa.security.util.json;

import org.kordamp.json.JSONArray;
import org.kordamp.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * In  The migration away from net.sf requires a little tweaking of the library,
 * since signatures have cnaged. This utility class centralizes these.
 */
public class MyJSONUtil {
    /**
     * Convert a JSON array to a list of strings. This will use the toString() method of the objects in the array.
     * @param array
     * @return
     */
    public static List<String> arraytoList(JSONArray array){
        List<String> out = new ArrayList<>(array.size());
        for(Object object : array){
            out.add(object.toString());
        }
        return out;
    }

    /**
     * For a {@link JSONArray} that is a member of a {@link JSONObject}, get it and convert it to a list
     * @param json
     * @param key
     * @return
     */
    public static List<String> arraytoList(JSONObject json, String key){
        return arraytoList(json.getJSONArray(key));
    }
}
