package edu.uiuc.ncsa.security.util.cli.json_edit;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Sert of static utilities for doing node surgery. Part of the issue is that the JSON
 * library we use creates copies of things so simply getting a node and then settign values on
 * it might not work.
 * <p>Created by Jeff Gaynor<br>
 * on 8/24/20 at  7:38 AM
 */
public class JSONNodeUtil {
    /**
     * Set the value of the node for the given path.
     *
     * @param source
     * @param path
     * @param value
     */
    public static void setNode(JSONObject source, String path, Object value) {
        setNode(source, JSONPaths.getComponents(path), value);
    }

    /**
     * Contract is that if the path is /a/b/.../c/d then the result is node d has the given value,
     * replacing what was there, if applicable.
     *
     * @param source
     * @param path
     * @param value
     */
    public static void setNode(JSONObject source, String[] path, Object value) {
        JSON current = source;
        for (int i = 0; i < path.length - 1; i++) {
            Object temp = null;
            if (current.isArray()) {
                temp = ((JSONArray) current).get(i);
            } else {
                temp = ((JSONObject) current).get(path[i]);
            }
            if (!(temp instanceof JSON)) {
                throw new IllegalArgumentException("Error: the component at \"" + path[i] + "\" is not JSON");
            }

            current = (JSON)temp;
        }
        // so we have navigated to the c in /a/b/.../c/d. We now have to make a decision about what to do
       if(current.isArray()){
           JSONArray array = (JSONArray)current;
          // rub is that you cannot just set an arbitrary index.
           int index = 0;
          try {
              index = Integer.parseInt(path[path.length - 1]);

          }catch(Throwable t){
              throw new IllegalArgumentException("Error: \"" + path[path.length-1] + "\" is not an integer index for this array");
          }
          if(0<= index && index <= array.size()){
                    array.set(index, value);
          }else{
              throw new IllegalArgumentException("Error: array index " + index + "\" out of bounds. Must be in range 0 <= index < " + array.size());

          }

       }else{
           JSONObject json = (JSONObject)current;
           json.put(path[path.length - 1], value);
       }
    }

    /**
     * Used in the case of /a/b.../d where d is an array. This appends the value as the next element of d.
     * @param source
     * @param path
     * @param value
     */
    public static void appendNode(JSONObject source, String[] path, Object value) {
                 
    }

    /**
     * Go to the given node and return
     *
     * @param source
     * @param path   - the raw path to the node
     * @return
     */
    public static JSON getNextToLastNode(JSONObject source, String path) {
        String[] parsedPath = JSONPaths.getComponents(path);
        return getNextToLastNode(source, parsedPath);

    }

    /**
     * This will return the next to last node for a pathh
     *
     * @param j
     * @param path
     * @return
     */
    public static JSON getNextToLastNode(JSON j, String[] path) {
        return getNodeDropIndex(j, path, 1); // drop last component
    }


    protected static JSON getNodeDropIndex(JSON j, String[] path, int numberOfComponentsToDrop) {
        for (int i = 0; i < path.length - numberOfComponentsToDrop; i++) {
            if ((j).isArray()) {
                j = ((JSONArray) j).getJSONObject(Integer.parseInt(path[i]));
            } else {
                j = (JSON) ((JSONObject) j).get(path[i]);
            }
            if (j == null) return null; // so component not found.
        }
        return j;

    }

    public static JSON getNode(JSON j, String[] path) {
        return getNodeDropIndex(j, path, 0); // return them all
    }


}