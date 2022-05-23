package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.expressions.IndexList;
import edu.uiuc.ncsa.qdl.state.QDLConstants;
import edu.uiuc.ncsa.qdl.state.StemMultiIndex;
import edu.uiuc.ncsa.qdl.state.VariableState;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static edu.uiuc.ncsa.qdl.state.legacy.SymbolTable.var_regex;
import static edu.uiuc.ncsa.qdl.variables.StemConverter.convert;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/16/20 at  12:09 PM
 */
public class StemVariable extends HashMap<String, Object> {
    public static final String STEM_INDEX_MARKER = ".";

    public QDLList getQDLList() {
        if (qdlList == null) {
            qdlList = new QDLList();
        }
        return qdlList;
    }

    public void setQDLList(QDLList qdlList) {
        this.qdlList = qdlList;
    }

    QDLList qdlList;

    // Convenience methods.
    public Long getLong(String key) {
        return (Long) get(key);
    }

    public Long getLong(Long key) {
        return (Long) get(key);
    }

    @Override
    public Object get(Object key) {
        if (key instanceof StemVariable) {
            // try to make a stem list
            IndexList r = get(new IndexList((StemVariable) key), true);
            return r.get(0);
        }
        if (key instanceof Integer) {
            return get(new Long((Integer) key));
        }
        if (key instanceof Long) {
            return get((Long) key);
        }
        if (key instanceof String) {
            String sKey = (String) key;
            if (StemPath.isPath(sKey)) {
                StemPath stemPath = new StemPath();
                stemPath.parsePath(sKey);
                return get(stemPath);
            }
            return get((String) key);
        }
        return super.get(key);
    }

    public BigDecimal getDecimal(Long key) {
        Object obj = get(key);
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        // try to convert it.
        return new BigDecimal(obj.toString());

    }

    public BigDecimal getDecimal(String key) {
        Object obj = get(key);
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        // try to convert it.
        return new BigDecimal(obj.toString());
    }

    /*
     get call with a conversion to a string or null if the object is null. This invokes to string on the object.
     */
    public String getString(Long key) {
        Object obj = get(key);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    /*
     get call with a conversion to a string or null if the object is null. This invokes to string on the object.
     */
    public String getString(String key) {
        Object obj = get(key);
        if (obj == null) {
            return null;
        }
        return obj.toString();
    }

    public Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public Boolean getBoolean(Long key) {
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
        if (key.endsWith(STEM_INDEX_MARKER)) {
            key = key.substring(0, key.length() - 1);
        }
        try {
            if (isLongIndex(key)) {
                return get(Long.parseLong(key));
            }
            if (key.endsWith(STEM_INDEX_MARKER)) {
                try {
                    Long kk = Long.parseLong(key.substring(0, key.length() - 1));
                    return get(kk);

                } catch (Throwable t) {
                    // not a number
                }
            }
            if (!containsKey(key) && defaultValue != null) {
                return defaultValue;
            }
            if (StemPath.isPath(key)) {
                StemPath stemPath = new StemPath();
                stemPath.parsePath(key);
                return get(stemPath);
            }
            return super.get(key);
        } catch (StackOverflowError | PatternSyntaxException sto) {
            //In this case someplace there is a reference to the stem itself, e.g.
            // a. := indices(5);
            // a.b. := a.
            // This can work if the indices are accessed directly but attempting to access the whole
            // things (such as printing it out with "say" is going to fail).
            throw new VariableState.CyclicalError("recursive overflow at index '" + key + "'");
        }
    }

    public Object remove(Long key) {
        getQDLList().removeByIndex(key);
        return null;
    }

    public StemVariable() {
    }

    public StemVariable(Long count, Object[] fillList) {
        QDLList s = new QDLList(count, fillList);
        setQDLList(s);
    }

    public Object remove(String key) {
        if (isLongIndex(key)) {
            return remove(Long.parseLong(key.toString()));
        }
        return super.remove(key);
    }

    public Object get(Long key) {
        Object rc = getQDLList().get(key);
        if (rc == null && defaultValue != null) {
            return defaultValue;
        }
        return rc;
    }

    /**
     * Adds a list of objects to this stem, giving them indices appropriate indices
     * This is mostly a convenience for people writing in java to create lists
     * programatically. Note there is no parameter for this list since that will blow up
     * if there are mixed entries.
     *
     * @param list
     */
    public void addList(List list) {
        getQDLList().addAll(list);
    }


    public Object get(StemMultiIndex w) {
        StemVariable currentStem = this;
        /**
         * Drill down, checking everything exists.
         */
        for (int i = 0; i < w.getComponents().size() - 1; i++) {
            String name = w.getComponents().get(i) + STEM_INDEX_MARKER;
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("could not find the given index \"" + name + "\" in this stem \"" + w.getName() + STEM_INDEX_MARKER, null);
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
        for (int i = 0; i < w.getComponents().size() - 1; i++) {
            //   String name = w.getComponents()[i] + STEM_INDEX_MARKER;
            String name = w.getComponents().get(i);
            Object object = currentStem.get(name);
            StemVariable nextStem = null;
            if (object instanceof StemVariable) {
                nextStem = (StemVariable) object;
            }
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
        for (int i = 0; i < w.getComponents().size() - 1; i++) {
            String name = w.getComponents().get(i) + STEM_INDEX_MARKER;
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("could not find the given index \"" + name + "\" in this stem \"" + w.getName() + STEM_INDEX_MARKER, null);
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
     * Make a shallow copy of this stem variable.
     *
     * @return
     */
    @Override
    public Object clone() {
        StemVariable output = new StemVariable();
        for (Object key : keySet()) {
            Object obj = get(key);
            boolean isLongKey = key instanceof Long;
            if (obj instanceof StemVariable) {
                output.putLongOrString(key,((StemVariable) obj).clone());
            } else {
                output.putLongOrString(key, obj);
            }
        }

        return output;
    }

    public StemVariable mask(StemVariable stem2) {
        StemVariable result = new StemVariable();

        for (Object key : stem2.keySet()) {
            if (!containsKey(key)) {
                throw new IllegalArgumentException("'" + key + "' is not a key in the first stem. " +
                        "Every key in the second argument must be a key in the first.");
            }
            Object rawBit = stem2.get(key);
            if (!(rawBit instanceof Boolean)) {
                throw new IllegalArgumentException("every value of the second argument must be boolean");
            }
            Boolean b = (Boolean) rawBit;
            if (b) {
                result.putLongOrString(key, get(key));;
            }
        }
        return result;
    }

    public StemVariable commonKeys(StemVariable arg2) {
        StemVariable result = new StemVariable();
        int index = 0;
        for (Object key : arg2.keySet()) {
            if (containsKey(key)) {
                String currentIndex = Integer.toString(index++);
                result.put(currentIndex, key);
            }
        }
        return result;
    }

    /*
    In point of fact, renameKeys does not handle list values, only string keys
     */
    public void renameKeys(StemVariable newKeys, boolean overWriteKeys) {
        for (Object oldKey : newKeys.keySet()) {
            if (containsKey(oldKey)) {
                Object newKey = newKeys.get(oldKey);
                if (containsKey(newKey)) {
                    if (overWriteKeys) {
                        putLongOrString(newKey, get(oldKey));
                        remove(oldKey);
                    } else {
                        throw new IllegalArgumentException("'" + oldKey + "' is already a key. You  must explicitly overwrite it with the flag");
                    }
                } else {
                    putLongOrString(newKey, get(oldKey));
                    remove(oldKey);
                }
            }
        }
    }

    @Override
    public Object remove(Object key) {
        if (key instanceof Long) {
            return remove((Long) key);
        }
        return super.remove(key);
    }

    /*  Quick example of rename.
              b.OA2_foo := 'a';
              b.OA2_woof := 'b';
              b.OA2_arf := 'c';
              b.fnord := 'd';
              rename_keys(b., keys(b.)-'OA2_')
         */
    public StemVariable excludeKeys(StemVariable keyList) {
        return oldExcludeKeys(keyList);
    }

    protected StemVariable oldExcludeKeys(StemVariable keyList) {
        StemVariable result = new StemVariable();
        for (Object key : keySet()) {
            if (key instanceof Long) {
                if (!keyList.containsValue((Long) key)) {
                    result.put((Long) key, get(key));
                }
            } else {
                if (!keyList.containsValue(key)) {
                    result.put((String) key, get(key));
                }
            }

        }
        return result;
    }

    public StemVariable includeKeys(StemVariable keyList) {
        return oldIncludeKeys(keyList);
    }

    protected StemVariable oldIncludeKeys(StemVariable keyList) {
        StemVariable result = new StemVariable();
        for (int i = 0; i < keyList.size(); i++) {
            // for loop to be sure that everything is done in order.
            String index = Integer.toString(i);
            if (!keyList.containsKey(index)) {
                throw new IllegalArgumentException("the set of supplied keys is not a list");
            }
            String currentKey = keyList.getString(index);
            if (containsKey(currentKey)) {
                result.put(currentKey, get(currentKey));
            }
        }
        return result;
    }

    // Possible replacement for include and exclude keys. Thought there was a bug in exclide_keys but
    // it turned out that there was another issue.
    protected StemVariable keepOrRemoveKeys(StemVariable keyList, boolean retain) {
        StemVariable result = new StemVariable();
        for (int i = 0; i < keyList.size(); i++) {
            // for loop to be sure that everything is done in order.
            String index = Integer.toString(i);
            if (!keyList.containsKey(index)) {
                throw new IllegalArgumentException("the set of supplied keys is not a list");
            }
            String currentKey = keyList.getString(index);
            if (retain && containsKey(currentKey)) {
                result.put(currentKey, get(currentKey));
            }
            if (!retain && !containsKey(currentKey)) {
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
                throw new IllegalArgumentException("the set of supplied keys is not a list");
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
        return toJSON(false); //
    }

    /**
     * Convert this to JSON.
     *
     * @param escapeNames -- whether or not to escape stem names when creating the JSON object.
     * @return
     */
    public JSON toJSON(boolean escapeNames) {
        return newToJSON(escapeNames);
    }

    protected JSON newToJSON(boolean escapeNames) {

        if (super.size() == 0 && getQDLList().size() == 0) {
            // Empty stem corresponds to an empty JSON Object
            return new JSONObject();
        }
        if (getQDLList().size() == size()) {
            return getQDLList().toJSON(escapeNames); // handles case of simple list of simple elements
        }
        JSONObject json = new JSONObject();
        //edge case. empty stem list should return a JSON object
        if (size() == 0) {
            return json;
        }
        if (super.size() == 0) {
            // super.size counts the number of non-stem entries. This means there are
            // list elements and nothing else.
            // if it is just a list, return it asap.
            return getQDLList().toJSON(escapeNames);
        }
        QDLList localSL = new QDLList();
        localSL.addAll(getQDLList());
        QDLCodec codec = new QDLCodec();

        // Special case of a JSON array of objects that has been turned in to a stem list.
        // We want to recover this since it is a very common construct.
        for (String key : super.keySet()) {
            Object object = get(key);

            if (object instanceof StemVariable) {
                StemVariable x = (StemVariable) object;

                // compound object

                String newKey = key;
                if (newKey.endsWith(STEM_INDEX_MARKER)) {
                    newKey = key.substring(0, key.length() - 1);
                }
                if (isLongIndex(newKey)) {
                    SparseEntry sparseEntry = new SparseEntry(Long.parseLong(newKey));
                    if (!localSL.contains(sparseEntry)) {
                        sparseEntry.entry = x;
                        localSL.add(sparseEntry);
                    } else {
                        throw new IndexError("The stem contains a list element '" + newKey + "' " +
                                "and a stem entry '" + key + "'. This is not convertible to a JSON Object", null);
                    }
                } else {
                    json.put(escapeNames ? codec.decode(newKey) : newKey, x.toJSON(escapeNames));
                }

            } else {
                if (!(object instanceof QDLNull)) {
                    // don't add it if it is null.
                    json.put(escapeNames ? codec.decode(key) : key, object);
                }
            }
        }
        // This is here because what it does is it checks that stem lists have all been added.
        // remove this and stem lists don't get processed right.
        if (localSL.size() == size()) {
            return localSL.toJSON(); // Covers 99.9% of all cases for lists of compound objects.
        }


        // now for the messy bit -- lists
        // At this point there were no collisions in the indices.
        JSONArray array = getQDLList().toJSON();
        if (!array.isEmpty()) {
            for (int i = 0; i < array.size(); i++) {
                json.put(i, array.get(i));
            }
        }

        return json;
    }


    public StemVariable fromJSON(JSONObject jsonObject) {
        return fromJSON(jsonObject, false);
    }

    /**
     * Populate this from a JSON object. Note that JSON arrays are turned in to stem lists.
     *
     * @param jsonObject return this object, populated
     */
    public StemVariable fromJSON(JSONObject jsonObject, boolean convertVars) {
        QDLCodec codec = new QDLCodec();
        for (Object k : jsonObject.keySet()) {
            String key = k.toString();

            Object v = jsonObject.get(k);
            if (v instanceof JSONObject) {
                StemVariable x = new StemVariable();
                if (convertVars) {
                    put(codec.encode(key) + STEM_INDEX_MARKER, x.fromJSON((JSONObject) v));
                } else {
                    put(key + STEM_INDEX_MARKER, x.fromJSON((JSONObject) v));
                }
            } else {
                if (v instanceof JSONArray) {
                    StemVariable x = new StemVariable();
                    if (convertVars) {
                        put(codec.encode(key) + STEM_INDEX_MARKER, x.fromJSON((JSONArray) v));
                    } else {
                        put(key + STEM_INDEX_MARKER, x.fromJSON((JSONArray) v));
                    }
                } else {
                    if (convertVars) {
                        put(codec.encode(key), v);
                    } else {
                        if (v instanceof Integer) {
                            put(key, ((Integer) v).longValue());
                        } else if (v instanceof Float) {
                            put(key, new BigDecimal(Float.toString((Float) v)));
                        } else if (v instanceof Double) {
                            put(key, new BigDecimal(Double.toString((Double) v)));
                        } else {
                            if (v instanceof String && v.equals(QDLConstants.JSON_QDL_NULL)) {
                                put(key, QDLNull.getInstance());
                            } else {
                                put(key, v);
                            }
                        }
                    }
                }
            }
        }

        return this;
    }

    public StemVariable fromJSON(JSONArray array) {
        return newfromJSON(array);
    }

    protected StemVariable newfromJSON(JSONArray array) {
        //     StemList<StemEntry> sl = new StemList<>();
        for (int i = 0; i < array.size(); i++) {
            Object v = array.get(i);
            if (v instanceof JSONObject) {
                StemVariable x = new StemVariable();
                put((long) i, x.fromJSON((JSONObject) v));
            } else {
                if (v instanceof JSONArray) {
                    StemVariable x = new StemVariable();
                    put((long) i, x.fromJSON((JSONArray) v));
                } else {
                    //   sl.add(new StemEntry(i, v));
                    if (v instanceof Integer) {
                        put((long) i, ((Integer) v).longValue());
                    } else if (v instanceof Float) {
                        put((long) i, new BigDecimal(Float.toString((Float) v)));
                    } else if (v instanceof Double) {
                        put((long) i, new BigDecimal(Double.toString((Double) v)));
                    } else {
                        if ((v instanceof String) && QDLConstants.JSON_QDL_NULL.equals(v)) {
                            put((long) i, QDLNull.getInstance());
                        } else {
                            put((long) i, v);
                        }
                    }
                }
            }
        }
        return this;
    }


    String int_regex = "[+-]?[1-9][0-9]*";
    public void putLongOrString(Object key, Object value){
        if(key instanceof Long){
            put((Long)key, value);
        }else{
            put((String)key, value);
        }
    }
    /**
     * Does a regex on the index to see if it is really a long. Note that this
     * still is needed since a user can set a.'2' := 3 and this should turn
     * it into a list entry otherwise we get both string and list entries with
     * "the same" key.
     * @param key
     * @return
     */
    public boolean isLongIndex(String key) {
        // special case of index being zero!! Otherwise, no such index can start with zero,
        // so a key of "01" is a string, not the number 1. Sorry, best we can do.
        //     try {
        return key.equals("0") || key.matches(int_regex);
    }

    Pattern var_pattern = Pattern.compile(var_regex);
    Pattern int_pattern = Pattern.compile(int_regex);

    protected boolean isVar(String var) {
        return var_pattern.matcher(var).matches();
    }

    protected boolean isIntVar(String var) {
        return var.equals("0") || int_pattern.matcher(var).matches();
    }

    @Override
    public Object put(String key, Object value) {

        if (key.endsWith(STEM_INDEX_MARKER)) {
            key = key.substring(0, key.length() - 1);
        }
        if (StringUtils.isTrivial(key)) {
            throw new IllegalArgumentException("cannot have a trivial stem key");
        }

        if (isIntVar(key)) {
            return put(Long.parseLong(key), value);
        }

        return super.put(key, value);
    }

    public Object put(int index, Object value) {
        return put(new Long(index), value);
    }

    public Object put(Long index, Object value) {
        getQDLList().set(index, value);
/*
        SparseEntry sparseEntry = new SparseEntry(index, value);
        getQDLList().remove(sparseEntry);
        getQDLList().add(sparseEntry);
*/
        return null;
    }

    /*     Quick test that union to a stem with sparse array entries appends after the last entry.
    a.p :='q'; a.r:='abc';
    a.0 := 42; a.1 := 3;a.17:=100;
    a.; // show it
    a.~[[mod(random(3),100)],mod(random(4),100)];
     */

    /**
     * This will return a new stem consisting of this stem and the union of all
     * the stem arguments.
     *
     * @param stemVariables
     * @return
     */
    public StemVariable union(StemVariable... stemVariables) {
        StemVariable newStem = (StemVariable) clone();
        for (StemVariable stemVariable : stemVariables) {
            newStem.putAll(stemVariable); // non-list
            newStem.listAppend(stemVariable); // list elements
            if (stemVariable.getDefaultValue() != null) {
                newStem.setDefaultValue(stemVariable.getDefaultValue());
            }
        }

        return newStem;
    }

    public static String STEM_ENTRY_CONNECTOR = ":";

    public String toString(int indentFactor, String currentIndent) {
        String list = null;
        try {
            if (!getQDLList().isEmpty()) {
                list = getQDLList().toString(indentFactor, currentIndent);
                if (isList()) {
                    if (getDefaultValue() != null) {
                        list = "{*:" + getDefaultValue() + "}~" + list;
                    }
                    return list;
                }
            }
        } catch (QDLList.seGapException x) {
            //rock on
        }
        String output = currentIndent + "{\n";
        boolean isFirst = true;
        if (getDefaultValue() != null) {
            isFirst = false;
            output = output + "*:" + getDefaultValue();
        }
        String newIndent = currentIndent + StringUtils.getBlanks(indentFactor);
        for (String key : super.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",\n";
            }
            Object o = get(key);
            if (o instanceof StemVariable) {
                output = output + newIndent + key + STEM_ENTRY_CONNECTOR + ((StemVariable) o).toString(indentFactor, newIndent);
            } else {
                output = output + newIndent + key + STEM_ENTRY_CONNECTOR + convert(o);
            }

        }
        if (list == null) {
            // now for any list
            long i = 0;

            for (Object obj : getQDLList()) {
                String temp = null;
                if (obj instanceof SparseEntry) {
                    SparseEntry entry = (SparseEntry) obj;
                    temp = newIndent + entry.index + STEM_ENTRY_CONNECTOR + convert(entry.entry);
                } else {
                    temp = newIndent + (i++) + STEM_ENTRY_CONNECTOR + convert(obj);
                }
                if (isFirst) {
                    isFirst = false;
                } else {
                    output = output + ",\n";
                }
                output = output + temp;
            }
        } else {
            output = list + "~" + output;

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
        System.out.println(InputFormUtil.inputForm(s));
        System.out.println(s.allKeys2());
        StemVariable allKeys = s.indices();
        System.out.println(allKeys);
        System.out.println(s.get(allKeys.get(10L)));
        System.out.println(s.indices(0L));
        System.out.println(s.indices(1L));
        System.out.println(s.indices(2L));
        System.out.println(s.indices(-1L));
        System.out.println(s.indices(4L));


        System.out.println(s.toJSON().toString());
        String rawJSON = "{\n" +
                "  \"isMemberOf\":   [" +
                "  {\n" +
                "      \"name\": \"all_users\",\n" +
                "      \"id\": 13002\n" +
                "    },\n" +
                "        {\n" +
                "      \"name\": \"staff_reporting\",\n" +
                "      \"id\": 16405\n" +
                "    },\n" +
                "        {\n" +
                "      \"name\": \"list_allbsu\",\n" +
                "      \"id\": 18942\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        StemVariable stemVariable = new StemVariable();
        stemVariable.fromJSON(JSONObject.fromObject(rawJSON));

        JSON j = stemVariable.toJSON();
        System.out.println(j.toString(2));


    }

    public String toString(int indentFactor) {
        return toString(indentFactor, "");
    }

    @Override
    public String toString() {
        String list = null;
        try {
            if (!getQDLList().isEmpty()) {
                list = getQDLList().toString();
                if (isList()) {
                    if (getDefaultValue() != null) {
                        list = "{*:" + getDefaultValue() + "}~" + list;
                    }
                    return list;
                }
            }
        } catch (QDLList.seGapException x) {
            //rock on. Just means the list is sparse so use full notation.
        }
        if (isEmpty()) {
            return "[]";
        }
        String output = "{";
        boolean isFirst = true;

        if (getDefaultValue() != null) {
            if (getDefaultValue() instanceof BigDecimal) {
                output = output + "*:" + InputFormUtil.inputForm((BigDecimal) getDefaultValue());
            } else {
                output = output + "*:" + getDefaultValue();
            }
            isFirst = false;
        }
        StemKeys keys = keySet();
        for (Object key : keys) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ", ";
            }
            Object vv = get(key);

            String ss;
            if (vv instanceof BigDecimal) {
                ss = InputFormUtil.inputForm((BigDecimal) vv);
            } else {
                ss = vv.toString();
            }
            output = output + key + STEM_ENTRY_CONNECTOR + ss;
        }

        return output + "}";

    }

    public String inputForm() {
        String list = null;
        try {
            if (!getQDLList().isEmpty()) {
                list = getQDLList().inputForm();
                if (isList()) {
                    if (getDefaultValue() != null) {
                        list = "{*:" + InputFormUtil.inputForm(getDefaultValue()) + "}~" + list;
                    }
                    return list;
                }
            }
        } catch (QDLList.seGapException x) {
            //rock on. Just means the list is sparse so use full notation.
        }

        String output = "{";
        boolean isFirst = true;

        if (getDefaultValue() != null) {
            output = output + "*:" + InputFormUtil.inputForm(getDefaultValue());
            isFirst = false;
        }
        Set<String> keys;
        if (list == null) {
            keys = keySet(); // process everything here.
        } else {
            keys = super.keySet(); // only process proper stem entries.
            output = list + "~" + output;
        }
        for (Object key : keys) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ", ";
            }
            output = output + InputFormUtil.inputForm(key) + STEM_ENTRY_CONNECTOR + InputFormUtil.inputForm(get(key));
        }

        return output + "}";

    }

    public String inputForm(int indentFactor) {
        return inputForm(indentFactor, "");
    }

    public String inputForm(int indentFactor, String currentIndent) {
        String list = null;
        try {
            if (!getQDLList().isEmpty()) {
                list = getQDLList().inputForm(indentFactor, currentIndent);
                if (isList()) {
                    if (getDefaultValue() != null) {
                        list = "{*:" + InputFormUtil.inputForm(getDefaultValue()) + "}~" + list;
                    }
                    return list;
                }
            }
        } catch (QDLList.seGapException x) {
            //rock on
        }
        String output = currentIndent + "{\n";
        boolean isFirst = true;
        if (getDefaultValue() != null) {
            isFirst = false;
            output = output + "*:" + InputFormUtil.inputForm(getDefaultValue());
        }
        String newIndent = currentIndent + StringUtils.getBlanks(indentFactor);
        for (String key : super.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",\n";
            }
            Object o = get(key);
            output = output + newIndent + InputFormUtil.inputForm(key) + STEM_ENTRY_CONNECTOR + InputFormUtil.inputForm(get(key));
        }
        if (list == null) {
            // now for any list
            long i = 0;
            isFirst = true;
            for (Object obj : getQDLList()) {
                String temp = null;
                if (obj instanceof SparseEntry) {
                    SparseEntry entry = (SparseEntry) obj;
                    temp = newIndent + InputFormUtil.inputForm(entry.index) + STEM_ENTRY_CONNECTOR + InputFormUtil.inputForm(entry.entry);
                } else {
                    temp = newIndent + InputFormUtil.inputForm(i++) + STEM_ENTRY_CONNECTOR + InputFormUtil.inputForm(obj);
                }
                if (isFirst) {
                    isFirst = false;
                } else {
                    output = output + ",\n";
                }
                output = output + temp;
            }
        } else {
            output = list + "~" + output;

        }
        return output + "\n" + currentIndent + "}";

    }


    protected static class OrderedIndexEntry implements Comparable, Serializable {
        public OrderedIndexEntry(long index, boolean hasStem) {
            this.index = index;
            this.hasStem = hasStem;
        }

        long index;
        boolean hasScalar;
        boolean hasStem;

        @Override
        public int compareTo(Object o) {
            if (o instanceof OrderedIndexEntry) {
                OrderedIndexEntry s = (OrderedIndexEntry) o;
                if (index < s.index) return -1;
                if (index == s.index) return 0;
                if (index > s.index) return 1;
            }
            throw new ClassCastException("the object '" + o.getClass().getSimpleName() + "' is not comparable.");
        }

        @Override
        public boolean equals(Object obj) {
            return compareTo(obj) == 0;
        }
    }

    StemKeys orderedkeySet() {

        if (super.keySet().isEmpty() && getQDLList().isEmpty()) {
            return new StemKeys();
        }
        StemKeys stemKeys = getQDLList().orderedKeys();
        TreeSet<String> stringTreeSet = new TreeSet<>();
        stringTreeSet.addAll(super.keySet());
        stemKeys.setStemKeys(stringTreeSet);
        return stemKeys;
/*
        LinkedHashSet<String> h;
        if (getQDLList().isEmpty()) {
            h = new LinkedHashSet<>();
            h.addAll(super.keySet());
            return h;
        }
        h = getQDLList().orderedKeys();
        h.addAll(super.keySet());
        return h;
*/
        /*TreeSet<OrderedIndexEntry> stemKeys = new TreeSet<>();

        Set<String> currentKeys = new HashSet<>();
        currentKeys.addAll(super.keySet());
        for (String k : super.keySet()) {
            if (k.endsWith(STEM_INDEX_MARKER)) {
                try {
                    OrderedIndexEntry orderedIndexEntry = new OrderedIndexEntry(Long.parseLong(k.substring(0, k.length() - 1)), true);
                    stemKeys.add(orderedIndexEntry);
                    currentKeys.remove(k);
                } catch (Throwable t) {

                }
            }
        }
        long i = 0L;
       for(Object obj : getQDLList()){
           OrderedIndexEntry orderedIndexEntry = null;
               if(obj instanceof SparseEntry){
                   SparseEntry s = (SparseEntry) obj;
                   orderedIndexEntry = new OrderedIndexEntry(s.index, false);
               }else{
                   orderedIndexEntry = new OrderedIndexEntry(i++, false);
               }
           if (stemKeys.contains(orderedIndexEntry)) {
               OrderedIndexEntry oie = stemKeys.floor(orderedIndexEntry);
               oie.hasScalar = true;
           } else {
               orderedIndexEntry.hasScalar = true;
               stemKeys.add(orderedIndexEntry);

           }*/


/*
}
        for (SparseEntry s : getQDLList()) {
            OrderedIndexEntry orderedIndexEntry = new OrderedIndexEntry(s.index, false);
            if (stemKeys.contains(orderedIndexEntry)) {
                OrderedIndexEntry oie = stemKeys.floor(orderedIndexEntry);
                oie.hasScalar = true;
            } else {
                orderedIndexEntry.hasScalar = true;
                stemKeys.add(orderedIndexEntry);

            }
        }

        for (OrderedIndexEntry oie : stemKeys) {
            if (oie.hasScalar) {
                h.add(Long.toString(oie.index));
            }
            if (oie.hasStem) {
                h.add(oie.index + STEM_INDEX_MARKER);
            }
        }
        h.addAll(currentKeys);
        return h;
    */
    }

    @Override
    public StemKeys keySet() {
        return orderedkeySet();
    }

    @Override
    public int size() {
        return super.size() + getQDLList().size();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof Long) {
            return containsKey((Long) key);
        }
        if (key instanceof String) {
            return containsKey((String) key);
        }
        if (key instanceof IndexList) {
            return get((IndexList) key, false) != null;
        }
        return false;
    }

    public boolean containsKey(Long key) {
        SparseEntry s = new SparseEntry(key);
        return getQDLList().containsKey(s);
    }

    public boolean containsKey(String key) {
       if (isLongIndex(key)) {
            boolean rc = getQDLList().containsKey(key);
            return rc;
        }
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {

        if (super.containsValue(value)) return true;
        if (getQDLList().isEmpty()) return false;
        // *sigh* have to look for it
        return getQDLList().contains(value);

    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && getQDLList().isEmpty();
    }
    // List operations follow

    /**
     * Append the list elements of the stem to this list.
     *
     * @param stemVariable
     */
    public void listAppend(StemVariable stemVariable) {
        getQDLList().appendAll(stemVariable.getQDLList().values());

    }

    public void listAppend(Object value) {
        getQDLList().append(value);
    }

    /**
     * Copies the elements from this list to the target list. Note that this will over-write any elements
     * already in the target. If you need to insert elements, use the {@link #listInsertAt(long, long, StemVariable, long)}
     * method.
     *
     * @param startIndex  first index in the source
     * @param length      how many elements to take from the source
     * @param target      that target to get the copy
     * @param insertIndex where in the target to start copying.
     */
    public void listCopy(long startIndex, long length, StemVariable target, long insertIndex) {
        // Caveat: in QDL List you copy/insert from a source into the current list
        target.getQDLList().listCopyFrom(startIndex, length, getQDLList(), insertIndex);
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
        target.getQDLList().listInsertFrom(startIndex, length, getQDLList(), insertIndex);
    }

    /**
     * Insert the whole argument in to the current stem, re-adjusting indices.
     *
     * @param startIndex
     */

    public StemVariable listSubset(long startIndex) {
        return listSubset(startIndex, getQDLList().size() - startIndex);
    }

    public StemVariable listSubset(long startIndex, long length) {
        StemVariable stemVariable = new StemVariable();
        stemVariable.setQDLList(getQDLList().subList(startIndex, true, length, false));
        return stemVariable;
    }

    public boolean isList() {
        return getQDLList().size() == size();
    }

    /*
           a.'foo' := 'abctest0';
 a.'bar' := 'abctest1';
 a.'baz' := 'deftest2';
 a.'fnord' := 'abdtest3';
    b.'z' := 'baz'
   b.w := 'foof';
   b.3 := 42
   a.www := b.
   unique(a.)

     unique(['a',2,4,true]~['a','b',0,3,true])
     unique(['a','b',0]~[['a',2,4,true]]~[[['a','b',0,3,true]]])
     unique(['a','b',0,3,true]~[['a','b',0,3,true]]~[[['a','b',0,3,true]]])

     */

    /**
     * This almost returns all the unique elements. The issue is that if there are deeply
     * nested stems, then do not entirely get made unique before getting added to the result,
     * hence the simplest fix is that in that case is to call this twice. Someday this can
     * be fixed with a careful rewrite of the recursion in stel lists.
     *
     * @return
     */
    public StemVariable almostUnique() {
        StemVariable output = new StemVariable();
        if (isList()) {
            output.setQDLList(getQDLList().unique());
            return output;
        }
        HashSet hashSet = new HashSet();
        for (Object key : keySet()) {
            Object value = get(key);
            if (value instanceof StemVariable) {
                StemVariable ss = ((StemVariable) value).almostUnique();
                hashSet.addAll(ss.getQDLList().values());
            } else {
                hashSet.add(value);
            }
        }
        QDLList qdlList1 = new QDLList();
        HashSet hashSet1 = new HashSet();
        for (Object obj : hashSet) {
            hashSet1.add(obj);
        }

        for (Object obj : hashSet) {
            qdlList1.append(obj);
        }
        output.setQDLList(qdlList1);
        return output;
    }


    public StemVariable dim() {
        StemVariable dim = new StemVariable();
        dim.setQDLList(getQDLList().dim());
        return dim;
    }

    public Long getRank() {
        return Long.valueOf(dim().size());
    }

    public Object get(StemPath<StemPathEntry> stemPath) {
        if (stemPath.isEmpty()) {
            return null;
        }
        QDLCodec codec = new QDLCodec();
        Object currentObj = QDLNull.getInstance();
        StemVariable lastStem = this;
        for (StemPathEntry spe : stemPath) {
            currentObj = lastStem.get(spe.isString() ? codec.decode(spe.getKey()) : spe.getIndex());
            if (currentObj == null) {
                return QDLNull.getInstance();
            }
            if (currentObj instanceof StemVariable) {
                lastStem = (StemVariable) currentObj;
            } else {
                lastStem = null;
            }
        }

        return currentObj;
    }

    /*
    Contract: E.g. a. has rank 4, b. has rank 2 then
    b.a.1.2.3.4.0 should resolve to b.(a.1.2.3.4).0 naturally.
    This would get the indices [1,2,3,4,0] and return
    [a.1.2.3.4, 0], i.e. zeroth element is the actual value (can even be a stem)
    This is used to overlay the stem in the calling function
    so arguments don't get lost
     */
    public IndexList get(IndexList indexList, boolean strictMatching) {
        return newGet(indexList, strictMatching);
    }


    public boolean remove(IndexList indexList) {
        StemVariable currentStem = this;
        for (int i = 0; i < indexList.size() - 1; i++) {
            String name = indexList.get(i) + STEM_INDEX_MARKER;
            StemVariable nextStem = (StemVariable) currentStem.get(name);
            if (nextStem == null) {
                throw new IndexError("could not find the given index '" + name + "' in this stem", null);
            }
            currentStem = nextStem;
        }
        // for last one. May be a variable or a stem
        Object lastIndex = indexList.get(indexList.size() - 1);
        switch (Constant.getType(lastIndex)) {
            case Constant.LONG_TYPE:
                currentStem.remove((Long) lastIndex);
                return true;
            case Constant.STRING_TYPE:
                currentStem.remove((String) lastIndex);
                return true;

        }
        return false;
    }


    /**
     * Strict matching is used at the last resolution of the stem. It means that left over scalars
     * are flagged as errors since there is no stem waiting to resolve them.
     *
     * @param indexList
     * @param strictMatching
     * @return
     */
    public IndexList newGet(IndexList indexList, boolean strictMatching) {
        if (indexList.get(indexList.size() - 1) instanceof StemVariable) {
            StemVariable ndx = (StemVariable) indexList.get(indexList.size() - 1);
            if (!ndx.isList()) {
                throw new IllegalArgumentException("stem index list must be a list");
            }
            Object obj = null;
            QDLList qdlList = ndx.getQDLList();
            StemVariable lastStem = this;
            for (int i = 0; i < qdlList.size(); i++) {
                obj = lastStem.get(ndx.get(i));
                if (obj == null) {
                    throw new IndexError("the index of \"" + indexList.get(i) + "\" was not found in this stem", null);
                }
                if (obj instanceof StemVariable) {
                    lastStem = (StemVariable) obj;
                } else {
                    if (i != qdlList.size() - 1) {
                        throw new IndexError(" index depth error '" + obj + "' is not a stem.", null);

                    }
                }
            }
            IndexList rc = new IndexList();
            rc.add(obj);
            return rc;
        }
        IndexList rc = new IndexList();
        StemVariable currentStem = this;
        boolean gotOne = false;
        Object obj = null;
        for (int i = 0; i < indexList.size(); i++) {
            if (gotOne) {
                rc.add(indexList.get(i));
                continue;
            }
            obj = currentStem.get(indexList.get(i));
            if (obj == null) {
                if (hasDefaultValue()) {
                    obj = getDefaultValue();
                } else {
                    throw new IndexError("the index of \"" + indexList.get(i) + "\" was not found in this stem", null);
                }
            }

            if (obj instanceof StemVariable) {
                currentStem = (StemVariable) obj;
            } else {
                if (strictMatching && i != indexList.size() - 1) {
                    throw new IndexError("no such stem at this multi-index.", null);
                }
                rc.add(obj); // 0th entry is returned value
                gotOne = true;
            }
            if ((i == indexList.size() - 1) && !gotOne) {
                rc.add(currentStem); // result is a stem
            }
        }
        return rc;

    }


    public void set(IndexList indexList, Object value) {
        StemVariable currentStem = this;
        // Contract inside stems is that a variable is either a scalar
        // or a stem (since it would be very hard to differentiate these
        // in tail resolution.
        Object currentIndex;
        for (int i = 0; i < indexList.size() - 1; i++) {
            currentIndex = indexList.get(i);
            Object obj = currentStem.get(currentIndex);
            if (obj == null) {
                StemVariable newStem = new StemVariable();
                currentStem.myPut(currentIndex, newStem);
                currentStem = newStem;

            } else {
                if (obj instanceof StemVariable) {
                    currentStem = (StemVariable) currentStem.get(currentIndex);
                } else {
                    StemVariable newStem = new StemVariable();
                    currentStem.myPut(indexList.get(i), newStem);
                    currentStem = newStem;
                }

            }
        }
        currentStem.myPut(indexList.get(indexList.size() - 1), value);

    }

    protected void myPut(Object index, Object value) {
        if (index instanceof Long) {
            put((Long) index, value);
            return;
        }
        if (index instanceof String) {
            put((String) index, value);
            return;
        }
        if (index instanceof StemVariable) {
            StemVariable s = (StemVariable) index;
            IndexList indexList = new IndexList(s);
            set(indexList, value);
            return;
        }
        if (index instanceof BigDecimal) {
            BigDecimal bd = (BigDecimal) index;
            put(bd.longValueExact(), value);
            return;
        }
        throw new IndexError("Unknown index type for \"" + index + "\"", null);
    }

    public StemVariable indices(Long axis) {
        KeyRankMap keysByRank = allKeys2();
        //keysByRank.;
        if (axis == 0L) {
            StemVariable stemVariable = new StemVariable();
            if (!keysByRank.containsKey(1)) {
                return new StemVariable();
            }
            List<List> list = keysByRank.get(1);
            for (List list1 : list) {
                stemVariable.addList(list1);
            }
            return stemVariable;
        }
        int targetAxis = axis < 0L ? keysByRank.lastKey() + axis.intValue() : axis.intValue();
        if (!keysByRank.containsKey(targetAxis + 1)) {
            return new StemVariable();
        }
        List list = keysByRank.get(targetAxis + 1);
        return convertKeyByRank(list);
    }

    public StemVariable indices() {
        KeyRankMap keysByRank = allKeys2();
        // really simple case of a basic list with no structure. Just return the elements in a stem
        if (keysByRank.size() == 1 && keysByRank.keySet().iterator().next() == 1) {
            //List indices = keysByRank.get(1);
            return convertKeyByRank(keysByRank.get(1));
            //rc.addList(indices);
            //return rc;
        }
        StemVariable rc = new StemVariable();
        for (Integer i : keysByRank.keySet()) {
            StemVariable stemVariable = convertKeyByRank(keysByRank.get(i));
            for (Object key : stemVariable.keySet()) {
                rc.getQDLList().append(stemVariable.get(key));
            }
        }
        return rc;
    }

    /**
     * Gets a list of lists, e.g.
     * <pre>
     *  [[foo, 0], [foo, 1], [foo, 2], [foo, tyu]]
     *  </pre>
     * and returns a stem of these.
     *
     * @param list
     * @return
     */
    protected StemVariable convertKeyByRank(List<List> list) {
        StemVariable stemVariable = new StemVariable();
        long i = 0;
        for (List obj : list) {
            StemVariable index = new StemVariable();
            index.addList(obj);
            stemVariable.put(i++, index);
        }
        return stemVariable;
    }

    protected KeyRankMap allKeys2() {
        KeyRankMap keyRankMap = new KeyRankMap();
        for (Object key : keySet()) {
            List list = new ArrayList();
            list.add(key);
/*
            if (isLongIndex(key)) {
                list.add(Long.parseLong(key));
            } else {
                list.add(key);
            }
*/
            Object v = get(key);
            if (v instanceof StemVariable) {
                indices((StemVariable) v, list, keyRankMap);
            } else {
                keyRankMap.put(list);
            }
        }
        return keyRankMap;
    }

    protected void indices(StemVariable v, List list, KeyRankMap keyRankMap) {
        for (Object key : v.keySet()) {
            List list2 = new ArrayList();
            list2.addAll(list);
            list2.add(key);
/*
            if (key instanceof Long) {
                list2.add((Long)key);
            } else {
                list2.add(key);
            }
*/
            if (v.get(key) instanceof StemVariable) {
                indices((StemVariable) v.get(key), list2, keyRankMap);
            } else {
                keyRankMap.put(list2);
            }
        }

    }

    public static class KeyRankMap extends TreeMap<Integer, List<List>> {
        public void put(List list) {
            if (!containsKey(list.size())) {
                List enclosingList = new ArrayList();
                put(list.size(), enclosingList);
            }
            get(list.size()).add(list);
        }
    }

    public QDLSet valueSet() {
        QDLSet qdlSet = new QDLSet();
        for (Object key : keySet()) {
            Object value = get(key);
            switch (Constant.getType(value)) {
                case Constant.STEM_TYPE:
                    qdlSet.addAll(((StemVariable) value).values());
                    break;
                case Constant.SET_TYPE:
                    qdlSet.addAll(((QDLSet) value));
                    break;
                default:
                    qdlSet.add(value);
            }

        }
        return qdlSet;
    }

    /**
     * Be aware that this creates an actual set so it reads every item. If you need to iterate
     * over the elements (so single pass, not potentially multiple passes) consider using
     * {@link #valuesIterator()} while gets the iterators and manages them.
     * @return
     */
    @Override
    public Collection<Object> values() {
        return valueSet();
    }

    /**
     * Hook to get the iterator for the super. This is used to construct the {@link ValueIterator},
     * naught else.
     * @return
     */
    protected Iterator superIterator(){
        return super.values().iterator();
    }
    public class ValueIterator implements  Iterator{
        Iterator listIterator = getQDLList().iterator();
        Iterator stemIterator = superIterator();
        @Override
        public boolean hasNext() {
            return listIterator.hasNext() || stemIterator.hasNext();
        }

        @Override
        public Object next() {
            if(listIterator.hasNext()){
                return listIterator.next();
            }
            return stemIterator.next();
        }
    }

    /**
     * A specific iterator for the values of this stem. This should be used when
     * traversing all values, such as in {@link edu.uiuc.ncsa.qdl.statements.WhileLoop}s.
     * @return
     */
    public Iterator valuesIterator(){
        return new ValueIterator();
    }

    public boolean hasValue(Object x) {
        for (Object k : keySet()) {
            Object v = get(k);
            if (v instanceof StemVariable) {
                if (((StemVariable) v).hasValue(x)) {
                    return true;
                }
            } else {
                if (v.equals(x)) {
                    return true;
                }
            }
        }
        return false;
    }
}
