package edu.uiuc.ncsa.qdl.scripting;

import edu.uiuc.ncsa.qdl.util.QDLVersion;
import edu.uiuc.ncsa.security.core.configuration.XProperties;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.StringReader;
import java.util.StringTokenizer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/5/20 at  11:06 AM
 */
public class Scripts {
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
        jsonProps.put("code", array);
        top.put("script", jsonProps);
        return top;
    }
    public static JSONObject toJSON(QDLScript script) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( script.getText());
        return toJSON(buffer, script.getProperties());
    }

    public static QDLScript fromJSON(JSONObject rawJSON){
        JSONObject content = rawJSON.getJSONObject("script");
        JSONArray code = content.getJSONArray("code");
        content.remove("code");
        XProperties xProperties= new XProperties();
        xProperties.putAll(content);
        StringBuffer stringBuffer = new StringBuffer();
        for(int i =0; i < code.size(); i++){
            stringBuffer.append(code.getString(i) + "\n");
        }
        QDLScript script = new QDLScript(new StringReader(stringBuffer.toString()), xProperties);
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
         XProperties xp= new XProperties();
         xp.put("exec_phase", "init");
         xp.put("lang_version", QDLVersion.VERSION);
         xp.put("language","qdl");
         xp.put("id","functions.qdl");
         QDLScript s = new QDLScript(new StringReader(script.toString()), xp);
         System.out.println(toJSON(s).toString(2));
         String rawJSON = "{\"script\": {\n" +
                 "  \"lang_version\": \"1.0\",\n" +
                 "  \"exec_phase\": \"init\",\n" +
                 "  \"id\": \"functions.qdl\",\n" +
                 "  \"language\": \"qdl\",\n" +
                 "  \"code\":   [\n" +
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
