package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.evaluate.StemEvaluator;
import edu.uiuc.ncsa.qdl.evaluate.SystemEvaluator;
import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.scripting.ScriptSet;
import edu.uiuc.ncsa.security.util.scripting.ScriptingConstants;
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
public class AnotherJSONUtil implements ScriptingConstants {
    public static final String XMD_TAG = "xmd";
    public static final String ARGS_TAG = "args";


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
                        // Convert to a string then to a big decimal. Creating a BD from a double always introduces rounding errors.
                        rc.add(new BigDecimal(jsonArray.getString(i)));
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
                scripts.add(createScript(a.getJSONObject(i).getJSONObject("qdl")));
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
            // options are to add single values or arrays.
            JSONObject xmd = jsonObject.getJSONObject(XMD_TAG);
            for (Object key : xmd.keySet()) {
                Object object = xmd.get(key);
                if (object instanceof JSONArray) {
                    JSONArray array = (JSONArray) object;
                    String[] list = new String[array.size()];
                    for (int i = 0; i < array.size(); i++) {
                        list[i] = array.getString(i);
                    }
                    xp.setList(key.toString(), list);
                } else {
                    xp.put(key.toString(), object.toString());
                }
            }
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
            String rawArgs = "";
            if (jsonObject.containsKey(ARGS_TAG)) {
                rawArgs = buildArgList((JSON) jsonObject.get(ARGS_TAG));
            }

            String cmd;
            if (StringUtils.isTrivial(rawArgs)) {
                cmd = SystemEvaluator.LOAD_COMMAND + "(" + scriptName + ");";
            } else {
                cmd = SystemEvaluator.LOAD_COMMAND + "(" + scriptName + ", " + rawArgs + ");";
            }
            lines.add(cmd);
            qdlScript.setLines(lines);
            qdlScript.setRunScript(true);

            return qdlScript;
        }
        if (jsonObject.containsKey(Scripts.CODE)) {
            QDLScript script = null;
            // contents are either a single line or a set of lines.
            Object object = jsonObject.get(Scripts.CODE);

            if (object instanceof String) {
                lines.add(jsonObject.getString(Scripts.CODE));
                script = new QDLScript(lines, xp);
                script.setFromCode(true);
            }else{
                if (object instanceof JSONArray) {
                    lines.addAll(jsonObject.getJSONArray(Scripts.CODE));
                    script = new QDLScript(lines, xp);
                    script.setFromCode(true);
                }else{
                    throw new IllegalArgumentException("Error: Unknown code block type. Must be a string  or an array of strings");
                }
            }
            return script;
        }
        throw new IllegalArgumentException("Error: Unknown script type.");
    }

    protected static String buildArgList(JSON json) {
        String out = "";
        if (json.isArray()) {
            JSONArray array = (JSONArray) json;
            for (int i = 0; i < array.size(); i++) {
                Object obj = array.get(i);
                if (obj instanceof String) {
                    out = out + "'" + obj.toString() + "'";
                }
                if (obj instanceof Boolean || obj instanceof Integer || obj instanceof Double) {
                    out = out + obj.toString();
                }
                if (obj instanceof JSON) {
                    StemVariable stemVariable = new StemVariable();
                    if (obj instanceof JSONArray) {
                        stemVariable.fromJSON((JSONArray) obj);
                    } else {
                        stemVariable.fromJSON((JSONObject) obj);
                    }
                    out = out + StemEvaluator.FROM_JSON + "('" + stemVariable.toJSON() + "')";
                }

                if (i != array.size() - 1) {
                    out = out + ",";
                }
            }
        } else {
            out = StemEvaluator.FROM_JSON + "('" + json.toString() + "')";
        }
        // Do not build it as a list, pass the arguments so we are consistent
        // with script arguments across the board.
        //out = "to_list(" + out + ")";
        DebugUtil.trace(AnotherJSONUtil.class, "returned arg list =" + out);
        return out;
        // must be a stem list
    }

    public static void main(String[] args) {
        try {
            JSONObject j = JSONObject.fromObject(test4);
            System.out.println(j.toString(1));
            ScriptSet<QDLScript> scripts = createScripts((JSON) j.get("qdl"));
            scripts.get(SRE_EXEC_PHASE, SRE_POST_AT);
            System.out.println(scripts);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static String test0 = "{\"qdl\":{\"load\":\"x.qdl\"}}";
    static String test1 = "{\"qdl\":{\"code\":\"init();\"}}";
    static String test2 = "{\"qdl\":\n" +
            "   {\n" +
            "     \"load\":\"y.qdl\",\n" +
            "     \"xmd\":{\"phase\":[\"pre_auth\",\"post_refresh\"],\"token_type\":\"access\"},\n" +
            "     \"args\":[4,true,-47.5, {\"server\":\"localhost\",\"port\":443},[3,4]]\n" +
            "   }\n" +
            "}\n";
    static String test2a = "{\"qdl\":\n" +
            "   {\n" +
            "     \"load\":\"y.qdl\",\n" +
            "     \"xmd\":{\"phase\":\"pre_auth\",\"token_type\":\"id\"},\n" +
            "     \"args\":{\"port\":9443,\"verbose\":true,\"x0\":-47.5, \"ssl\":[3.5,true]},\n" +
            "     \"arg_name\":\"oa2\"\n" +
            "   }\n" +
            "}\n";
    static String test3 = "{\"qdl\":\n" +
            "     [\n" +
            "      {\"load\":\"init.qdl\", \"xmd\":{\"exec_phase\":\"pre_auth\"}},\n" +
            "      {\"load\":\"lsst.qdl\", \"xmd\":{\"exec_phase\":\"post_token\"}} \n" +
            "     ]\n" +
            "}";
    static String test4 = "{\"qdl\":\n" +
            "   {\n" +
            "     \"code\":\"init(true, 9443);\",\n" +
            "     \"xmd\":{\"phase\":\"" + ScriptingConstants.SRE_POST_ALL + "\",\"token_type\":\"refresh\"}\n" +
            "   }\n" +
            "}\n";

    static String test5 = "{\"qdl\":\n" +
            "   {\n" +
            "     \"code\":[\"init(9443);\",\"verbose(true);\"],\n" +
            "     \"xmd\":{\"phase\":\"pre_auth\",\"token_type\":\"scitoken\"}\n" +
            "   }\n" +
            "}\n";
}
