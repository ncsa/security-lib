package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.state.XKey;
import edu.uiuc.ncsa.qdl.state.XThing;

import static edu.uiuc.ncsa.qdl.variables.Constant.UNKNOWN_TYPE;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/22 at  6:07 AM
 */
public class VThing implements XThing {
    public VThing(XKey key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getName() {
        return key.getKey();
    }

    XKey key;

    @Override
    public XKey getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        type = Constant.getType(value);
    }

    Object value;
    int type = UNKNOWN_TYPE;

    public int getType() {
        if (type == UNKNOWN_TYPE) {
            type = Constant.getType(value);
        }
        return type;
    }

}
