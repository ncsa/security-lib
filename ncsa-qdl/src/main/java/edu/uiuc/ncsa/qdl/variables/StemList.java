package edu.uiuc.ncsa.qdl.variables;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.TreeSet;

/**
 * This is used internally by a stem to store its entries that have integer indices.
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

    /**
     * This exports the current list as a {@link JSONArray}. Note that there is no
     * analog for importing one -- use the {@link StemVariable#fromJSON(JSONObject)}
     * to do that, since the result will in general be a stem (if one element of the
     * array is a JSONObject, then the index has to make it a stem -- this is just how the
     * bookkeeping is done).
     * @return
     */
    public JSONArray toJSON() {
        JSONArray array = new JSONArray();
        for(StemEntry s : this){
            Object v = s.entry;
            if(v instanceof StemVariable){
                array.add(((StemVariable )v).toJSON());
            }else{
                array.add(v);
            }
        }
        return array;
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
