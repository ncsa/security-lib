package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.module.ModuleMap;
import edu.uiuc.ncsa.qdl.util.StemVariable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  4:15 PM
 */
public class SymbolTableImpl extends AbstractSymbolTable implements SymbolTable {
    NamespaceResolver namespaceResolver;

    public SymbolTableImpl(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    protected HashMap<String, Object> map = new HashMap();
    static final int NULL_TYPE = 0;
    static final int INTEGER_TYPE = 1;
    static final int BOOLEAN_TYPE = 2;
    static final int STRING_TYPE = 3;
    static final int VARIABLE_TYPE = 4;

    @Override
    public void setStringValue(String variableName, String value) {
        setValue(variableName, value);
    }

    @Override
    public void setLongValue(String variableName, Long value) {
        setValue(variableName, value);
    }
    @Override
       public void setDecimalValue(String variableName, BigDecimal value) {
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
    public void setValue(String variableName, Object value) {
     //   if(setModuleValue(variableName, value)) return;
        if (!isStem(variableName)) {
            map.put(variableName, value);
            return;
        }
        if(isTotalStem(variableName)){
            // Simplest case of setting a. := stem. No resolution. 
            map.put(variableName, value);
            return;
        }
        // So the stem is compound, like a.b.c and requires resolving variable values from the right to
        // see what the actual indix for this is. Then the stem will be set to that index.
        // So if a.b.c resolves to a.2, then a.2 is set to the value (which may be a stem).
        String head = getStemHead(variableName);
        String tail = getStemTail(variableName);
        if (isStem(tail)) {
            Object xxx = resolveValue(tail);
            tail = xxx.toString();
        }
        String tailKey = tail;
        if (map.containsKey(tail)) {
            Object tailValue = map.get(tail);
            if(tailValue != null){
                tailKey = tailValue.toString();
            }
        }

        StemVariable stemVar = null;
        if (map.containsKey(head)) {
            stemVar = (StemVariable) map.get(head);
        } else {
            // Completely new entry. Make a stem, add the value, return
            StemVariable entry = new StemVariable();

            if(value == null) {
                map.put(head, entry);
            }else {
                entry.put(tailKey, value);
                map.put(head, entry);
            }
            return;
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

    protected boolean isImported(String var) {
        return var.contains("#");
    }

   /* protected Module findModule(String variable) {
        if (isImported(variable)) {
            String head = getHashHead(variable);
            String tail = getHashTail(variable);
            String namespace;
            if (namespaceResolver.hasAlias(head)) {
                namespace = namespaceResolver.getByAlias(head);
            } else {
                namespace = head;
            }
            return importedModules.get(namespace);

        }
        return null;
    }
*/
    /*protected Object checkImports(String variable) {
        Module module = findModule(variable);
        if (module != null) {
            return module.getSymbols().resolveValue(getHashTail(variable));
        }
        return null;
    }*/

    /**
     * This will do lookups <b>including resolutions for stem variables.</b>
     *
     * @param variable
     * @return
     */
    @Override
    public Object resolveValue(String variable) {
   /*     Object obj = checkImports(variable);
        if (obj != null) {
            return obj;
        }
   */     boolean isStem = isStem(variable);
        if (!isStem) {
            if (map.containsKey(variable)) {
                return map.get(variable);
            }
            return null;
        }
        String head = getStemHead(variable);
        if (!map.containsKey(head)) {
            return null;
        }
        String tail = getStemTail(variable);
        if (tail == null || tail.isEmpty()) {
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
            String head = getStemHead(symbol);
            if (!map.containsKey(head)) {
                return false;
            }
            StemVariable stem = (StemVariable) map.get(head);
            String tail = getStemTail(symbol);
            if (tail == null || tail.isEmpty()) {
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
            map.remove(getStemHead(symbol));
            return;
        } else {
            map.remove(symbol);
            return;
        }
    }

    @Override
    public void setStemVariable(String key, StemVariable stem) {
        if (key.endsWith(".")) {
            // easy case -- just replace the entire stem.
            map.put(key, stem);
            return;
        }
        String h = getStemHead(key);
        StemVariable stemVariable;

        if (map.containsKey(h)) {
            stemVariable = (StemVariable) map.get(h);
        } else {
            stemVariable = new StemVariable();
            map.put(h, stemVariable);
        }

        String t = getStemTail(key);
        stemVariable.put(t, stem);
    }

    @Override
    public String toString() {
        return "SymbolTable[" +
                "map=" + map +
                ']';
    }

    @Override
    public Set<String> listVariables() {
        return map.keySet();
    }

    ModuleMap importedModules = new ModuleMap();

    @Override
    public void addModule(Module module) {
        if (importedModules.containsKey(module.getNamespace())) {
            return;
        }
        importedModules.put(module);
    }

    @Override
    public Map getMap() {
        return map;
    }
}
