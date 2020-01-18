package edu.uiuc.ncsa.security.util.qdl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * /**
 * This has a list of {@link SymbolTableImpl}s that are searched in a particular order. This
 * is what gives loops, conditionals etc. their local state.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  7:31 AM
 */
public class SymbolStack implements SymbolTable {
    /**
     * Use this for creating the very first one (as a convienience). The result is this
     * stack with an empty symbol table.
     *
     */
    public SymbolStack() {
        addParent(new SymbolTableImpl());
    }

    /**
     * This constructor takes all of the parents and then pushes a new {@link SymbolTableImpl}
     * on to the stack.
     *
     * @param parentTables
     */
    public SymbolStack(ArrayList<SymbolTable> parentTables) {
        this.parentTables = parentTables;
        parentTables.add(new SymbolTableImpl());
    }

    /*
        Note that the order of the parents is that the most recent comes 0th. So the local table
        is at index 0, then its parent is 1, ... the very last table is the global state.
        The purpose of this stack is to keep these straight and manage them so all the right variables
        end up in the right environment.
         */
    public ArrayList<SymbolTable> getParentTables() {
        if (parentTables == null) {
            parentTables = new ArrayList<>();
        }
        return parentTables;
    }

    public void setParentTables(ArrayList<SymbolTable> parentTables) {
        this.parentTables = parentTables;
    }

    ArrayList<SymbolTable> parentTables;

    public void addParent(SymbolTableImpl symbolTableInterface) {
        getParentTables().add(0, symbolTableInterface);
    }

    /**
     * Used for retrieving the parents of this to, e.g., make another environment at the same level.
     *
     * @return
     */
    public List<SymbolTable> getOnlyParents() {
        ArrayList<SymbolTable> parents = new ArrayList<>();
        System.arraycopy(parentTables, 1, parents, 0, parentTables.size() - 1);
        return parents;
    }

    public SymbolTable getTopST() {
        return getParentTables().get(0);
    }

    protected SymbolTable searchSTs(String variableName) {
        for (SymbolTable s : getParentTables()) {
            if (s.isDefined(variableName)) return s;
        }
        return null;
    }

    @Override
    public void setStringValue(String variableName, String value) {
        getRightST(variableName).setStringValue(variableName, value);
    }

    /**
     * Generally use this method. So the searchSTs looks in all the tables, including the current one. If none of them
     * work, then the default is to work with the zero-th table. So this searches and returns
     * the correct table.
     *
     * @param variableName
     * @return
     */
    protected SymbolTable getRightST(String variableName) {
        SymbolTable symbolTable = searchSTs(variableName);
        if (symbolTable == null) {
            symbolTable = getParentTables().get(0);
        }
        return symbolTable;
    }

    @Override
    public void setLongValue(String variableName, Long value) {
        getRightST(variableName).setLongValue(variableName, value);
    }

    @Override
    public void setBooleanValue(String variableName, Boolean value) {
        getRightST(variableName).setBooleanValue(variableName, value);
    }

    @Override
    public void setRawValue(String rawName, String rawValue) {
        getRightST(rawName).setRawValue(rawName, rawValue);
    }

    @Override
    public void clear() {
        // clear only applies to the current state.
        getTopST().clear();
    }

    @Override
    public Object resolveValue(String variable) {
        return getRightST(variable).resolveValue(variable);
    }

    @Override
    public boolean isDefined(String variable) {
        return getRightST(variable).isDefined(variable);
    }

    @Override
    public void remove(String symbol) {
        getTopST().remove(symbol);
    }

    @Override
    public void setStemVariable(String key, StemVariable stem) {
        getRightST(key).setStemVariable(key, stem);
    }
}
