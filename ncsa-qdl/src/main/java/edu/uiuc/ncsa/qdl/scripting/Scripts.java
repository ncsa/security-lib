package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  11:06 AM
 */
public class Scripts {

    /**
     * Identifies the JSON array of line sof code
     */
    public static final String CODE = "code";
    /**
     * Identifies this object
     */
    public static final String SCRIPT = "script";
    /**
     * Identifies a phase of execution where this may be invoked. The phases are defined on the server,
     * this merely provides a place to put it
     */
    public static final String EXEC_PHASE = "exec_phase";
    /**
     * The version of the language this script is compatible with.
     */
    public static final String LANG_VERSION = "lang_version";
    /**
     * The revision of this script.
     */
    public static final String SCRIPT_VERSION = "script_version";
    /**
     * The language this script is written in
     */
    public static final String LANGUAGE = "language";
    /**
     * ISO 8601 timestamp when this was created.
     */
    public static final String CREATE_TIME = "create_ts";

    public static final String LAST_MODIFIED = "last_modified";
    /**
     * (Required!) The identifier for this script.
     */
    public static final String ID = "id";

    /**
     * Returns a script object of the form {"script" : {"key0" : "value0",..., "code":[lines]}}
     * where the key and value pairs are information in the properties file.
     *
     * @param script
     * @param prop
     * @return
     */
    public static JSONObject toJSON(StringBuffer script, XProperties prop) {
        JSONObject top = new JSONObject();
        JSONObject jsonProps = new JSONObject();
        jsonProps.putAll(prop);
        String x = script.toString();
        StringTokenizer st = new StringTokenizer(x, "\n");
        JSONArray array = new JSONArray();
        while (st.hasMoreTokens()) {
            array.add(st.nextToken());
        }
        jsonProps.put(CODE, array);
        top.put(SCRIPT, jsonProps);
        return top;
    }
    public static JSONObject toJSON(QDLScript script) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( script.getText());
        return toJSON(buffer, script.getProperties());
    }

    public static QDLScript fromJSON(JSONObject rawJSON){
        JSONObject content = rawJSON.getJSONObject(SCRIPT);
        JSONArray code = content.getJSONArray(CODE);
        content.remove(CODE);
        XProperties xProperties= new XProperties();
        xProperties.putAll(content);
        ArrayList<String> lines = new ArrayList<>();
        for(int i =0; i < code.size(); i++){
            lines.add(code.getString(i));
        }
        QDLScript script = new QDLScript(lines, xProperties);
        return script;
    }
    public static void  main(String[] args){
        String f_body = "(192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \n" +
                 "     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\n" +
                 "   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \n" +
                 "     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \n" +
                 "     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4)";

         String g_x = "define[g(x)]body[return((x^2-1)/(x^4+1));];";
         String h_y = "define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];";
         String f_xy = "define[f(x,y)]body[return(" + f_body + ");];";
         StringBuffer script = new StringBuffer();
         script.append(g_x + "\n");
         script.append( h_y + "\n");
         script.append(f_xy + "\n");
         script.append("say(f(1.5,-2.1));");
         XProperties xp= new XProperties();
         xp.put(EXEC_PHASE, "init");
         xp.put(LANG_VERSION, QDLVersion.VERSION);
         xp.put(LANGUAGE,"qdl");
         xp.put(ID,"functions.qdl");
         xp.put(CREATE_TIME, Iso8601.date2String(new Date()));
         xp.put(LAST_MODIFIED, Iso8601.date2String(new Date()));
         QDLScript s = new QDLScript(new StringReader(script.toString()), xp);
         System.out.println(toJSON(s).toString(2));
         String rawJSON = "{\""+SCRIPT +"\": {\n" +
                 "  \""+LANG_VERSION+"\": \"1.0\",\n" +
                 "  \""+SCRIPT_VERSION +"\": \"2.0\",\n" +
                 "  \""+ EXEC_PHASE+ "\": \"init\",\n" +
                 "  \"" + ID + "\": \"functions.qdl\",\n" +
                 "  \"" + LANGUAGE +"\": \"qdl\",\n" +
                 "  \"" + CODE + "\":   [\n" +
                 "    \"define[g(x)]body[return((x^2-1)/(x^4+1));];\",\n" +
                 "    \"define[h(y)]body[return((3*y^3-2)/(4*y^2+y+1));];\",\n" +
                 "    \"define[f(x,y)]body[return((192*g(x)^3 + 192*g(x)^5 + 68*g(x)*h(y) + 216*g(x)^2*h(y) + \",\n" +
                 "    \"     68*g(x)^3*h(y) + 45*h(y)^2 + 192*g(x)^3*h(y)^2 + 68*g(x)*h(y)^3)/\",\n" +
                 "    \"   (384*g(x)^4 + 384*g(x)^6 + 280*g(x)^2*h(y) + 432*g(x)^3*h(y) + \",\n" +
                 "    \"     280*g(x)^4*h(y) + 21*h(y)^2 + 252*g(x)*h(y)^2 + 21*g(x)^2*h(y)^2 + \",\n" +
                 "    \"     384*g(x)^4*h(y)^2 + 280*g(x)^2*h(y)^3 + 21*h(y)^4));];\"\n" +
                 "  ]\n" +
                 "}}";
         QDLScript s2 = fromJSON(JSONObject.fromObject(rawJSON));
         System.out.println(s2);
    }
}
