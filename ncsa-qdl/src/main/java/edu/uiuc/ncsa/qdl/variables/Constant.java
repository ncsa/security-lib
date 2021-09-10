package edu.uiuc.ncsa.qdl.variables;

import edu.uiuc.ncsa.qdl.functions.FunctionReferenceNode;

import java.math.BigDecimal;

/**
 * Utility class to manage constants for the system. This is the value recorded at parse time
 * <p>Created by Jeff Gaynor<br>
 * on 1/13/20 at  3:07 PM
 */
public class Constant {
    public static int getType(Object object){

        if(object instanceof QDLNull) return NULL_TYPE;
        if(object instanceof String) return STRING_TYPE;
        if(object instanceof Long) return LONG_TYPE;
        if(object instanceof Boolean) return BOOLEAN_TYPE;
        if(object instanceof StemVariable) return STEM_TYPE;
        if(object instanceof BigDecimal) return DECIMAL_TYPE;
        if(object instanceof FunctionReferenceNode) return FUNCTION_TYPE;
        return UNKNOWN_TYPE;
    }
    public static final int UNKNOWN_TYPE = -1;
    public static final int NULL_TYPE = 0;
    public static final int BOOLEAN_TYPE = 1;
    public static final int LONG_TYPE = 2;
    public static final int STRING_TYPE = 3;
    public static final int STEM_TYPE = 4; // these are mixed type
    public static final int DECIMAL_TYPE = 5;
    public static final int FUNCTION_TYPE = 6;
    public static final int COMPLEX_TYPE = 7; // some day...
    Object value;
    int type = UNKNOWN_TYPE;

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public String getString() {
        if (value == null) return null;
        return value.toString();
    }
    public Long getLong(){
        if(value instanceof Long) return (Long)value;
        return 0L;
    }
}
