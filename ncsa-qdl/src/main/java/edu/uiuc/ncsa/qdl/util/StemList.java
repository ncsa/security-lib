package edu.uiuc.ncsa.qdl.util;

import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.TreeSet;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/20 at  8:39 AM
 */
public class StemList<V extends StemEntry> extends TreeSet<V> {
    public Object get(long index) {
        V stemEntry = (V) new StemEntry(index);
        if (!contains(stemEntry)) return null;
        return floor(stemEntry).entry;
    }

    /**
     * Find the largest element of this list and append the given object to the end of it.
     *
     * @param obj
     */
    public void append(Object obj) {
        V newEntry;
        if (isEmpty()) {
            newEntry = (V) new StemEntry(0L, obj);
        } else {
            V stemEntry = last();
            newEntry = (V) new StemEntry(stemEntry.index + 1, obj);
        }
        add(newEntry);
    }

    @Override
    public String toString() {
        String output = "StemList{";
        boolean isFirst = true;
        for (long i = 0; i < size(); i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",";
            }
            output = output + i + "=" + get(i);
        }

        return output + "}";
    }

    public JSONObject toJSON() {
        JSONObject output = new JSONObject();
        for (long i = 0; i < size(); i++) {
            Object v = get(i);
            if (get(i) instanceof BigDecimal) {
                v = v.toString(); // make sure this ends up as a big decimal
            }

            output.put(i, v);
        }
        return output;
    }

  /*  public void fromJSON(JSONObject json) {
        // This has issues. For one thing, saving a bunch of big decimals might mean they are strings.
        for (Object key : json.keySet()) {
            try {
                Object v = json.get(key);
                if (v instanceof Integer) {
                    add((V) new StemEntry(Long.parseLong(key.toString()), json.getLong(key.toString())));
                } else {
                    if (v.toString().contains(".")) {
                        try {
                            BigDecimal bigDecimal = new BigDecimal(v.toString());
                            add((V) new StemEntry(Long.parseLong(key.toString()), bigDecimal));
                        }catch(Throwable t){
                            add((V) new StemEntry(Long.parseLong(key.toString()), v)); // if not parseable,
                        }
                    } else {
                        add((V) new StemEntry(Long.parseLong(key.toString()), v));
                    }
                }
            } catch (Throwable t) {
                throw new QDLException("Error: The key \"" + key + "\" could not be parsed as a long.");
            }
        }
    }*/
}
