package edu.uiuc.ncsa.security.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/1/18 at  10:14 AM
 */
public class TemplateUtility {
    /*
      The way that replacements are made is that a regex-friendly expression is created. The REGEX_*
      expressions are for internal consumption.

      Applications that need to use these, should use the LEFT_DELIMITER and RIGHT_DELIMITER
     */
    public static String REGEX_LEFT_DELIMITER = "\\$\\{";
    public static String REGEX_RIGHT_DELIMITER = "\\}";
    public static String LEFT_DELIMITER = "${";
    public static String RIGHT_DELIMITER = "}";

    /**
     * Simple-minded template replacement. This works well for small, simple  arguments.
     *
     * @param template
     * @param replacements
     * @return
     */
    public static String replaceAll(String template, Map replacements) {
        return newReplaceAll(template, replacements);
    }

    /*
    Need this split into separate method so it can be iterative called.
     */
    public static String rr(String template, Map replacements) {
        String out = template;
        for (Object key : replacements.keySet()) {
            String newKey = LEFT_DELIMITER + key.toString() + RIGHT_DELIMITER;
            if (replacements.containsKey(key) && (replacements.get(key) != null)) {
                out = out.replace(newKey, replacements.get(key).toString());
            }
        }
        return out;
    }

    /**
     * This handles nesting of the templates.
     * @param template
     * @param replacements
     * @return
     */
    public static String newReplaceAll(String template, Map replacements) {
        String out = template;
        if (out == null || out.length() == 0) {
            return out;
        }
        if (replacements == null || replacements.isEmpty()) {
            return out;
        }
        out = rr(template, replacements);
        String oldOut = null;
        String newOut = out;
        while (!newOut.equals(oldOut)) {
            oldOut = newOut;
            newOut = rr(newOut, replacements);
        }
        return newOut;
    }


    public static void main(String[] args) {
        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("ford", "fnord");
            map.put("tt", "${ford}o");
            map.put("uu", "abc${tt}def");
            String templace = "${uu}xyz";
            System.out.println(TemplateUtility.newReplaceAll(templace, map));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
