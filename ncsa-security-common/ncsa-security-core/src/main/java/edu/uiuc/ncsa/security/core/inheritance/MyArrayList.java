package edu.uiuc.ncsa.security.core.inheritance;

import java.util.ArrayList;

/**
 * An {@link ArrayList} that is actually aware of arrays...
 * <p>Created by Jeff Gaynor<br>
 * on 2/2/21 at  7:23 AM
 */
public class MyArrayList<V> extends ArrayList<V> {
    public void add(V[] vs){
        for(V v : vs){
            add(v);
        }
    }

    public MyArrayList() {
    }
    public MyArrayList(V[] vs) {
        add(vs);
    }

}
