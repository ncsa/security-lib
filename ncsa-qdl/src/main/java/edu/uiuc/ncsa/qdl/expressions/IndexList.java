package edu.uiuc.ncsa.qdl.expressions;

import edu.uiuc.ncsa.qdl.exceptions.IndexError;
import edu.uiuc.ncsa.qdl.variables.QDLStem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple list of indices for a stem. This refers to a single value.
 * so if this is [1,2,3,4] then this is interpreted when accessing the stem as <br/></br>
 * a.1.2.3.4 == a.[1,2,3,4]   <br/><br/>
 * It is assumed that all values have been resolved, so these are just constants,
 * though they may be used to resolve other values. E.g.
 * <pre>
 *     a.2.b.[1,2] == a.2.(b.1.2)
 * </pre>
 * So the index is used to resolve b.
 * <p>Created by Jeff Gaynor<br>
 * on 6/12/21 at  6:47 AM
 */
public class IndexList extends ArrayList {
    /**
     * Unlike standard {@link ArrayList}, this fills up the list with nulls.
     * This is because it allows the individual elements to be managed later
     *
     * @param initialCapacity
     */
    public IndexList(int initialCapacity) {

        super(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            add(null);
        }
    }

    public IndexList() {
    }

    public IndexList(QDLStem stemVariable) {
        if (!stemVariable.isList()) {
            throw new IndexError("generic stem not supported as index", null);
        }
        for (long i = 0; i < stemVariable.size(); i++) {
            add(stemVariable.get(i));
        }
    }

    public IndexList tail(int n) {
        IndexList indexList = new IndexList();
        indexList.addAll(subList(n, size()));
        return indexList;
    }

    /**
     * Drops everything from index n on. New index list has n elements
     *
     * @param n
     */
    public void truncate(int n) {
        removeRange(n, size() - 1);
        remove(n);

    }

    @Override
    public IndexList clone() {
        IndexList x = new IndexList();
        for (Object is : this) {
            x.add(is);
        }
        return x;
    }

    /**
     * Return n clones of this object
     * @param n
     * @return
     */
    public List<IndexList> clone(int n){
          ArrayList<IndexList> x = new ArrayList<>(n);
          for(int i = 0; i < n; i++){
                x.add(clone());
          }
          return x;
    }
}
