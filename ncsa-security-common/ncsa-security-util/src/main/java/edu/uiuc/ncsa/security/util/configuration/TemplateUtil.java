package edu.uiuc.ncsa.security.util.configuration;

import java.util.HashMap;
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
        return newReplaceAll(template, replacements);
    }
    public static String oldReplaceAll(String template, Map replacements) {
        String out = template;
        if (out == null || out.length() == 0) {
            return out;
        }
        int count = 0;
        if (replacements == null || replacements.isEmpty()) {
            return out;
        }
        for (Object key : replacements.keySet()) {
            // Have to properly escape the regex here.
            // Note that this does not permit nesting of templates, so while
            // ab${xx}c would get replaced correctly, ab${x${y}}c would not and you'd
            // get a complaint about grouping from the replaceAll method. This is because
            // the key of the hashmap is turned in to an actual regex with the right escaping
            //
            count++;
            String newKey = REGEX_LEFT_DELIMITER + key.toString() + REGEX_RIGHT_DELIMITER; // makes a reg ex.
            if (replacements.containsKey(key) && (replacements.get(key) != null)) {
                out = out.replaceAll(newKey, replacements.get(key).toString());
            }
        }
        return out;

    }



    /*
    Need this split into separate method so it can be iterative called.
     */
    public static String rr(String template, Map replacements) {
        String out = template;
        for (Object key : replacements.keySet()) {
            String newKey = "${" + key.toString() + "}";
            if (replacements.containsKey(key) && (replacements.get(key) != null)) {
                out = out.replace(newKey, replacements.get(key).toString());
            }
        }
        return out;
    }

    /**
     * Unlike {@link #oldReplaceAll(String, Map)} this can handle nesting of the templates.
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
            System.out.println(TemplateUtil.newReplaceAll(templace, map));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
