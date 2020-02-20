package edu.uiuc.ncsa.qdl.state;

import static edu.uiuc.ncsa.qdl.util.StemVariable.STEM_INDEX_MARKER;

/**
 * The interface for access to symboles (a.a variables) in QDL. Note that this is not assumed
 * to be namespace aware at all. It is not the task of this component to understand or resolve
 * namespaces.
 * <p>Created by Jeff Gaynor<br>
 * on 1/21/20 at  5:04 PM
 */
public abstract class AbstractSymbolTable implements SymbolTable {
    protected int getType(String raw) {
        if (raw.equals("null")) return SymbolTableImpl.NULL_TYPE;
        if (raw.equals("true") || raw.equals("false")) return SymbolTableImpl.BOOLEAN_TYPE;
        if (raw.startsWith("'") && raw.endsWith("'")) return SymbolTableImpl.STRING_TYPE;
        try {
            Integer.parseInt(raw);
            return SymbolTableImpl.INTEGER_TYPE;
        } catch (Throwable t) {
            // do nothing.
        }
        return SymbolTableImpl.VARIABLE_TYPE;
    }

    protected String getStemHead(String stem) {
        return getHead(stem, STEM_INDEX_MARKER);
    }

    protected String getStemTail(String stem) {
        return getTail(stem, STEM_INDEX_MARKER);
    }

    
    protected String getHead(String var, String delimiter) {
        return var.substring(0, var.indexOf(delimiter) + 1);
    }

    protected String getTail(String var, String delimiter) {
        return var.substring(var.indexOf(delimiter) + 1);

    }

    protected boolean isStem(String variable) {
        return variable.contains(STEM_INDEX_MARKER);
    }

    /**
     * This tests if the stem is compound like a.b.c
     * @param var
     * @return
     */
    protected boolean isCompoundStem(String var) {
        return isStem(var) && (var.indexOf(STEM_INDEX_MARKER) != var.lastIndexOf(STEM_INDEX_MARKER));
    }

    /**
     * For the case of a stem like a. Returns false if it is of the form a.b. or some such.
     * @param var
     * @return
     */
    protected boolean isTotalStem(String var){
        return isStem(var) && var.endsWith(STEM_INDEX_MARKER) && !isCompoundStem(var);
    }
}
