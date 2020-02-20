package edu.uiuc.ncsa.qdl.util;

import java.io.Serializable;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/20 at  8:35 AM
 */
public class StemEntry implements Comparable, Serializable {
    /**
     * Useful for making index entries, i.e., checking is an element is at a certain index
     * but not caring about the entry (which we never do, since we are modelling lists with these).
     * @param index
     */
    public StemEntry(long index) {
        this(index, null);
    }

    public StemEntry(long index, Object entry) {
        this.index = index;
        this.entry = entry;
    }

    public long index;
    public Object entry;

    @Override
    public int compareTo(Object o) {
        if (o instanceof StemEntry) {
            StemEntry s = (StemEntry) o;
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
