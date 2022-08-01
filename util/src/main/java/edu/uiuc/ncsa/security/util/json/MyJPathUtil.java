package edu.uiuc.ncsa.security.util.json;

import edu.uiuc.ncsa.security.util.cli.json_edit.JSONPaths;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;

import static edu.uiuc.ncsa.security.util.cli.json_edit.JSONPaths.normalize;
import static edu.uiuc.ncsa.security.util.cli.json_edit.JSONPaths.resolve;
 /*
    Keep! Used in OA4MP along with stuff in cli/json_edit
  */
/**
 * A utility for navigating JSON objects. You provide an absolute path or a root and a relative path.
 * Note that arrays are treated consistently, so if /a is the path to the array, /a/0 is the zeroth element.
 * so
 * <pre>
 *     getObject(obj, "/foo/bar")
 * </pre>
 * gets the object <code>foo→bar</code>. This does permit appending to an array, so if the array is at
 * /a/b/array then
 * <pre>
 *     append(json, "/a/b/array", newValue)
 * </pre>
 * would append the value of newValue to the array.
 * <p>Created by Jeff Gaynor<br>
 * on 9/9/20 at  9:25 AM
 */
public class MyJPathUtil {
    protected static final String TYPE_JSON_OBJECT = "json_object";
    protected static final String TYPE_JSON_ARRAY = "json_array";
    protected static final String TYPE_INTEGER = "int";
    protected static final String TYPE_LONG = "long";
    protected static final String TYPE_STRING = "string";
    protected static final String TYPE_DATE = "date";


    protected static Object get(JSON json, String absPath) {
        absPath = JSONPaths.normalize(absPath);
        Object result = null;
        String paths[] = JSONPaths.getComponents(absPath);
        JSON current = json;
        for (int i = 0; i < paths.length - 1; i++) {
            current = getJSON(current, paths[i]);
            if (current == null) {
                return null;
            }
        }
        // current may be a JSONObject or an array.
        String key = paths[paths.length - 1]; //last component
        if (current.isArray()) {
            try {
                int index = Integer.parseInt(key);
                result = ((JSONArray) current).get(index);

            } catch (NumberFormatException nx) {
                throw new JSONValueNotFoundException("Error: \"" + key + "\" is not an integer");
            }
        } else {

            result = ((JSONObject) current).get(key);
        }

        return result;
    }

    /**
     * Since the API we use is hinky about arrays and objects, this fronts the operation.
     * Note that this is not used on terminal nodes, merely intermediate ones. So in the path
     * <pre>
     *     /a/b/c/d/e
     * </pre>
     * It can be invoked safely on <code>a -&gt; b -&gt; c -&gt; d</code> but e generally will require the specific type
     * (int, long, etc.) and is not JSON.
     *
     * @param current
     * @param key
     * @return
     */
    protected static JSON getJSON(JSON current, String key) {

        Object obj = null;
        if (current.isArray()) {
            obj = ((JSONArray) current).get(intKey(key));
        } else {
            obj = ((JSONObject) current).get(key);
        }
        if (obj instanceof JSON) {
            return (JSON) obj;
        }
        return null;
    }

    static JSONPaths jsonPaths = new JSONPaths();

    public static JSONObject getJSONObject(JSON json, String root, String relativePath) {
        return getJSONObject(json, resolve(root, relativePath));
    }

    public static JSONObject getJSONObject(JSON json, String absPath) {
        return (JSONObject) get(json, absPath);
    }


    public static JSONArray getJSONArray(JSON json, String root, String relativePath) {
        return getJSONArray(json, resolve(root, relativePath));
    }

    public static JSONArray getJSONArray(JSON json, String absPath) {
        return (JSONArray) get(json, absPath);
    }

    public static Integer getInt(JSON json, String root, String relativePath) {
        return getInt(json, resolve(root, relativePath));
    }

    public static Integer getInt(JSON json, String absPath) {
        return (Integer) get(json, absPath);
    }

    public static Long getLong(JSON json, String root, String relativePath) {
        return getLong(json, resolve(root, relativePath));
    }

    public static Long getLong(JSON json, String absPath) {
        return (Long) get(json, absPath);
    }

    public static String getString(JSON json, String root, String relativePath) {
        return getString(json, resolve(root, relativePath));
    }

    public static String getString(JSON json, String absPath) {
        return (String) get(json, absPath);
    }

    public static Date getDate(JSON json, String root, String relativePath) {
        return getDate(json, resolve(root, relativePath));
    }

    public static Date getDate(JSON json, String absPath) {
        return (Date) get(json, absPath);
    }

    protected static int intKey(String key) {
        try {
            return Integer.parseInt(key);
        } catch (NumberFormatException nx) {
            throw new JSONValueNotFoundException("Error: \"" + key + "\" is not an integer");
        }

    }


    public static void append(JSON json, String root, String relativePath, Object newValue) {
        append(json, resolve(root, relativePath), newValue);
    }

    public static void append(JSON json, String absPath, Object newValue) {
        getJSONArray(json, absPath).add(newValue);
    }

    public static void set(JSON json, String root, String relativePath, Object newValue) {
        set(json, resolve(root, relativePath), newValue);
    }

    public static void set(JSON json, String absPath, Object newValue) {
        absPath = JSONPaths.normalize(absPath); // in case it has cruft like /a/b/c/../d in it
        String paths[] = JSONPaths.getComponents(absPath);
        JSON current = json;
        for (int i = 0; i < paths.length - 1; i++) {
            current = getJSON(current, paths[i]);
        }
        String key = paths[paths.length - 1];
        if (current.isArray()) {
            JSONArray array = (JSONArray) current;
            int index = intKey(key);
            if (array.size() <= index) {
                array.add(newValue);
            } else {
                array.set(index, newValue);
            }
        } else {
            ((JSONObject) current).put(key, newValue);
        }
    }

    public static boolean containsKey(JSON json, String root, String relativePath) {
        return containsKey(json, resolve(root, relativePath));
    }

    public static boolean containsKey(JSON json, String absPath) {
        absPath = normalize(absPath);
        return null != get(json, absPath);
    }

    public static void main(String[] args) {
        String test = "{\"a\":{\"b\":{\"c\":1}, \"d\":[\"p\",\"q\",\"r\"]}}";
        JSONObject json = JSONObject.fromObject(test);
        System.out.println(json.toString(2));
        System.out.println(containsKey(json, "/a/z/q"));
        System.out.println(getInt(json, "/a/b/c"));
        System.out.println(getJSONArray(json, "/a/d"));
        System.out.println(getString(json, "/a/d/1"));
        set(json, "/a/d/1", 4);
        append(json, "/a/d", "X");
        System.out.println(getInt(json, "/a/d/1"));
        set(json, "/a/b/Z", JSONObject.fromObject("{\"zzz\":\"aaa\"}"));
        System.out.println("after setting values:\n" + json.toString(2));

    }
}
