package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;

import static edu.uiuc.ncsa.qdl.variables.StemVariable.STEM_INDEX_MARKER;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.STACKS_TAG;

/**
 * /**
 * This has a list of {@link SymbolTableImpl}s that are searched in a particular order. This
 * is what gives loops, conditionals etc. their local state.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/20 at  7:31 AM
 */
public class SymbolStack extends AbstractSymbolTable {
    public SymbolStack() {
        addParent(new SymbolTableImpl());
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
    public SymbolStack(ArrayList<SymbolTable> parentTables) {
        for (SymbolTable s : parentTables) {
            // use this and not addParent, since that reverses their order, inserting a the front of the list
            getParentTables().add(s);
        }
        addParent(new SymbolTableImpl());
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


    ArrayList<SymbolTable> parentTables;

    public void addParent(SymbolTableImpl symbolTableInterface) {
        getParentTables().add(0, symbolTableInterface);
    }

    /**
     * Get the current most local symbol table.
     *
     * @return
     */
    public SymbolTable getLocalST() {
        return getParentTables().get(0);
    }

    /**
     * Looks through the symbol tables in this state and returns the value for the variable <b>or</b>
     * null if there is no value.
     *
     * @param var
     * @return
     */
    protected Object findValueInATable(String var) {
        for (SymbolTable s : getParentTables()) {
            Object obj = s.resolveValue(var);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    public Object resolveValue(String variableName) {
        return findValueInATable(variableName); // to keep current code working
    }

    /**
     * Returns the value for a variable. This used to do this for a stem variable but the
     * algorithm it used was too simple-minded ultimately. Since it requres access to
     * namespaces. it was moved to the {@link State} object.
     *
     * @param variableName
     * @return
     * @deprecated
     */
    public Object oldResolveValue(String variableName) {
        if (isStem(variableName)) {

            String head = getStemHead(variableName);

            if (null == findValueInATable(head)) {
                // this is not defined any place, so it gets a null.
                return null;
            }
            if (variableName.endsWith(STEM_INDEX_MARKER)) {
                return findValueInATable(variableName);
            }

            String tail = getStemTail(variableName);
            // the real issue is that a stem like a.b.c.d.e may have each of the variables in
            // a different and effectively random symbol table. 
            StringTokenizer tokenizer = new StringTokenizer(tail, STEM_INDEX_MARKER);
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
                    currentVariable = vars[i] + STEM_INDEX_MARKER + returnedValue.toString();
                }
                Object obj = findValueInATable(currentVariable);
                if (obj != null) {
                    returnedValue = obj;
                } else {
                    returnedValue = currentVariable;
                }

            }
            return findValueInATable(head + returnedValue); // head include final .
        }

        Object obj = findValueInATable(variableName);
        return obj;
    }

    public void setStringValue(String variableName, String value) {
        setValue(variableName, value);
    }

    @Override
    public void setValue(String variableName, Object value) {
        SymbolTable st = getRightST(variableName);
        if (st == null) {
            // nothing like this exists, so it goes in the local environment
            st = getLocalST();
        }
        st.setValue(variableName, value);
/*
        if (!isStem(variableName)) {
               st.setValue(variableName, value);
               return;
           }
        if (isTotalStem(variableName)) {
            // case is setting something like a. := ...
            // Just set it. No tail resolution needed.
            st.setValue(variableName, value);
            return;
        }


        String t = getStemTail(variableName);
        Object resolvedTail = resolveValue(t);
        st.setValue(getStemHead(variableName) + (resolvedTail==null?t:resolvedTail), value);
        return;
*/
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
        return getLocalST();
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
        getLocalST().setRawValue(rawName, rawValue);
    }

    public void clear() {
        // clear only applies to the current state.
        getLocalST().clear();
    }


    public boolean isDefined(String variable) {
        return findValueInATable(variable) != null;
    }

    public void remove(String symbol) {
        getLocalST().remove(symbol);
    }

    public void setStemVariable(String key, StemVariable stem) {
        if (!isCompoundStem(key)) {
            if (isTotalStem(key)) {
                if (isDefined(key)) {
                    getRightST(key).setStemVariable(key, stem);
                    return;
                } else {
                    getLocalST().setStemVariable(key, stem);
                }
                return;

            }
            // much harder is if it is a long stem a.b.c...
            String h = getStemHead(key);
            String t = getStemTail(key);
            Object newKey = findValueInATable(t);
            if (newKey == null) {
                getLocalST().setStemVariable(key, stem);
                return;
            }
        }
        getRightST(key).setStemVariable(key, stem);
    }

    public TreeSet<String> listVariables() {
        TreeSet<String> vars = new TreeSet<>();
        for (SymbolTable st : getParentTables()) {
            vars.addAll(st.listVariables());
        }
        return vars;
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
            SymbolTableImpl symbolTable = new SymbolTableImpl();
            JSONObject jsonObject = array.getJSONObject(i);
            symbolTable.getMap().putAll(jsonObject);

        }
        return;
    }

    @Override
    public Map getMap() {
        throw new NotImplementedException("Don't call this. It makes no sense in a stack");
    }

    @Override
    public int getSymbolCount() {
        int count = 0;
        for (SymbolTable symbolTable : getParentTables()) {
            count = count + symbolTable.getSymbolCount();
        }
        return count;
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        xsw.writeStartElement(XMLConstants.STACKS_TAG);
        xsw.writeComment("The list of symbol tables for this state.");
        for (SymbolTable st : getParentTables()) {
            st.toXML(xsw);
        }
        xsw.writeEndElement();
    }

    public void fromXML(XMLEventReader xer) throws XMLStreamException {
        // points to stacks tag
        XMLEvent xe = xer.nextEvent(); // moves off the stacks tag.
        // no attributes or such with the stacks tag.
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case XMLConstants.STACK_TAG:
                            SymbolTableImpl si = new SymbolTableImpl();
                            si.fromXML(xer);
                            if(0 < si.getSymbolCount()) {
                                addParent(si);
                            }
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(STACKS_TAG)) {
                        return;
                    }
                    break;
            }
            xe = xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + STACKS_TAG );

    }
}
