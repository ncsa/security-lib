package edu.uiuc.ncsa.qdl.variables;

import java.io.Serializable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/20 at  8:35 AM
 */
public class SparseEntry implements Comparable, Serializable {
    /**
     * Useful for making index entries, i.e., checking is an element is at a certain index
     * but not caring about the entry (which we never do, since we are modelling lists with these).
     * @param index
     */
    public SparseEntry(long index) {
        this(index, null);
    }

    public SparseEntry(long index, Object entry) {
        this.index = index;
        this.entry = entry;
    }

    public long index;
    public Object entry;

    @Override
    public int compareTo(Object o) {
        if (o instanceof SparseEntry) {
            SparseEntry s = (SparseEntry) o;
            if (index < s.index) return -1;
            if (index == s.index) return 0;
            if (index > s.index) return 1;
        }
        throw new ClassCastException("Error: the object \"" + o.getClass().getSimpleName() + "\" is not comparable.");
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo(obj) == 0;
    }

}
