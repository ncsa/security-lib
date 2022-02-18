package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.util.InputFormUtil;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.util.*;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.STACKS_TAG;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.VARIABLE_STACK;

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

    public boolean isEmpty() {
        for (SymbolTable s : parentTables) {
            // use this and not addParent, since that reverses their order, inserting a the front of the list
            if (!getParentTables().isEmpty()) {
                return false;
            }
        }
        return true;
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

    public int parentCount() {
        return getParentTables().size();
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

    List<UUID> addedTables = new ArrayList<>();

    protected void add(SymbolTableImpl si) {
        if (si.getMap().isEmpty()) {
            return;
        }
        if (addedTables.contains(si.getID())) {
            return;
        }
        addedTables.add(si.getID());
        getParentTables().add(si);
    }

    public void addAll(List<SymbolTable> tables) {
        for (SymbolTable symbolTable : tables) {
            if (symbolTable instanceof SymbolTableImpl) {
                add((SymbolTableImpl) symbolTable);
            }
            if (symbolTable instanceof SymbolStack) {
                SymbolStack symbolStack = (SymbolStack) symbolTable;
                addAll(symbolStack.getParentTables());
            }
        }

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
        return findValueInATable(var, 0);
    }

    /**
     * startIndex = 0 means look in local state too, startIndex = 1 means skip local state.
     *
     * @param var
     * @param startIndex
     * @return
     */
    protected Object findValueInATable(String var, int startIndex) {
        for (int i = startIndex; i < getParentTables().size(); i++) {
            Object obj = getParentTables().get(i).resolveValue(var);
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Gets the value from the most local table. Used mostly for intrinsic values.
     *
     * @param variableName
     * @return
     */
    public Object getLocalValue(String variableName) {
        return getParentTables().get(0).resolveValue(variableName);
    }

    /**
     * Sets the value in the most local table. Used mostly for intrinsic values.
     *
     * @param variableName
     * @param value
     */
    public void setLocalValue(String variableName, Object value) {
        getParentTables().get(0).setValue(variableName, value);
    }

    /**
     * Checks if the most local table contains the value. Used mostly for intrinsic values.
     *
     * @param variableName
     * @return
     */
    public boolean hasLocalValue(String variableName) {
        return null != getParentTables().get(0).resolveValue(variableName);
    }

    public Object resolveValue(String variableName, int startIndex) {
        return findValueInATable(variableName, startIndex); // to keep current code working

    }

    public Object resolveValue(String variableName) {
        return findValueInATable(variableName, 0); // to keep current code working
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
            for(Object key : st.getMap().keySet()){
                Object obj = st.getMap().get(key);
                jsonObject.put(key, key + ":=" + InputFormUtil.inputForm(obj));
            }
            array.add(jsonObject);
        }
        return array;
    }

    /**
     * {@link #fromJSON(JSONArray)}  is busted. {@link #toJSON()} works.  Need way to interpret each
     * entry from its input form that does not require a complete interpreter.
     * @param array
     */
    public void fromJSON(JSONArray array) {
        QDLInterpreter qi = new QDLInterpreter(new State());
        try {
            qi.execute("");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        for (int i = array.size(); i < 0; i--) {
            // these were put in in reverse order, so have to pop them out.
            SymbolTableImpl symbolTable = new SymbolTableImpl();
            JSONObject jsonObject = array.getJSONObject(i);
            for(Object key : jsonObject.keySet()){
                String raw = jsonObject.getString((String) key);
                //symbolTable.getMap().putAll(key, InputFormUtil.);
            }
             getParentTables().add(symbolTable);
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
    public void toXML(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException {
         toXML(xsw);
    }

    @Override
    public void toXML(XMLStreamWriter xsw) throws XMLStreamException {
        //xsw.writeStartElement(XMLConstants.STACKS_TAG);
        xsw.writeStartElement(VARIABLE_STACK);
        xsw.writeComment("The list of symbol tables for this state.");
        for (SymbolTable st : getParentTables()) {
            st.toXML(xsw);
        }
        xsw.writeEndElement();
    }

    /**
     * For version 2,0
     * @param xer
     * @param XMLSerializationState
     * @throws XMLStreamException
     */
    public void fromXML(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        // points to stacks tag
        XMLEvent xe = xer.nextEvent(); // moves off the stacks tag.
        // no attributes or such with the stacks tag.
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    switch (xe.asStartElement().getName().getLocalPart()) {
                        case XMLConstants.VARIABLES_TAG:
                            SymbolTableImpl si = new SymbolTableImpl();
                            si.fromXML(xer, XMLSerializationState);
                            if (0 < si.getSymbolCount()) {
                                add(si);
                            }
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(VARIABLE_STACK)) {
                        if(getParentTables().isEmpty()){
                            // Just make sure there is at least one.
                            getParentTables().add(new SymbolTableImpl());
                        }
                        return;
                    }
                    break;
            }
            xe = xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + VARIABLE_STACK);


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
                            if (0 < si.getSymbolCount()) {
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
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + STACKS_TAG);

    }

    @Override
    public String toString() {
        String out = "[" + getClass().getSimpleName();
        out = out + ", table#=" + getParentTables().size();
        int i = 0;
        int totalSymbols = 0;
        boolean isFirst = true;
        out = ", counts=[";
        for (SymbolTable symbolTable : getParentTables()) {
            if (isFirst) {
                isFirst = false;
                out = out + symbolTable.getMap().size();
            } else {
                out = out + "," + symbolTable.getMap().size();
            }
            totalSymbols += symbolTable.getMap().size();
        }
        out = out + "], total=" + totalSymbols;
        out = out + "]";
        return out;
    }
}
