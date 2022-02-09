package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.variables.StemVariable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/20 at  4:15 PM
 */
public class SymbolTableImpl extends AbstractSymbolTable implements SymbolTable {

    public SymbolTableImpl() {
    }

    protected HashMap<String, Object> map = new HashMap();


    /**
     * Called internally after all checks etc, have been done.
     *
     * @param variableName
     * @param value
     */
    public void setValue(String variableName, Object value) {
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
        // see what the actual index for this is. Then the stem will be set to that index.
        // So if a.b.c resolves to a.2, then a.2 is set to the value (which may be a stem).
        String head = getStemHead(variableName);
        String tail = getStemTail(variableName);
        if (isStem(tail)) {
            Object xxx = resolveValue(tail);
            tail = xxx==null?tail:xxx.toString();
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
     * Clear all the values from the current table.
     */
    @Override
    public void clear() {
        map = new HashMap();
    }

    @Override
    public Object resolveValue(String variable, int startIndex) {
        return resolveValue(variable);
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
        // from here on almost never gets called after some parser updates,
        // TODO -- track down the couple of edge cases in tests where this is exercised.
        // See also setValue
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
            // So this means the compoud tail like "0.0" does not exist either.
            // This is a new key and should be returned unaltered.
            actualKey = zzzz==null?tail:zzzz.toString();
        }
        if (map.containsKey(tail)) {
            actualKey = map.get(tail).toString();
        }
        StemVariable stem = (StemVariable) map.get(head);
        return stem.get(actualKey);
    }

    /**
     * Return if the symbol is defined. 
     *
     * @param symbol
     * @return
     */
    @Override
    public boolean isDefined(String symbol) {
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
        if (key.endsWith(STEM_INDEX_MARKER)) {
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



    @Override
    public Map getMap() {
        return map;
    }

    @Override
    public int getSymbolCount() {
        return map.size();
    }

    UUID id = UUID.randomUUID();

    public UUID getID() {
        return id;
    }

}
