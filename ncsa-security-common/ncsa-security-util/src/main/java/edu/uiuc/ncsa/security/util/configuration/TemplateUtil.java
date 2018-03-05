package edu.uiuc.ncsa.security.util.configuration;

import edu.uiuc.ncsa.security.core.util.DebugUtil;

import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/1/18 at  10:14 AM
 */
public class TemplateUtil {
    public static String REGEX_LEFT_DELIMITER = "\\$\\{";
    public static String REGEX_RIGHT_DELIMITER = "\\}";

    /**
     * Simple-minded template replacement. This works well for small, simple  arguments.
     *
     * @param template
     * @param replacements
     * @return
     */
    public static String replaceAll(String template, Map replacements) {
        String out = template;
        if (out == null || out.length() == 0) {
            DebugUtil.dbg(TemplateUtil.class, "Empty template found, no message body or subject.");
            return out;
        }
        int count = 0;
        if(replacements == null || replacements.isEmpty()){
            return out;
        }
        for (Object key : replacements.keySet()) {
            // Have to properly escape the regex here.
            count++;
            String newKey = REGEX_LEFT_DELIMITER + key.toString() + REGEX_RIGHT_DELIMITER; // makes a reg ex.
            if (replacements.containsKey(key) && (replacements.get(key) != null)) {
                out = out.replaceAll(newKey, replacements.get(key).toString());
            }
        }
        DebugUtil.dbg(TemplateUtil.class, "made " + count + " replacements in the template");
        return out;
    }
}
