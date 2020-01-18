package edu.uiuc.ncsa.security.util.qdl.util;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  4:15 PM
 */
public class SymbolTableImpl implements SymbolTable {
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

    @Override
    public void setStringValue(String variableName, String value) {
        setValue(variableName, value);
    }

    @Override
    public void setLongValue(String variableName, Long value) {
        setValue(variableName, value);
    }

    @Override
    public void setBooleanValue(String variableName, Boolean value) {
        setValue(variableName, value);
    }

    /**
     * Called internally after all checks etc, have been done. In particular, this does NOT check if the
     * value is a reference to another variable ({@link #setRawValue(String, String)}, however, will resolve
     * a variable reference.
     *
     * @param variableName
     * @param value
     */
    protected void setValue(String variableName, Object value) {
        if (!isStem(variableName)) {
            map.put(variableName, value);
            return;
        }
        String head = getHead(variableName);
        String tail = getTail(variableName);
        if (isStem(tail)) {
            Object xxx = resolveValue(tail);
            tail = xxx.toString();
        }
        if (map.containsKey(tail)) {
            Object object = map.get(tail);

        }
        StemVariable stemVar = null;
        if (map.containsKey(head)) {
            stemVar = (StemVariable) map.get(head);
        } else {
            stemVar = new StemVariable();
            map.put(head, stemVar);
        }
        String keyValue = tail.toString();
        if (map.containsKey(tail)) {
            keyValue = map.get(tail).toString();
        }
        stemVar.put(keyValue, value);
    }

    /**
     * Sets the value from raw input from the parser. This will determine the type and set it.
     * This means that, for instance, you would supply a string surrounded by single quotes.
     * If you need to set a value explicitly, use the setXXXValue command.
     *
     * @param rawName
     * @param rawValue
     */
    @Override
    public void setRawValue(String rawName, String rawValue) {
        String realName = null;
        int valueType = getType(rawValue);
        // do easy stuff
        Object realValue = null;

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
        setValue(rawName, realValue);

    }

    /**
     * Clear all the values from the current table.
     */
    @Override
    public void clear() {
        map = new HashMap();
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
    @Override
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
        if(tail == null || tail.isEmpty()){
            // Simplest case, they just want the whole stem variable.
           return map.get(head);
        }
        String actualKey = tail;
        if (isStem(tail)) {
            Object zzzz = resolveValue(tail);
            actualKey = zzzz.toString();
        }
        if (map.containsKey(tail)) {
            actualKey = map.get(tail).toString();
        }
        StemVariable stem = (StemVariable) map.get(head);
        return stem.get(actualKey);
    }

    /**
     * Return if the symbol is defined. This needs work for stem variables.
     *
     * @param symbol
     * @return
     */
    @Override
    public boolean isDefined(String symbol) {
        if (isStem(symbol)) {
            String head = getHead(symbol);
            if (!map.containsKey(head)) {
                return false;
            }
            StemVariable stem = (StemVariable) map.get(head);
            String tail = getTail(symbol);
            if(tail == null || tail.isEmpty()){
                return true;
            }
            Object foo = resolveValue(tail);
            if (foo == null) {
                return stem.containsKey(tail);
            }
            return stem.containsKey(foo.toString()); // since the value may be an integer, e.g.
        }
        return map.containsKey(symbol);
    }

    /**
     * remove a symbol. Returns true if removed. False otherwise.
     *
     * @param symbol
     * @return
     */
    @Override
    public void remove(String symbol) {
        if (!isDefined(symbol)) {
            return;
        }
        if (isStem(symbol)) {
            map.remove(getHead(symbol));
            return;
        } else {
            map.remove(symbol);
            return;
        }
    }

    @Override
    public void setStemVariable(String key, StemVariable stem) {
        if(!key.endsWith(".")){
            throw new IllegalArgumentException("Error: stem variable names must end with a period \""+ key + "\" did not " );
        }
        map.put(key, stem);
    }

    @Override
    public String toString() {
        return "SymbolTable[" +
                "map=" + map +
                ']';
    }
}
