package edu.uiuc.ncsa.qdl.extensions.example;

import edu.uiuc.ncsa.qdl.extensions.QDLFunction;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<String> getDocumentation() {
        ArrayList<String> docs = new ArrayList<>();
        docs.add("concat(string, string) will concatenate the two arguments");
        docs.add("This is identical in function to the built in '+' operator. It is just part of the");
        docs.add("sample kit for writing a java extension to QDL that is shipped with the standard distro.");
        return docs;
    }
}
