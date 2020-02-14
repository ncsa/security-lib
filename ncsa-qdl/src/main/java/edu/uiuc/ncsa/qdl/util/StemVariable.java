package edu.uiuc.ncsa.qdl.util;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.state.StemMultiIndex;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  12:09 PM
 */
public class StemVariable extends HashMap<String, Object> {
    // Convenience methods.
    public Long getLong(String key) {
        return (Long) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    /**
     * If this is set, then any get with no key will return this value. Since
     * the basic unit of QDL is the stem, this gives us a way of basically turning
     * a scalar in to a stem without having to do complicated size and key matching.
     * <br/><br/>
     * <b>Note</b> that {@link #containsKey(Object)} still works as usual, so you can ask
     * if a key exists.
     *
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    Object defaultValue;

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    @Override
    public Object get(Object key) {
        if (!containsKey(key) && defaultValue != null) {
            return defaultValue;
        }
        return super.get(key);
    }

    /**
     * Adds a list of objects to this stem, giving them indices 0,1,...
     * This is mostly a convenience for people writing in java to create lists
     * programatically
     *
     * @param list
     */
    public void addList(List<Object> list) {
        for (int i = 0; i < list.size(); i++) {
            put(Integer.toString(i), list.get(i));
        }
    }

    public Object get(StemMultiIndex w) {
        StemVariable currentStem = this;
        /**
         * Drill down, checking everything exists.
         */
        for (int i = 0; i < w.getComponents().length - 1; i++) {
            String name = w.getComponents()[i] + ".";
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("Error: Could not find the given index \"" + name + "\" in this stem \"" + w.getName() + "\".");
            }
            currentStem = nextStem;
        }
        // for last one. May be a variable or a stem
        if (w.isStem()) {
            return currentStem.get(w.getLastComponent() + ".");
        } else {
            return currentStem.get(w.getLastComponent());
        }
    }

    public void set(StemMultiIndex w, Object value) {
        StemVariable currentStem = this;
        /**
         * Drill down to next. If this is a completely new variable, may have to make all
         * the ones in between.
         */
        for (int i = 0; i < w.getComponents().length - 1; i++) {
            String name = w.getComponents()[i] + ".";
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                nextStem = new StemVariable();
                currentStem.put(name, nextStem);
            }
            currentStem = nextStem;
        }
        // for last one
        if (w.isStem()) {
            currentStem.put(w.getLastComponent() + ".", value);
        } else {
            currentStem.put(w.getLastComponent(), value);
        }
    }

    public void remove(StemMultiIndex w) {
        StemVariable currentStem = this;
        /**
         * Drill down, checking everything exists.
         */
        for (int i = 0; i < w.getComponents().length - 1; i++) {
            String name = w.getComponents()[i] + ".";
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("Error: Could not find the given index \"" + name + "\" in this stem \"" + w.getName() + "\".");
            }
            currentStem = nextStem;
        }
        // for last one. May be a variable or a stem
        if (w.isStem()) {
            currentStem.remove(w.getLastComponent() + ".");
        } else {
            currentStem.remove(w.getLastComponent());
        }

    }

    /**
     * Initialize a stem variable with all the same value. Very useful when floating a single value
     * to a stem.
     *
     * @param template
     * @param value
     * @return
     */
    public StemVariable fillStem(StemVariable template, Object value) {
        clear();
        for (String key : template.keySet()) {
            put(key, value);
        }
        return this;
    }

    /**
     * returns the set of keys common to this stem and its argument. The list may be empty.
     *
     * @param x
     * @return
     */
    public List<String> getCommonKeys(StemVariable x) {
        ArrayList<String> commonKeys = new ArrayList<>();
        for (String key : x.keySet()) {
            if (containsKey(key)) {
                commonKeys.add(key);
            }
        }
        return commonKeys;
    }

    /**
     * Make a shallow copy of this stem variable.
     *
     * @return
     */
    @Override
    public Object clone() {
        StemVariable output = new StemVariable();
        for (String key : keySet()) {
            output.put(key, get(key));
        }
        return output;
    }

    public StemVariable mask(StemVariable stem2) {
        StemVariable result = new StemVariable();

        for (String key : stem2.keySet()) {
            if (!containsKey(key)) {
                throw new IllegalArgumentException("Error: \"" + key + "\" is not a key in the first stem. " +
                        "Every key in the second argument must be a key in the first.");
            }
            Object rawBit = stem2.get(key);
            if (!(rawBit instanceof Boolean)) {
                throw new IllegalArgumentException("Error: Every value of the second argument must be boolean");
            }
            Boolean b = (Boolean) rawBit;
            if (b) {
                result.put(key, get(key));
            }
        }
        return result;
    }

    public StemVariable commonKeys(StemVariable arg2) {
        StemVariable result = new StemVariable();
        int index = 0;
        for (String key : arg2.keySet()) {
            if (containsKey(key)) {
                String currentIndex = Integer.toString(index++);
                result.put(currentIndex, key);
            }
        }
        return result;
    }

    public void renameKeys(StemVariable newKeys) {
        for (String key : newKeys.keySet()) {
            if (containsKey(key)) {
                String newKey = newKeys.getString(key);
                if (containsKey(newKey)) {
                    throw new IllegalArgumentException("Error: The current stem already has a key named \"" + newKeys.getString(key)
                            + "\". This operation does not replace values, it only renames existing keys.");
                }
                put(newKey, get(key));
                remove(key);
            }
        }
    }

    public StemVariable excludeKeys(StemVariable keyList) {
        StemVariable result = new StemVariable();
        for (String key : keySet()) {
            if (!keyList.containsValue(key)) {
                result.put(key, get(key));
            }
        }
        return result;
    }

    public StemVariable includeKeys(StemVariable keyList) {
        StemVariable result = new StemVariable();
        for (int i = 0; i < keyList.size(); i++) {
            // for loop to be sure that everything is done in order.
            String index = Integer.toString(i);
            if (!keyList.containsKey(index)) {
                throw new IllegalArgumentException("Error: the set of supplied keys is not a list");
            }
            String currentKey = keyList.getString(index);
            if (containsKey(currentKey)) {
                result.put(currentKey, get(currentKey));
            }
        }
        return result;
    }

    /**
     * Takes a stem list of keys and returns a boolean list conformable to the argument.
     *
     * @param keyList
     * @return
     */
    public StemVariable hasKeys(StemVariable keyList) {
        StemVariable result = new StemVariable();
        for (int i = 0; i < keyList.size(); i++) {
            // for loop to be sure that everything is done in order.
            String index = Integer.toString(i);
            if (!keyList.containsKey(index)) {
                throw new IllegalArgumentException("Error: the set of supplied keys is not a list");
            }
            result.put(index, containsKey(keyList.get(index).toString()));
        }
        return result;
    }

    /**
     * Convert this to JSON.
     *
     * @return
     */
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putAll(this);
        return jsonObject;
    }

    protected StemVariable convert(JSONObject object) {

        StemVariable out = new StemVariable();
        for (Object key : object.keySet()) {
            Object obj = object.get(key);

            out.put(key.toString(), convert(obj));
        }
        return out;
    }

    protected StemVariable convert(JSONArray array) {
        StemVariable out = new StemVariable();
        for (int i = 0; i < array.size(); i++) {
            out.put(Integer.toString(i), convert(array.get(i)));

        }
        return out;
    }

    /**
     * Does the grunt work of taking an entry from a JSON object and converting it to something QDL can
     * understand.
     *
     * @param obj
     * @return
     */
    protected Object convert(Object obj) {
        if (obj == null) return null;

        if (obj instanceof Integer) return new Long(obj.toString());
        if (obj instanceof Double) return new BigDecimal(obj.toString());
        if (obj instanceof Boolean) return obj;
        if (obj instanceof Long) return obj;
        if (obj instanceof Date) return Iso8601.date2String((Date) obj);
        if (obj instanceof String) return obj;
        if (obj instanceof JSONArray) return convert((JSONArray) obj);
        if (obj instanceof JSONObject) return convert((JSONObject) obj);
        return obj.toString();
    }

    /**
     * Populate this from a JSON object. Note that JSON arrays are turned in to stem lists.
     *
     * @param jsonObject
     */
    public void fromJSON(JSONObject jsonObject) {
        for (Object key : jsonObject.keySet()) {
            Object v = jsonObject.get(key);
            put(key.toString(), convert(v));
        }

    }

    public StemVariable union(StemVariable... stemVariables) {
        for (StemVariable stemVariable : stemVariables) {
            this.putAll(stemVariable);
        }
        return this;
    }
}
