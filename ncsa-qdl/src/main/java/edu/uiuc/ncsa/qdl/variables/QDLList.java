package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.exceptions.QDLException;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

/**
 * This is used internally by a stem to store its entries that have integer indices.
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/20 at  8:39 AM
 */
public class QDLList<V extends SparseEntry> extends TreeSet<V> {
    public QDLList() {
        super();
    }

    public QDLList(long size) {
        for (long i = 0L; i < size; i++) {
            SparseEntry sparseEntry = new SparseEntry(i, i);
            add((V) sparseEntry);
        }
    }

    public QDLList(long size, Object[] fill) {

        int fillSize = -1;
        if (fill != null && fill.length!=0) {
            fillSize = fill.length;
        }
        SparseEntry sparseEntry;
        for (long i = 0L; i < size; i++) {
            if (fill == null) {
                sparseEntry = new SparseEntry(i, i);
            } else {

                sparseEntry = new SparseEntry(i, fill[(int) i % fillSize]);
            }
            add((V) sparseEntry);
        }
    }

    /**
     * Runs over <i>every</i> entry in the stem list (including danglers).
     * result is a standard list (starts at 0, no gaps) of unique elements.
     *
     * @return
     */
    public QDLList unique() {
        Iterator<V> iterator = iterator();
        HashSet set = new HashSet();
        while (iterator.hasNext()) {
            Object obj = iterator.next().entry;
            if(obj instanceof StemVariable){
                StemVariable ss = ((StemVariable)obj).almostUnique();
                set.addAll(ss.getStemList().unique());
            }else {
                set.add(obj);
            }
        }
        QDLList qdlList1 = new QDLList();
        HashSet hashSet1 = new HashSet();
           for (Object obj : set) {
               hashSet1.add(obj);
           }

        for (Object object : set) {
            qdlList1.append(object);
        }
        return qdlList1;
    }


    public Object get(long index) {
        if (index < 0L) {
            index = size() + index;
        }
        V stemEntry = (V) new SparseEntry(index);
        if (!contains(stemEntry)) return null;
        return floor(stemEntry).entry;
    }

    public boolean remove(Long index) {
        if (index < 0L) {
            index = size() + index;
        }
        V stemEntry = (V) new SparseEntry(index);
        return super.remove(stemEntry);
    }

    /**
     * Find the largest element of this list and append the given object to the end of it.
     *
     * @param obj
     */
    public void append(Object obj) {
        V newEntry;
        if (obj instanceof SparseEntry) {
            // in this case, stem entries are being added directly, so don't wrap them in a stem entry.

            newEntry = (V) new SparseEntry(size(), ((SparseEntry) obj).entry); // argh Java requires a cast. If StemEntry is ever extended, this will break.

        } else {
            if (isEmpty()) {
                newEntry = (V) new SparseEntry(0L, obj);
            } else {
                V stemEntry = last();
                newEntry = (V) new SparseEntry(stemEntry.index + 1, obj);
            }

        }
        add(newEntry);
    }
    public void append(QDLSet set) {
        long index = 0L;
        if(!isEmpty()) {
             index = last().index;
        }
        for(Object k : set){
             add((V)new SparseEntry(index++, k));
         }
    }


    public void appendStem(QDLList qdlist){
        Iterator<SparseEntry> iterator = qdlist.iterator();
        while(iterator.hasNext()){
            Object v = iterator.next().entry;
            append(v);
        }
    }


    public static class seGapException extends QDLException {
        // If there is a gap in the entries, fall back on stem notation.
        // All this exception needs is to exist.
    }

    public String toString(int indentFactor, String currentIndent) {
        String output = currentIndent + "[";
        String newIndent = currentIndent + StringUtils.getBlanks(indentFactor);
        boolean needsCRWithClosingBrace = true;
        boolean isFirst = true;
        for (long i = 0; i < size(); i++) {

            Object obj = get(i);
            if (obj == null) {
                throw new seGapException();
            }
            if(obj instanceof StemVariable){
                if (isFirst) {
                    isFirst = false;
                    output = output + "\n";
                } else {
                    output = output + ",\n";
                }
                output = output + newIndent +   ((StemVariable) obj).toString(indentFactor, newIndent);
            }else{
                needsCRWithClosingBrace = false;
                if(isFirst){
                    isFirst = false;
                    output = output  + StemConverter.convert(obj);
                }   else{

                    output = output + "," + StemConverter.convert(obj);
                }
            }
        }

        return output + (needsCRWithClosingBrace?"\n"+newIndent + "]":"]") ;
    }

    public String toString(int indentFactor) {
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
            if (obj == null) {
                throw new seGapException();
            }
            String vv;
            if (obj instanceof BigDecimal) {
                vv = InputFormUtil.inputForm((BigDecimal) obj);
            } else {
                vv = obj.toString();
            }
            output = output + vv;
        }

        return output + "]";
    }

    /**
     * This exports the current list as a {@link JSONArray}. Note that there is no
     * analog for importing one -- use the {@link StemVariable#fromJSON(JSONObject)}
     * to do that, since the result will in general be a stem (if one element of the
     * array is a JSONObject, then the index has to make it a stem -- this is just how the
     * bookkeeping is done).
     *
     * @return
     */
    public JSONArray toJSON() {
        return toJSON(false);
    }
    public JSONArray toJSON(boolean escapeNames) {
        JSONArray array = new JSONArray();
        for (SparseEntry s : this) {
            Object v = s.entry;
            if (v instanceof StemVariable) {
                array.add(((StemVariable) v).toJSON(escapeNames));
            } else {
                array.add(v);
            }
        }
        return array;
    }

    public String inputForm(int indent) {
        return inputForm(indent, "");
    }

    public String inputForm() {
        String output = "[";
        boolean isFirst = true;

        for (long i = 0; i < size(); i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                output = output + ",";
            }
            Object obj = get(i);
            if (obj == null) {
                throw new seGapException();
            }
            output = output + InputFormUtil.inputForm(obj);
        }
        return output + "]";
    }

    public String inputForm(int indentFactor, String currentIndent) {
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
            if (obj == null) {
                throw new seGapException();
            }
            output = output + newIndent + InputFormUtil.inputForm(obj);
        }

        return output + "\n]";
    }



    /**
     * Convert this to an array of objects. Note that there may be gaps
     * filled in with null values if this is sparse.
     *
     * @param noGaps     - if true, truncates array at first encountered gap
     * @param allowStems - if true, hitting a stem throws an exception.
     * @return
     */
    public Object[] toArray(boolean noGaps, boolean allowStems) {
        Object[] r = new Object[size()];

        for (long i = 0L; i < size(); i++) {
            Object o = get(i);
            if (!allowStems && (o instanceof StemVariable)) {
                throw new IllegalArgumentException("error: a stem is not allowed in this list");
            }
            if (o == null && noGaps) {
                Object[] r2 = new Object[(int) i];
                System.arraycopy(r, 0, r2, 0, (int) i);
                return r2;
            }
            r[(int) i] = o;
        }
        return r;
    }
   public QDLList getSize(){
        QDLList s = new QDLList();
        if(isEmpty()){
            return s;
        }
        long index = 0L;
        SparseEntry sparseEntry = new SparseEntry(index++,new Long(size()));
        s.add(sparseEntry);
        SparseEntry currentEntry = first();
        while(currentEntry != null){
                 Object obj = currentEntry.entry;
                 if(obj instanceof StemVariable){
                     StemVariable s1 = (StemVariable) currentEntry.entry;
                     if(s1.getStemList().size() == 0){
                         break;
                     }
                     sparseEntry = new SparseEntry(index++,new Long(s1.getStemList().size()));
                     s.add(sparseEntry);
                     currentEntry = s1.getStemList().first();
                 }else{
                     break;
                 }
        }
        return s;
   }
   public Long getRank(){
        return new Long(getSize().size());
   }

   public ArrayList values(){
        ArrayList list = new ArrayList();
        Iterator<? extends SparseEntry> iterator = iterator();
        while(iterator.hasNext()){
            list.add(iterator.next().entry);
        }
        return list;
   }

}
