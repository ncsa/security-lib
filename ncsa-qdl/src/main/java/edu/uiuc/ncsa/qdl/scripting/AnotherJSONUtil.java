package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.evaluate.ControlEvaluator;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.util.scripting.ScriptSet;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/1/20 at  3:52 PM
 */
public class AnotherJSONUtil {
    public static final String XMD_TAG = "xmd";
    public static final String ARGS_TAG = "args";
    public static final String ARG_NAME_TAG = "arg_name";


    /**
     * Contract is: Passing in a {@link net.sf.json.JSONObject}  means this has a single argument which is a stem.
     * Passing in a {@link net.sf.json.JSONArray} means this is an array of objects.
     *
     * @param json
     * @return
     */
    public static List<Object> toScriptArgs(JSON json) {
        List<Object> rc = null;
        if (json.isArray()) {
            JSONArray jsonArray = (JSONArray) json;
            rc = new ArrayList<Object>();
            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i) instanceof JSON) {
                    StemVariable s = new StemVariable();
                    if (jsonArray.get(i) instanceof JSONObject) {
                        s.fromJSON(jsonArray.getJSONObject(i));
                    } else {
                        s.fromJSON(jsonArray.getJSONArray(i));
                    }
                    rc.add(s);
                } else {
                    Object ooo = jsonArray.get(i);
                    if (ooo instanceof Integer || ooo instanceof Long) {
                        rc.add(jsonArray.getLong(i));
                    }
                    if (ooo instanceof Double) {
                        rc.add(new BigDecimal(jsonArray.getDouble(i)));
                    }
                    if (ooo instanceof Boolean || ooo instanceof String) {
                        rc.add(ooo);
                    }
                }
            }
        } else {
            StemVariable stemVariable = new StemVariable();
            stemVariable.fromJSON((JSONObject) json);
            rc = new ArrayList<>();
            rc.add(stemVariable);
        }
        return rc;
    }

    /**
     * Create all the scripts for this configuration. This is the main entry point for this class.
     *
     * @param topNode
     * @return
     */
    public static ScriptSet<QDLScript> createScripts(JSON topNode) {

        ScriptSet<QDLScript> scripts = new ScriptSet<>();
        if (topNode.isArray()) {
            JSONArray a = (JSONArray) topNode;
            for (int i = 0; i < a.size(); i++) {
                scripts.add(createScript(a.getJSONObject(i)));
            }
        } else {
            scripts.add(createScript((JSONObject) topNode));
        }
        return scripts;
    }

    /**
     * Every configured elements has a set of meta-data about processing. Get it from the configuration
     * and return it in an {@link XProperties} object.
     *
     * @param jsonObject
     * @return
     */
    protected static XProperties processMD(JSONObject jsonObject) {
        XProperties xp = new XProperties();
        xp.put(Scripts.LANGUAGE, QDLVersion.LANGUAGE_NAME);
        xp.put(Scripts.LANG_VERSION, QDLVersion.VERSION);

        if (jsonObject.containsKey(XMD_TAG)) {
            xp.add(jsonObject.getJSONObject(XMD_TAG), true);
            return xp;
        }
        return xp;
    }

    /**
     * Create an individual script.
     *
     * @param jsonObject
     * @return
     */
    protected static QDLScript createScript(JSONObject jsonObject) {
        // Everything should have a metadata block.
        XProperties xp = processMD(jsonObject);

        QDLScript qdlScript = null;
        List<String> lines = new ArrayList<>();

        boolean ok = false;
        if (jsonObject.containsKey(Scripts.RUN)) {
            //create a script
            qdlScript = new QDLScript((List<String>) null, xp);

            String scriptName = jsonObject.getString(Scripts.RUN).trim();
            if (!scriptName.startsWith("'")) {
                scriptName = "'" + scriptName;
            }
            if (!scriptName.endsWith("'")) {
                scriptName = scriptName + "'";
            }
            StemVariable argList = new StemVariable();
            if (jsonObject.containsKey(ARGS_TAG)) {
                argList.addList(toScriptArgs((JSON) jsonObject.get(ARGS_TAG)));

            }
            String argName = QDLScript.DEFAULT_ARG_NAME;
            if (jsonObject.containsKey(ARG_NAME_TAG)) {
                argName = jsonObject.getString(ARG_NAME_TAG);
                qdlScript.setScriptArgName(argName);
            }
            String cmd;
            if (argList.isEmpty()) {
                cmd = ControlEvaluator.LOAD_COMMAND + "(" + scriptName + ");";
            } else {
                cmd = ControlEvaluator.LOAD_COMMAND + "(" + scriptName + ", " + argName + ");";
                qdlScript.setScriptArglist(argList);
            }
            lines.add(cmd);
            qdlScript.setLines(lines);
            qdlScript.setRunScript(true);

            return qdlScript;
        }
        if (jsonObject.containsKey(Scripts.CODE)) {
            // contents are either a single line or a set of lines.
            Object object = jsonObject.get(Scripts.CODE);
            if (object instanceof String) {
                lines.add(jsonObject.getString(Scripts.CODE));
                return new QDLScript(lines, xp);
            }
            if (object instanceof JSONArray) {
                lines.addAll(jsonObject.getJSONArray(Scripts.CODE));
                return new QDLScript(lines, xp);
            }
        }
        throw new IllegalArgumentException("Error: Unknown script type.");
    }

    public static void main(String[] args) {
        try {
            JSONObject j = JSONObject.fromObject(test2);
            System.out.println(j.toString(2));
            ScriptSet<QDLScript> scripts = createScripts((JSON) j.get("qdl"));
            System.out.println(scripts);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static String test0 = "{\"qdl\":{\"run\":\"x.qdl\"}}";
    static String test1 = "{\"qdl\":{\"code\":\"init();\"}}";
    static String test2 = "{\"qdl\":\n" +
            "   {\n" +
            "     \"run\":\"y.qdl\",\n" +
            "     \"xmd\":{\"phase\":\"pre_auth\",\"token_type\":\"wlcg\"},\n" +
            "     \"args\":[4,true,{\"server\":\"localhost\",\"port\":443}],\n" +
            "     \"arg_name\":\"oa2\"\n" +
            "   }\n" +
            "}\n";

    static String test3 = "{\"qdl\":\n" +
            "     [\n" +
            "      {\"run\":\"init.qdl\", \"xmd\":{\"exec_phase\":\"pre_auth\"}},\n" +
            "      {\"run\":\"lsst.qdl\", \"xmd\":{\"exec_phase\":\"post_token\"}} \n" +
            "     ]\n" +
            "}";
    static String test4 = "{\"qdl\":\n" +
            "   {\n" +
            "     \"code\":\"y.qdl\",\n" +
            "     \"xmd\":{\"phase\":\"pre_auth\",\"token_type\":\"scitoken\"}\n" +
            "   }\n" +
            "}\n";
}
