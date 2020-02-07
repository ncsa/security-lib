package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.util.StemVariable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  7:33 AM
 */
public interface SymbolTable extends Serializable {

    void setValue(String variableName, Object value);
    void setStringValue(String variableName, String value);

    void setDecimalValue(String variableName, BigDecimal value);
    void setLongValue(String variableName, Long value);

    void setBooleanValue(String variableName, Boolean value);

    void setRawValue(String rawName, String rawValue);

    void clear();

    Object resolveValue(String variable);

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
}
