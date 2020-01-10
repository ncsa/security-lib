package edu.uiuc.ncsa.security.util.qdl.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  4:15 PM
 */
public class SymbolTable {
    protected HashMap<String, Object> map = new HashMap();
    static final int NULL_TYPE = 0;
    static final int INTEGER_TYPE = 1;
    static final int BOOLEAN_TYPE = 2;
    static final int STRING_TYPE = 3;
    static final int VARIABLE_TYPE = 4;

    protected int getType(String raw) {
        if (raw.equals("null")) return NULL_TYPE;
        if (raw.equals("true") || raw.equals("false")) return BOOLEAN_TYPE;
        if (raw.startsWith("'") && raw.endsWith("'")) return STRING_TYPE;
        try {
            Integer.parseInt(raw);
            return INTEGER_TYPE;
        } catch (Throwable t) {
            // do nothing.
        }
        return VARIABLE_TYPE;
    }

    /**
     * Sets the value from raw input from the parser. This will determine the type and set it.
     *
     * @param rawName
     * @param rawValue
     */
    public void setValue(String rawName, String rawValue) {
        String realName = null;
        int valueType = getType(rawValue);
        // do easy stuff
        Object realValue = null;
        boolean isStemVar = isStem(rawName);

        realName = rawName;
        switch (valueType) {
            case NULL_TYPE:
                return; // don't store nulls
            case STRING_TYPE:
                realValue = rawValue.trim().substring(1, rawValue.length() - 1);
                break;
            case INTEGER_TYPE:
                realValue = Long.parseLong(rawValue);
                break;
            case BOOLEAN_TYPE:
                realValue = Boolean.parseBoolean(rawValue);
                break;
            default:
            case VARIABLE_TYPE:
                realValue = map.get(realName);
                break;
        }
        if (!isStemVar) {
            map.put(realName, realValue);
            return;
        }
        // so we have a stem variable
        // then it's a stem variable
        String head = getHead(rawName);
        String tail = getTail(rawName);
        if (map.containsKey(tail)) {
            Object object = map.get(tail);

        }
        Map<String, Object> stemVar = null;
        if (map.containsKey(head)) {
            stemVar = (Map<String, Object>) map.get(head);
        } else {
            stemVar = new HashMap<String, Object>();
            map.put(head, stemVar);
        }
        String keyValue = null;
        if (map.containsKey(tail)) {
            keyValue = map.get(tail).toString();
        }
        stemVar.put(keyValue, realValue);
        // store stem variables with the '.', so there can be like-named variables,
        // e.g. a stem variable foo. is different from the simple value foo.

    }

    protected String getHead(String stem) {
        return stem.substring(0, stem.indexOf(".") + 1);
    }

    protected String getTail(String stem) {
        return stem.substring(stem.indexOf(".") + 1);

    }

    protected boolean isStem(String variable) {
        return variable.contains(".");
    }

    /**
     * This will do lookups <b>including resolutions for stem variables.</b>
     *
     * @param variable
     * @return
     */
    public Object resolveValue(String variable) {
        boolean isStem = isStem(variable);
        if (!isStem) {
            if (map.containsKey(variable)) {
                return map.get(variable);
            }
            return null;
        }
        String head = getHead(variable);
        if (!map.containsKey(head)) {
            return null;
        }
        String tail = getTail(variable);
        String actualKey = tail;
        if (map.containsKey(tail)) {
            actualKey = map.get(tail).toString();
        }
        Map<String, Object> stem = (Map<String, Object>) map.get(head);

        return stem.get(actualKey);
    }

    /**
     * Return if the symbol is defined. This needs work for stem variables.
     *
     * @param symbol
     * @return
     */
    public boolean isDefined(String symbol) {
        if(isStem(symbol)){
            String head = getHead(symbol);
            if(!map.containsKey(head)){
                return false;
            }
            Map<String, Object> stem = (Map<String, Object>) map.get(head);
            String tail = getTail(symbol);
            Object foo = resolveValue(tail);
            if(foo == null){
                return stem.containsKey(tail);
            }
            return stem.containsKey(foo.toString()); // since the value may be an integer, e.g.
        }
        return map.containsKey(symbol);
    }

    /**
     * remove a symbol. Returns true if removed. False otherwise.
     * @param symbol
     * @return
     */
    public void remove(String symbol){
        if(!isDefined(symbol)){
            return;
        }
        if(isStem(symbol)){
           map.remove(getHead(symbol));
           return;
        }else{
            map.remove(symbol);
            return;
        }
    }
}
