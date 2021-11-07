package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.xml.XMLSerializable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  7:33 AM
 */
public interface SymbolTable extends Serializable, XMLSerializable {

    void setValue(String variableName, Object value);

    void clear();

    Object resolveValue(String variable);
    Object resolveValue(String variable, int startIndex);
    boolean isDefined(String symbol);

    void remove(String symbol);

    void setStemVariable(String key, StemVariable stem);

    @Override
    String toString();

    Set<String> listVariables();

    /**
     * This exists internally to judge the size of the symbol table. It allows for
     * setting limits in server mode.
     * @return
     */
     int getSymbolCount();
    public Map getMap();

    /**
     * This is used by various classes to check if a variable is legal. Compile this using {@link java.util.regex.Pattern}
     * then
     */
    String var_regex = "^[a-zA-Z0-9_$]+[a-zA-Z0-9_$\\.]*";
    String int_regex = "[1-9][0-9]*";

}
