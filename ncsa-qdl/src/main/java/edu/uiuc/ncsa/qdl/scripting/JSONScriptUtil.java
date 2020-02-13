package edu.uiuc.ncsa.qdl.scripting;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * writes a set of scripts to a configuration object.
 * <p>Created by Jeff Gaynor<br>
 * on 2/12/20 at  2:01 PM
 */
public class JSONScriptUtil {
    public static String SCRIPTS_TAG = "scripts";

    public static JSONObject createConfig() {
        JSONObject jsonObject = new JSONObject();
        JSONArray scripts = new JSONArray();
        jsonObject.put(SCRIPTS_TAG, scripts);
        return jsonObject;
    }

    public static void addScript(JSONObject config, QDLScript script){
        JSONObject j = Scripts.toJSON(script);
         config.getJSONArray(SCRIPTS_TAG).add(j);
    }

}
