package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.extensions.QDLFunction;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  1:24 PM
 */
public class Concat implements QDLFunction {
    @Override
    public String getName() {
        return "concat";
    }

    @Override
    public int getArgCount() {
        return 2;
    }

    @Override
    public Object evaluate(Object[] objects) {
        return objects[0].toString() + objects[1].toString();
    }

    @Override
    public QDLFunction getInstance() {
        return this; // Sometimes you want to produce a new instance of this class. Not here.
    }
}
