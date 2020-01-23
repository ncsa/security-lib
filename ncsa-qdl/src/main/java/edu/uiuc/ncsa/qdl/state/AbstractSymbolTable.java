package edu.uiuc.ncsa.qdl.state;

/**
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
        return getHead(stem, ".");
    }

    protected String getStemTail(String stem) {
        return getTail(stem, ".");
    }

    protected String getHashHead(String var) {
        return getHead(var, "#");
    }

    protected String getHashTail(String var) {
        return getTail(var, "#");

    }

    protected String getHead(String var, String delimiter) {
        return var.substring(0, var.indexOf(delimiter) + 1);
    }

    protected String getTail(String var, String delimiter) {
        return var.substring(var.indexOf(delimiter) + 1);

    }

    protected boolean isStem(String variable) {
        return variable.contains(".");
    }

    /**
     * This tests if the stem is compound like a.b.c
     * @param var
     * @return
     */
    protected boolean isCompoundStem(String var) {
        return isStem(var) && (var.indexOf(".") != var.lastIndexOf("."));
    }

    /**
     * For the case of a stem like a. Returns false if it is of the form a.b. or some such.
     * @param var
     * @return
     */
    protected boolean isTotalStem(String var){
        return isStem(var) && var.endsWith(".") && !isCompoundStem(var);
    }
}
