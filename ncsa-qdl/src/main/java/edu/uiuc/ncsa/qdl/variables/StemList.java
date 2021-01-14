package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * This is used internally by a stem to store its entries that have integer indices.
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/20 at  8:39 AM
 */
public class StemList<V extends StemEntry> extends TreeSet<V> {
    /**
     * Runs over <i>every</i> enetry in the stem list (including danglers).
     * result is a standard list (starts at 0, no gaps) of unique elements.
     * @return
     */
    public StemList unique(){
        Iterator<V> iterator = iterator();
        HashSet set = new HashSet();
        while(iterator.hasNext()){
            set.add(iterator.next().entry);
        }
        StemList stemList1 = new StemList();
        for(Object object : set){
            stemList1.append(object);
        }
        return stemList1;
    }
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
        if(obj instanceof StemEntry){
            // in this case, stem entries are being added directly, so don't wrap them in a stem entry.

            newEntry = (V)new StemEntry(size(), ((StemEntry) obj).entry); // argh Java requires a cast. If StemEntry is ever extended, this will break.

        }else{
            if (isEmpty()) {
                newEntry = (V) new StemEntry(0L, obj);
            } else {
                V stemEntry = last();
                newEntry = (V) new StemEntry(stemEntry.index + 1, obj);
            }

        }
        add(newEntry);
    }

     public static class seGapException extends QDLException{
      // If there is a gap in the entries, fall back on stem notation.
         // All this exception needs is to exist.
     }
    public String toString(int indentFactor, String currentIndent){
        String output = currentIndent + "[\n";
        String newIndent = currentIndent + StringUtils.getBlanks(indentFactor);

        boolean isFirst = true;
        for (long i = 0; i < size(); i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",\n";
            }
            Object obj = get(i);
            if(obj == null){
                throw new seGapException();
            }
            output = output + newIndent + StemConverter.convert(obj);
        }

        return output + "\n]";


    }
    public String toString(int indentFactor){
          return toString(indentFactor, "");
    }

    @Override
    public String toString() {
        String output = "[";
        boolean isFirst = true;
        for (long i = 0; i < size(); i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",";
            }
            Object obj = get(i);
            if(obj == null){
                throw new seGapException();
            }
            output = output +  obj;
        }

        return output + "]";
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

}
