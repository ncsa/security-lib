package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.util.StemVariable;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.math.BigDecimal;
import java.util.*;

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
        for (SymbolTable s : parentTables) {
            // use this and not addParent, since that reverses their order, inserting a the front of the list
            getParentTables().add(s);
        }
        this.namespaceResolver = namespaceResolver;
        addParent(new SymbolTableImpl(namespaceResolver));
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

    protected Object findValueInATable(String var) {
        for (SymbolTable s : getParentTables()) {
            if (s.isDefined(var)) {
                return s.resolveValue(var);
            }
        }
        return null;
    }


    public Object resolveValue(String variableName) {
        if (isStem(variableName)) {

            String head = getStemHead(variableName);

            if (null == findValueInATable(head)) {
                // this is not defined any place, so it gets a null.
                return null;
            }
            if (variableName.endsWith(".")) {
                return findValueInATable(variableName);
            }

            String tail = getStemTail(variableName);
            // the real issue is that a stem like a.b.c.d.e may have each of the variables in
            // a different and effectively random symbol table. 
            StringTokenizer tokenizer = new StringTokenizer(tail, ".");
            String[] vars = new String[tokenizer.countTokens()];
            for (int i = tokenizer.countTokens() - 1; i >= 0; i--) {
                vars[i] = tokenizer.nextToken();
            }
            // These are reversed. Now we start the lookup  and this means
            // we start working our way backwards

            String currentVariable = "";
            boolean isFirstPass = true;
            Object returnedValue = null;
            for (int i = 0; i < vars.length; i++) {
                if (isFirstPass) {
                    isFirstPass = false;
                    currentVariable = vars[i];
                } else {
                    currentVariable = vars[i] + "." + returnedValue.toString();
                }
                Object obj = findValueInATable(currentVariable);
                if (obj != null) {
                    returnedValue = obj;
                } else {
                    returnedValue = currentVariable;
                }

            }
      //      System.out.println("SymbolStack (" + getParentTables().size() + ") returning " + variableName + " = " + returnedValue);
            return findValueInATable(head + returnedValue); // head include final .
        }
     /*   for(SymbolTable s : getParentTables()){
            if(s.isDefined(variableName)) {
                System.out.println("\n   checking  parents " + variableName + " = " + s.resolveValue(variableName));
            }

        }*/
        Object obj = findValueInATable(variableName);
        //System.out.println("SymbolStack2  (" + getParentTables().size() + ") returning " + variableName + " = " + obj);


        return obj;
    }

    public void setStringValue(String variableName, String value) {
        setValue(variableName, value);
        // getRightST(variableName).setStringValue(variableName, value);
    }

    @Override
    public void setValue(String variableName, Object value) {
        SymbolTable st = getRightST(variableName);
        if (st == null) {
            // nothing like this exists, so it goes in the local environment
            getTopST().setValue(variableName, value);
            return;
        }
        if (isTotalStem(variableName)) {
            // case is setting something like a. := ...
            // Just set it. No tail resolution needed.
            st.setValue(variableName, value);
            return;
        }
        if (!isStem(variableName)) {
            st.setValue(variableName, value);
            return;
        }

        String t = getStemTail(variableName);
        Object resolvedTail = resolveValue(t);
        st.setValue(getStemHead(variableName) + (resolvedTail==null?t:resolvedTail), value);
        return;
    }

    /**
     * Generally use this method. So the searchSTs looks in all the tables, including the current one. If none of them
     * work, then the default is to work with the zero-th table. So this searches and returns
     * the correct table.
     * <br/><br/> <b>NOTE</b> this will look for the variable, if simple and the stem -- with no resolution of the
     * tail. So if this is passed a.b.c.d.e it will return the {@link SymbolTable} containing a. The tail has to be
     * resolved elsewhere. A null means this variable is not defined anyplace.
     *
     * @param variableName
     * @return
     */
    protected SymbolTable getRightST(String variableName) {
        String variableToCheck = variableName;
        if (isStem(variableName)) {
            variableToCheck = getStemHead(variableName);
        }
        for (SymbolTable s : getParentTables()) {
            if (s.isDefined(variableToCheck)) {
                return s;
            }
        }
        return null;
    }

    public void setLongValue(String variableName, Long value) {
        setValue(variableName, value);
    }

    public void setBooleanValue(String variableName, Boolean value) {
        setValue(variableName, value);
    }

    public void setRawValue(String rawName, String rawValue) {
        if (isDefined(rawName)) {
            getRightST(rawName).setRawValue(rawName, rawValue);
        }
        getTopST().setRawValue(rawName, rawValue);
    }

    public void clear() {
        // clear only applies to the current state.
        getTopST().clear();
    }


    public boolean isDefined(String variable) {
        return resolveValue(variable) != null;
    }

    public void remove(String symbol) {
        getTopST().remove(symbol);
    }

    public void setStemVariable(String key, StemVariable stem) {
        if (!isCompoundStem(key)) {
            if (isTotalStem(key)) {
                if (isDefined(key)) {
                    getRightST(key).setStemVariable(key, stem);
                    return;
                } else {
                    getTopST().setStemVariable(key, stem);
                }
                return;

            }
            // much harder is if it is a long stem a.b.c...
            String h = getStemHead(key);
            String t = getStemTail(key);
            Object newKey = findValueInATable(t);
            if (newKey == null) {
                getTopST().setStemVariable(key, stem);
                return;
            }


        }

        getRightST(key).setStemVariable(key, stem);
    }

    public Set<String> listVariables() {
        Set<String> vars = new HashSet<>();
        for (SymbolTable st : getParentTables()) {
            vars.addAll(st.listVariables());
        }
        return vars;
    }

    public void addModule(Module module) {
        throw new NotImplementedException();
    }

    public JSON toJSON() {
        JSONArray array = new JSONArray();
        for (SymbolTable st : getParentTables()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(st.getMap());
            array.add(jsonObject);
        }
        return array;
    }

    public void fromJSON(JSONArray array) {
        for (int i = array.size(); i < 0; i--) {
            // these were put in in reverse order, so have to pop them out.
            SymbolTableImpl symbolTable = new SymbolTableImpl(NamespaceResolver.getResolver());
            JSONObject jsonObject = array.getJSONObject(i);
            symbolTable.getMap().putAll(jsonObject);

        }
        return;
    }

    @Override
    public Map getMap() {
        return null;
    }
}
