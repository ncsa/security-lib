package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * /**
 * This has a list of {@link SymbolTableImpl}s that are searched in a particular order. This
 * is what gives loops, conditionals etc. their local state.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  7:31 AM
 */
public class SymbolStack extends AbstractSymbolTable {
    NamespaceResolver namespaceResolver;

    public SymbolStack(NamespaceResolver namespaceResolver) {
        this.namespaceResolver = namespaceResolver;
    }

    @Override
    public void setDecimalValue(String variableName, BigDecimal value) {
            setValue(variableName, value);
    }

    /**
     * This constructor takes all of the parents and then pushes a new {@link SymbolTableImpl}
     * on to the stack.
     *
     * @param parentTables
     */
    public SymbolStack(NamespaceResolver namespaceResolver, ArrayList<SymbolTable> parentTables) {
        this.parentTables = parentTables;
        this.namespaceResolver = namespaceResolver;
        parentTables.add(new SymbolTableImpl(namespaceResolver));
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

    public void setStringValue(String variableName, String value) {
        getRightST(variableName).setStringValue(variableName, value);
    }

    @Override
    public void setValue(String variableName, Object value) {
           getRightST(variableName).setValue(variableName, value);
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

    public void setLongValue(String variableName, Long value) {
        getRightST(variableName).setLongValue(variableName, value);
    }

    public void setBooleanValue(String variableName, Boolean value) {
        getRightST(variableName).setBooleanValue(variableName, value);
    }

    public void setRawValue(String rawName, String rawValue) {
        getRightST(rawName).setRawValue(rawName, rawValue);
    }

    public void clear() {
        // clear only applies to the current state.
        getTopST().clear();
    }

    public Object resolveValue(String variable) {
        if(isStem(variable)){
            // The tail may be in a different symbol table than the head.
            String tail = getStemTail(variable);
            Object value = getRightST(tail).resolveValue(tail);
/*
            String newVar = getStemHead(variable) + value;
            Object foo = getRightST(newVar).resolveValue(newVar);
*/

            // FIX ME!! THis qctually requires we loop since there may be multiple keys all in
            // different scopes. e.g. a.b.c.d.
/*
            while(isStem(newVar)){

            }
*/
            if(value != null){
                String newVar = getStemHead(variable) + value;
                return getRightST(newVar).resolveValue(newVar);
            }
        }
        return getRightST(variable).resolveValue(variable);
    }


    public boolean isDefined(String variable) {
        return getRightST(variable).isDefined(variable);
    }

    public void remove(String symbol) {
        getTopST().remove(symbol);
    }

    public void setStemVariable(String key, StemVariable stem) {
        getRightST(key).setStemVariable(key, stem);
    }

    public Set<String> listVariables() {
        Set<String> vars = new HashSet<>();
        for(SymbolTable st : getParentTables()){
          vars.addAll(st.listVariables());
        }
        return vars;
    }

    public void addModule(Module module) {
           throw new NotImplementedException();
    }
}
