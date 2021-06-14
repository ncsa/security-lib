package edu.uiuc.ncsa.qdl.expressions;

import java.util.ArrayList;

/**
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

    public IndexList tail(int n) {
        IndexList indexList = new IndexList();
        indexList.addAll(subList(n, size() ));
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
}
