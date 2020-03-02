package edu.uiuc.ncsa.qdl.scripting;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static edu.uiuc.ncsa.qdl.vfs.FileEntryConstants.ID;

/**
 * Writes a set of scripts to a configuration object. So this gets a {@link QDLScript} and
 * converts it to JSON, then places it in a JSON scripts object.
 * <p>Created by Jeff Gaynor<br>
 * on 2/12/20 at  2:01 PM
 */
public class JSONScriptUtil {
    public static String SCRIPTS_TAG = "scripts";

    /**
     * Creates a brand new, empty scripts entry for the QDL configuration.
     *
     * @return
     */
    public static JSONObject createConfig() {
        JSONObject jsonObject = new JSONObject();
        JSONArray scripts = new JSONArray();
        jsonObject.put(SCRIPTS_TAG, scripts);
        return jsonObject;
    }

    /**
     * Add a single {@link QDLScript} to the set of JSON object of scripts. This gets (minimally)
     * <pre>
     *      {"scripts":[script,script,...]}
     *  </pre>
     * (so if there are other tags, it ignores them and only works with this one tag)
     * Note that it will replace the same named script.
     *
     * @param scripts
     * @param script
     */
    public static JSONObject addScript(JSONObject scripts, QDLScript script) {
        JSONObject j = Scripts.toJSON(script);
        return addScript(scripts, j);
    }

    /**
     * Adds the already serialized script to the configuration object  So first arg input is
     * <pre>
     *     {"key0":"value0","key1":"value1",...,"scripts":[ array of scripts ]}
     * </pre>
     * and the second argument is just a script to go  the array
     * <pre>
     *     {"script":{"id":..., ... }}
     * </pre>
     * This should only update the scripts' array
     *
     * @param scripts
     * @param j
     * @return updated first argument
     */
    public static JSONObject addScript(JSONObject scripts, JSONObject j) {
        if (scripts.isNullObject()) {
            // then this is a null object.
            scripts = new JSONObject();
        }
        if (!scripts.containsKey(SCRIPTS_TAG)) {
            JSONArray array = new JSONArray();
            scripts.put(SCRIPTS_TAG, array);

        }
        JSONArray array = scripts.getJSONArray(SCRIPTS_TAG);
        String id = j.getJSONObject(Scripts.SCRIPT).getString(ID);
        for (int i = 0; i < array.size(); i++) {
            Object obj = array.get(i);
            if (obj instanceof JSONObject) {
                JSONObject currentEntry = (JSONObject) obj;
                if (currentEntry.containsKey(Scripts.SCRIPT)) {
                    JSONObject currentScript = currentEntry.getJSONObject(Scripts.SCRIPT);
                    if (currentScript.containsKey(ID)) {
                        String currentID = currentScript.getString(ID);
                        if (currentID.equals(id)) {
                            array.set(i, j);
                            scripts.put(SCRIPTS_TAG, array);
                            return scripts;
                        }
                    }
                }

            }

        }
        array.add(j); // not found, append it.
        scripts.put(SCRIPTS_TAG, array);
        return scripts;
    }

    /**
     * removes a script from a set with a given id. So input is
     * <pre>
     *     {"scripts":{[
     *       {"script":{"id0":...}},
     *       {"script":{"id1":...}},
     *       {"script":{"id2":...}},...
     *       ]}}
     * </pre>
     * And if e.g. "id1" is passed in, it is removed.
     *
     * @param scripts
     * @param id
     */
    public JSONObject removeScript(JSONObject scripts, String id) {
        JSONArray array = scripts.getJSONArray(SCRIPTS_TAG);
        for (int i = 0; i < array.size(); i++) {
            if (array.getJSONObject(i).containsKey(ID)) {
                String currentID = array.getJSONObject(i).getString(ID);
                if (currentID.equals(id)) {
                    array.remove(i);
                    // Because our JSON lib copies things and removing the value does not
                    // change the original
                    scripts.put(SCRIPTS_TAG, array);
                    return scripts;
                }
            }
        }
        return scripts;
    }

    public static void main(String[] args) {
        JSONObject scripts = JSONObject.fromObject("{\"A\":\"A-entry\",\"B\":\"B-entry\",\n" +
                "  \"scripts\": [\n" +
                "  {\"script\":   {\n" +
                "    \"lang_version\": \"1.0\",\n" +
                "    \"exec_phase\": \"pre_auth\",\n" +
                "    \"script_version\": \"1.0\",\n" +
                "    \"language\": \"qdl\",\n" +
                "    \"id\": \"pre_auth.qdl\",\n" +
                "    \"code\": [\"claims.uid := 'jgaynor';\"]\n" +
                "  }},\n" +
                "  {\"script\":   {\n" +
                "    \"lang_version\": \"1.0\",\n" +
                "    \"exec_phase\": \"post_token\",\n" +
                "    \"script_version\": \"1.0\",\n" +
                "    \"language\": \"qdl\",\n" +
                "    \"id\": \"post_token.qdl\",\n" +
                "    \"code\": [\"if[  in_group(claims.isMemberOf., 'all_users')]then[  claims.check := 'in group';]else[  claims.check := 'not in group';]; //end if\"]\n" +
                "  }}\n" +
                "]}");
        JSONObject script = JSONObject.fromObject(" {\"script\":   {\n" +
                "    \"lang_version\": \"1.0\",\n" +
                "    \"exec_phase\": \"post_token\",\n" +
                "    \"script_version\": \"1.0\",\n" +
                "    \"language\": \"qdl\",\n" +
                "    \"id\": \"post_token.qdl\",\n" +
                "    \"code\": [\"if[  in_group(claims.isMemberOf., 'all_users')]then[  claims.check := 'in group';]else[  claims.check := 'not in group2';]; //end if\"]\n" +
                "  }}");
        addScript(scripts, script);
    }
}
