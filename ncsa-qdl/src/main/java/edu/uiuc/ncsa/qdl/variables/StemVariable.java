package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.state.StemMultiIndex;
import edu.uiuc.ncsa.security.core.util.Iso8601;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

import static edu.uiuc.ncsa.qdl.state.SymbolTable.var_regex;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  12:09 PM
 */
public class StemVariable extends HashMap<String, Object> {
    public static final String STEM_INDEX_MARKER = ".";

    public StemList<StemEntry> getStemList() {
        if (stemList == null) {
            stemList = new StemList();
        }
        return stemList;
    }

    public void setStemList(StemList<StemEntry> stemList) {
        this.stemList = stemList;
    }

    StemList<StemEntry> stemList;

    // Convenience methods.
    public Long getLong(String key) {
        return (Long) get(key);
    }

    public String getString(String key) {
        return get(key).toString();
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

    public Object get(String key) {
        // TODO -- Horribly inefficient. This should be improved but that may take some serious work, so deferring
        if (isLongIndex(key)) {
            return get(Long.parseLong(key));
        }

        if (!containsKey(key) && defaultValue != null) {
            return defaultValue;
        }
        return super.get(key);
    }

    public Object remove(Long key) {
        StemEntry stemEntry = new StemEntry(key);
        getStemList().remove(stemEntry);
        return null;
    }

    public Object remove(String key) {
        if (isLongIndex(key)) {
            return remove(Long.parseLong(key.toString()));
        }
        return super.remove(key);
    }

    public Object get(Long key) {
        StemEntry index = new StemEntry(key);
        if (!getStemList().contains(index) && defaultValue != null) {
            return defaultValue;
        }
        if (getStemList().contains(index)) {
            return getStemList().floor(index).entry;
        }
        return null;
    }

    /**
     * Adds a list of objects to this stem, giving them indices appropriate indices
     * This is mostly a convenience for people writing in java to create lists
     * programatically
     *
     * @param list
     */
    public void addList(List<Object> list) {
        StemList list1 = new StemList();
        long startIndex = -1L;
        if (!getStemList().isEmpty()) {
            startIndex = getStemList().last().index;
        }
        for (int i = 0; i < list.size(); i++) {
            StemEntry stemEntry = new StemEntry(i + startIndex + 1, list.get(i));
            getStemList().add(stemEntry);
        }
    }


    public Object get(StemMultiIndex w) {
        StemVariable currentStem = this;
        /**
         * Drill down, checking everything exists.
         */
        for (int i = 0; i < w.getComponents().length - 1; i++) {
            String name = w.getComponents()[i] + STEM_INDEX_MARKER;
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("Error: Could not find the given index \"" + name + "\" in this stem \"" + w.getName() + STEM_INDEX_MARKER);
            }
            currentStem = nextStem;
        }
        // for last one. May be a variable or a stem
        if (w.isStem()) {
            return currentStem.get(w.getLastComponent() + STEM_INDEX_MARKER);
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
            String name = w.getComponents()[i] + STEM_INDEX_MARKER;
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                nextStem = new StemVariable();
                currentStem.put(name, nextStem);
            }
            currentStem = nextStem;
        }
        // for last one
        if (w.isStem()) {
            currentStem.put(w.getLastComponent() + STEM_INDEX_MARKER, value);
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
            String name = w.getComponents()[i] + STEM_INDEX_MARKER;
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("Error: Could not find the given index \"" + name + "\" in this stem \"" + w.getName() + STEM_INDEX_MARKER);
            }
            currentStem = nextStem;
        }
        // for last one. May be a variable or a stem
        if (w.isStem()) {
            currentStem.remove(w.getLastComponent() + STEM_INDEX_MARKER);
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
 /*   public StemVariable fillStem(StemVariable template, Object value) {
        clear();
        for (String key : template.keySet()) {
            put(key, value);
        }
        return this;
    }
*/

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
                if (!containsKey(newKey)) {
                    put(newKey, get(key));
                    remove(key);

                }
            }
        }
    }

    /*  Quick example of rename.
          b.OA2_foo := 'a';
          b.OA2_woof := 'b';
          b.OA2_arf := 'c';
          b.fnord := 'd';
          rename_keys(b., keys(b.)-'OA2_')
     */
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
     * Converts this to a JSON object. Names of stem components are decoded. So if you have a stem, a.,
     * with component $23foo, then a.$23foo yields
     * <pre>
     *     {"#foo":...}
     * </pre> I.e. the $23 is treated as an escaped name
     * and converted back. If you do not want stem names escaped when converting to JSON, then use
     * {@link #toJSON(boolean)} with the argument being <b>false</b>. In that case the outputted JSON would be
     * <pre>
     *     {"$23foo":...}
     * </pre>
     *
     * @return
     */
    public JSON toJSON() {
        return toJSON(true); //
    }

    /**
     * Convert this to JSON.
     *
     * @param escapeNames -- whether or not to escape stem names when creating tje JSON object.
     * @return
     */
    public JSON toJSON(boolean escapeNames) {
        JSONObject json = new JSONObject();
        StemList<StemEntry> localSL = getStemList();
        QDLCodec codec = new QDLCodec();

        // Special case of a JSON array of objects that has been turned in to a stem list.
        // We want to recover this since it is a very common construct.
        for (String key : super.keySet()) {

            if (key.endsWith(STEM_INDEX_MARKER)) {
                StemVariable x = (StemVariable) get(key);

                // compound object
                String newKey = key.substring(0, key.length() - 1);
                if (isLongIndex(newKey)) {
                    StemEntry stemEntry = new StemEntry(Long.parseLong(newKey));
                    if (!localSL.contains(stemEntry)) {
                        stemEntry.entry = x;
                        localSL.add(stemEntry);
                    } else {
                        throw new IndexError("Error: The stem contains a list element \"" + newKey + "\" " +
                                "and a stem entry \"" + key + "\". This is not convertible to a JSON Object");
                    }
                } else {
                    json.put(escapeNames ? codec.decode(newKey) : newKey, x.toJSON());
                }

            } else {
                json.put(escapeNames ? codec.decode(key) : key, get(key));
            }
        }
        if (json.isEmpty()) {
            // no other values.
            if (localSL.isEmpty()) {
                return json;
            }
            return getStemList().toJSON();
        }
        // now for the messy bit -- lists
        JSONArray array = getStemList().toJSON();
        if (!array.isEmpty()) {
            for (int i = 0; i < array.size(); i++) {
                json.put(i, array.get(i));
            }
        }

        return json;
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
        StemList<StemEntry> stemList = new StemList<>();

        for (int i = 0; i < array.size(); i++) {
            stemList.append(new StemEntry(i, convert(array.get(i))));
        }
        out.setStemList(stemList);
        return out;
    }

    /**
     * Does the grunt work of taking an entry from a JSON object and converting it to something QDL can
     * understand. Used mostly in the toString methods.
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
     * @param jsonObject return this object, populated
     */
    public StemVariable fromJSON(JSONObject jsonObject) {
        QDLCodec codec = new QDLCodec();
        for (Object k : jsonObject.keySet()) {
            String key = k.toString();

            Object v = jsonObject.get(k);
            if (v instanceof JSONObject) {
                StemVariable x = new StemVariable();
                put(codec.encode(key) + STEM_INDEX_MARKER, x.fromJSON((JSONObject) v));
            } else {
                if (v instanceof JSONArray) {
                    StemVariable x = new StemVariable();

                    put(codec.encode(key) + STEM_INDEX_MARKER, x.fromJSON((JSONArray) v));
                } else {
                    put(codec.encode(key), v);
                }
            }
        }

        return this;
    }

    public StemVariable fromJSON(JSONArray array) {
        StemList<StemEntry> sl = new StemList<>();
        for (int i = 0; i < array.size(); i++) {
            Object v = array.get(i);
            if (v instanceof JSONObject) {
                StemVariable x = new StemVariable();
                put(i + STEM_INDEX_MARKER, x.fromJSON((JSONObject) v));
            } else {
                if (v instanceof JSONArray) {
                    StemVariable x = new StemVariable();
                    put(i + STEM_INDEX_MARKER, x.fromJSON((JSONArray) v));
                } else {
                    sl.add(new StemEntry(i, v));
                }
            }
        }
        setStemList(sl);

        return this;
    }

    String int_regex = "[1-9][0-9]*";

    boolean isLongIndex(String key) {
        // special case of index being zero!! Otherwise, no such index can start with zero,
        // so a key of "01" is a string, not the number 1. Sorry, best we can do. 
        return key.equals("0") || key.matches(int_regex);
    }
      Pattern var_pattern = Pattern.compile(var_regex);
      Pattern int_pattern = Pattern.compile(int_regex);
   protected  boolean isVar(String var){
        return var_pattern.matcher(var).matches();
    }

    protected  boolean isIntVar(String var){
         return var.equals("0") || int_pattern.matcher(var).matches();
     }

    @Override
    public Object put(String key, Object value) {
        if(!isVar(key)){
            throw new IllegalArgumentException("Error: " + key + " is not a legal varaible name");
        }
        if (!key.endsWith(STEM_INDEX_MARKER) && isIntVar(key)) {
            return put(Long.parseLong(key), value);
        }
        return super.put(key, value);
    }

    public Object put(Long index, Object value) {
        StemEntry stemEntry = new StemEntry(index, value);
        getStemList().remove(stemEntry);
        getStemList().add(stemEntry);
        return null;
    }

    public StemVariable union(StemVariable... stemVariables) {
        for (StemVariable stemVariable : stemVariables) {
            this.putAll(stemVariable);
        }
        return this;
    }

    public String toString(int indentFactor, String currentIndent) {
        String blanks = "                                                           ";
        blanks = blanks + blanks + blanks + blanks; // lots of blanks
        String output = currentIndent + "{\n";
        boolean isFirst = true;
        String newIndent = currentIndent + blanks.substring(0, indentFactor);
        for (String key : super.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",\n";
            }
            Object o = get(key);
            if (o instanceof StemVariable) {
                output = output + newIndent + key + "=" + ((StemVariable) o).toString(indentFactor, newIndent);
            } else {
                output = output + newIndent + key + "=" + convert(o);
            }
        }
        // now for any list
        for (StemEntry entry : getStemList()) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",\n";
            }
            output = output + newIndent + entry.index + "=" + convert(entry.entry);
        }
        return output + "\n" + currentIndent + "}";

    }

    public static void main(String[] arg) {
        StemVariable s = new StemVariable();
        StemVariable s2 = new StemVariable();
        StemVariable s3 = new StemVariable();
        s.put("a", "b");
        s.put("s", "n");
        s.put("d", "m");
        s.put("0", "foo");
        s.put("1", "bar");
        s2.put(0L, "qwe");
        s2.put(1L, "eee");
        s2.put(2L, "rrr");
        s2.put("rty", "456");
        s2.put("tyu", "ftfgh");
        s2.put("ghjjh", "456456");
        s3.put("a3rty", "456222");
        s3.put("a3tyu", "ftf222gh");
        s3.put("a3ghjjh", "422256456");
        s2.put("woof.", s3);
        s.put("foo.", s2);
        System.out.println(s.toString(1));
        System.out.println(s.toJSON().toString());
    }

    public String toString(int indentFactor) {
        return toString(indentFactor, "");
    }

    @Override
    public String toString() {
        String output = "{";
        boolean isFirst = true;
        for (String key : keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ", ";
            }
            output = output + key + "=" + get(key);
        }

        return output + "}";

    }

    @Override
    public Set<String> keySet() {
        HashSet<String> keys = new HashSet<>();
        keys.addAll(super.keySet()); // have to copy it since we cannot modify the key set of a map.
        if (getStemList().isEmpty()) {
            return keys;
        }
        for (StemEntry s : getStemList()) {
            keys.add(Long.toString(s.index));
        }
        return keys;
    }

    @Override
    public int size() {
        return super.size() + getStemList().size();
    }

    public boolean containsKey(String key) {
        if (isLongIndex(key)) {
            StemEntry s = new StemEntry(Long.parseLong(key));
            return getStemList().contains(s);
        }
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {

        if (super.containsValue(value)) return true;
        if (getStemList().isEmpty()) return false;
        // *sigh* have to look for it
        for (StemEntry s : getStemList()) {
            if (s.entry.equals(value)) return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && getStemList().isEmpty();
    }
    // List operations follow

    /**
     * Append the list elements of the stem to this list.
     *
     * @param stemVariable
     */
    public void listAppend(StemVariable stemVariable) {
        StemList<StemEntry> list = stemVariable.getStemList();
        if (list.isEmpty()) return;
        long startIndex = 0L;

        if (!getStemList().isEmpty()) {
            StemEntry last = getStemList().last();
            startIndex = last.index + 1;
            ;
        }
        for (StemEntry entry : list) {
            getStemList().add(new StemEntry(startIndex++, entry.entry));
        }
    }

    public void listAppend(Object value) {
        getStemList().append(value);
    }

    /**
     * Copies the elements from this list to the target list. Note that this will over-write any elements
     * already in the target. If you need to insert elements, use the {@link #listInsertAt(StemVariable, long)}
     * method.
     *
     * @param startIndex  first index in the source
     * @param length      how many elements to take from the source
     * @param target      that target to get the copy
     * @param insertIndex where in the target to start copying.
     */
    public void listCopy(long startIndex, long length, StemVariable target, long insertIndex) {
        if (length == 0L) return; // do nothing.
        if (!target.getStemList().contains(new StemEntry(insertIndex))) {
            throw new IllegalArgumentException("Error: The insertion index for the target of this copy does not exist.");
        }

        if (length + startIndex > getStemList().size()) {
            throw new IllegalArgumentException("Error: the source does not have enough elements to copy.");
        }
        SortedSet<StemEntry> sortedSet = getStemList().subSet(new StemEntry(startIndex), true, new StemEntry(startIndex + length), true);
        long newIndex = 0L;
        StemList<StemEntry> targetList = target.getStemList();
        for (StemEntry s : sortedSet) {
            StemEntry stemEntry = new StemEntry(insertIndex + newIndex++, s.entry);
            targetList.remove(stemEntry);
            targetList.add(stemEntry);
        }
    }

    /**
     * insert the list all the values of the stem list into this one at the starting index.
     *
     * @param insertIndex
     * @return
     */
    public void listInsertAt(StemVariable target, long insertIndex) {
        listInsertAt(0, size(), target, insertIndex);
    }

    /**
     * Insert the current
     *
     * @param startIndex
     * @param length
     * @param target
     * @param insertIndex
     */
    public void listInsertAt(long startIndex, long length, StemVariable target, long insertIndex) {
        StemList<StemEntry> tSL = target.getStemList();
        StemList<StemEntry> outSL = new StemList<>();
        for (long i = 0; i < insertIndex; i++) {
            outSL.append(tSL.get(i));
        }
        StemList<StemEntry> sSL = getStemList();
        for (long i = startIndex; i < startIndex + length; i++) {
            outSL.append(sSL.get(i));
        }
        for (long i = insertIndex; i < tSL.size(); i++) {
            outSL.append(tSL.get(i));
        }
        target.setStemList(outSL);
    }

    /**
     * Insert the whole argument in to the current stem, re-adjusting indices.
     *
     * @param arg
     * @param startIndex
     * @param length
     */
    public void oldListInsertAt(StemVariable arg, long startIndex, long length) {
        if (arg.getStemList().size() < length) {
            throw new IllegalArgumentException("Error: the given list is not long enough. It has length " +
                    arg.getStemList().size() + " and you requested to copy " + length + " elements.");
        }
        StemList<StemEntry> currentSL = getStemList();
        StemList<StemEntry> argSL = arg.getStemList();
        StemList<StemEntry> newSL = new StemList<>();
        for (long i = 0; i < startIndex; i++) {
            newSL.add(new StemEntry(i, currentSL.get(i)));
        }
        for (long i = 0; i < length; i++) {
            newSL.add(new StemEntry(i + startIndex, argSL.get(i)));
        }
        for (long i = 0; i < currentSL.size() - startIndex; i++) {
            newSL.add(new StemEntry(i + startIndex + length, currentSL.get(startIndex + i)));
        }
        setStemList(newSL);
    }

    public StemVariable listSubset(long startIndex) {
        return listSubset(startIndex, getStemList().size() - startIndex);
    }

    public StemVariable listSubset(long startIndex, long length) {
        StemVariable stemVariable = new StemVariable();

        if (getStemList().isEmpty()) return stemVariable;
        SortedSet<StemEntry> sortedSet = getStemList().subSet(new StemEntry(startIndex), new StemEntry(startIndex + length));
        StemList<StemEntry> newStemList = new StemList<>();
        long i = 0;
        for (StemEntry s : sortedSet) {
            // Now we have to adjust the indices since the tree set function returns the indices in the set
            newStemList.add(new StemEntry(i++, s.entry));
        }
        stemVariable.setStemList(newStemList);
        return stemVariable;
    }

    public boolean isList() {
        return stemList.size() == size();
    }
}