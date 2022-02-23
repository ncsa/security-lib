package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.functions.FKey;
import edu.uiuc.ncsa.qdl.functions.FStack;
import edu.uiuc.ncsa.qdl.functions.FunctionRecord;
import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;
import edu.uiuc.ncsa.qdl.variables.StemVariable;
import edu.uiuc.ncsa.qdl.variables.VStack;
import edu.uiuc.ncsa.qdl.variables.VThing;
import edu.uiuc.ncsa.qdl.xml.XMLConstants;
import edu.uiuc.ncsa.qdl.xml.XMLMissingCloseTagException;
import edu.uiuc.ncsa.qdl.xml.XMLSerializationState;
import net.sf.json.JSONArray;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import java.math.BigDecimal;
import java.util.*;

import static edu.uiuc.ncsa.qdl.xml.XMLConstants.FUNCTION_TABLE_STACK_TAG;
import static edu.uiuc.ncsa.qdl.xml.XMLConstants.VARIABLE_STACK;

/**
 * A stateful stack of things, such as functions. This is the method by which local state
 * is preserved. The zero-th element is the current local table. It is used for functions,
 * variables, modules etc., hence the prefix of <b>X</b> for it and things related to it.
 * <h3>Usage</h3>
 * <p>Create a subclass as needed for your objects. This involves an {@link XStack},
 * {@link XTable}, {@link XKey} and an {@link XThing}.</p>
 * <h3>How state is managed</h3>
 * If we had the following QDL:
 * <pre>
 *     f(x)-&gt;x^2;
 *     block[f(x)-&gt;x^3;...]
 * </pre>
 * Then {@link XStack} subclass for functions inside the block would look like
 * <pre>
 *     table       entry
 *     0           f(x)-&gt;x^3
 *     1           f(x)-&gt;x^2
 * </pre>
 * Calls to <@link {@link XStack#get(XKey)} would peek at 0 and return f(x)-&gt;x^3
 * inside the block. This is how local state overrides the parent state.
 * Blocks of course can be for loops, functions, conditionals etc. Were there no
 * entry for f(x) in the block, then {@link XStack} would return f(x)-&gt;x^2.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 11/8/21 at  6:27 AM
 */
public abstract class XStack<V extends XTable<? extends XKey, ? extends XThing>> {
    /**
     * Take an XStack and add all of the tables in this stack in the correct order
     * to the front of the stack. If XStack is [A,B,C,...] And the existing stack is
     * [P,Q,...] the result is [A,B,C,...,P,Q,...]
     * This is needed when, e.g., creating new local state for function reference resolution
     *
     * @param xStack
     */
    public void addTables(XStack xStack) {
        // add backwards from stack
        for (int i = xStack.getStack().size() - 1; 0 <= i; i--) {
            XTable xt = (XTable) xStack.getStack().get(i);
            if (addedTables.contains(xt.getID())) {
                continue;
            }
            if (!xt.isEmpty()) {
                push((XTable) xStack.getStack().get(i)); // puts at 0th elements each time
                addedTables.add(xt.getID());
            }
        }
    }

    Set<UUID> addedTables = new HashSet<>();

    /**
     * Similar to {@link #addTables(XStack)}, but this appends them to the existing
     * set of tables. If XStack is [A,B,C,...] And the existing stack is
     * [P,Q,...] the result is [P,Q,...,A,B,C,...,]
     *
     * @param xStack
     */
    public void appendTables(XStack xStack) {
        //for (int i = xStack.getStack().size() - 1; 0 <= i; i--) {
        for (int i = 0; i < xStack.getStack().size(); i++) {
            XTable xt = (XTable) xStack.getStack().get(i);
            if (addedTables.contains(xt.getID())) {
                continue;
            }
            if (!xt.isEmpty()) {
                append((V) xStack.getStack().get(i)); // puts at 0th elements each time
                addedTables.add(xt.getID());

            }

        }
    }

    abstract public XStack newInstance();

    abstract public XTable newTableInstance();

    /**
     * Append the table to the end of the stack -- this sets the root for the table.
     *
     * @param v
     */
    public void append(V v) {
        getStack().add(v);
    }

    @Override
    public XStack clone() {
        XStack cloned = newInstance();
        for (XTable ft : getStack()) {
            cloned.append(ft);
        }
        return cloned;
    }

    /**
     * Check that a specific key is in a table starting at the index.
     * This lets you, e.g., skip local state if the start index is positive.
     *
     * @param key
     * @param startTableIndex
     * @return
     */
    public boolean containsKey(XKey key, int startTableIndex) {
        for (int i = startTableIndex; i < getStack().size(); i++) {
            if (getStack().get(i).containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsKey(XKey key) {
        return containsKey(key, 0);
    }

    /**
     * Only returns a non-null element if it is defined in the local (index 0) table.
     *
     * @param key
     * @return
     */
    public XThing localGet(XKey key) {
        if (getStack().isEmpty()) {
            return null;
        }
        return getStack().get(0).get(key);
    }

    public boolean localHas(XKey xkey) {
        if (getStack().isEmpty()) {
            return false;
        }
        return getStack().get(0).get(xkey) != null;

    }

    public XThing get(XKey key) {
        for (XTable<? extends XKey, ? extends XThing> xTable : getStack()) {
            XThing xThing = xTable.get(key);
            if (xThing != null) {
                return xThing;
            }
        }
        return null;
    }

    /**
     * searches for the entry every place except the most local state.
     *
     * @param key
     * @return
     */
    public XThing nonlocalGet(XKey key) {
        for (int i = 1; i < getStack().size(); i++) {
            XThing xThing = getStack().get(i).get(key);
            if (xThing != null) {
                return xThing;
            }
        }
        return null;
    }

    /**
     * Get all of the values from all tables.This returns a flat list.
     *
     * @return
     */
    public List<? extends XThing> getAll() {
        List<? extends XThing> list = new ArrayList<>();
        for (XTable xTable : getStack()) {
            list.addAll(xTable.values());
        }
        return list;
    }

    /**
     * Since all new tables are added at 0, the initial one, called the root, is last. This gets
     * the root {@link XTable}.
     *
     * @return
     */
    public XTable<? extends XKey, ? extends XThing> getRoot() {
        return getStack().get(getStack().size() - 1);
    }

    public List<XTable<? extends XKey, ? extends XThing>> getStack() {
        if (stack == null) {
            stack = new ArrayList();
        }
        return stack;
    }

    public void setStack(List<XTable<? extends XKey, ? extends XThing>> stack) {
        this.stack = stack;
    }

    List<XTable<? extends XKey, ? extends XThing>> stack = new ArrayList<>();

    public boolean isEmpty() {
        boolean empty = true;
        for (XTable<? extends XKey, ? extends XThing> xTable : getStack()) {
            empty = empty && xTable.isEmpty();
        }
        return empty;
    }

    public XTable<XKey, XThing> peek() {
        return (XTable<XKey, XThing>) getStack().get(0);
    }

    public void push(XTable<? extends XKey, ? extends XThing> xTable) {
        getStack().add(0, xTable);
    }

    public void pushNewTable() {
        push(newTableInstance());
    }


    /**
     * Only add this to the local state.
     *
     * @param value
     * @return
     */
    public XThing localPut(XThing value) {
        XThing oldValue = getStack().get(0).get(value);
        getStack().get(0).put(value);
        return oldValue;
    }

    public XThing put(XKey xKey, XThing xThing) {
        for (XTable<? extends XKey, ? extends XThing> xTable : getStack()) {
            if (xTable.containsKey(xKey)) {
                xTable.put(xThing);
                return xThing;
            }
        }
        return peek().put(xKey, xThing);

    }

    public XThing put(XThing value) {
        for (XTable<? extends XKey, ? extends XThing> xTable : getStack()) {
            if (xTable.containsKey(value.getKey())) {
                xTable.put(value);
                return value;
            }
        }
        return peek().put(value);
    }

    /**
     * Removes only the most local entry.
     *
     * @param key
     */
    public void localRemove(XKey key) {
        peek().remove(key);
    }

    /**
     * Removes <i>all</i> references from all tables. This includes all overrides
     * so at the end of this operation there are no references any place.
     *
     * @param key
     */
    public void remove(XKey key) {
        for (XTable<? extends XKey, ? extends XThing> xTable : getStack()) {
            xTable.remove(key);
            addedTables.remove(xTable.getID());
        }
    }

    public int size() {
        int totalSymbols = 0;
        for (XTable xTable : getStack()) {
            totalSymbols += xTable.size();
        }
        return totalSymbols;
    }

    @Override
    public String toString() {
        String out = "[" + getClass().getSimpleName();
        out = out + ", table#=" + getStack().size();
        int i = 0;
        int totalSymbols = 0;
        boolean isFirst = true;
        out = ", counts=[";
        for (XTable functionTable : getStack()) {
            if (isFirst) {
                isFirst = false;
                out = out + functionTable.size();
            } else {
                out = out + "," + functionTable.size();
            }
            totalSymbols += functionTable.size();
        }
        out = out + "], total=" + totalSymbols;
        out = out + "]";
        return out;
    }

    /**
     * Does the grunt work of writing the stack in the right order. You write the start tag,
     * any comments, invoke this, then the end tag. See {@link edu.uiuc.ncsa.qdl.functions.FStack#toXML(XMLStreamWriter, XMLSerializationState)}
     * for a canonical example.
     *
     * @param xsw
     * @throws XMLStreamException
     */
    public void toXML(XMLStreamWriter xsw, XMLSerializationState xmlSerializationState) throws XMLStreamException {
        toXMLNEW(xsw, xmlSerializationState);
    }

    public void toXMLOLD(XMLStreamWriter xsw, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        if (isEmpty()) {
            return;
        }
        xsw.writeStartElement(getXMLStackTag());
        for (int i = getStack().size() - 1; 0 <= i; i--) {
            XTable xTable = getStack().get(i);
            if (xTable.isEmpty()) {
                continue;
            }
            xsw.writeStartElement(getXMLTableTag());
            xsw.writeAttribute(XMLConstants.LIST_INDEX_ATTR, Integer.toString(i));
            xTable.toXML(xsw, XMLSerializationState);
            xsw.writeEndElement(); // end of table.
        }
        if (getStack().get(0).isEmpty()) {
            // write the zero-th elements as an empty table so the structure of the stack
            // stays intact
            xsw.writeStartElement(getXMLTableTag());
            xsw.writeAttribute(XMLConstants.LIST_INDEX_ATTR, "0");
            xsw.writeEndElement(); // end of table.
        }


        xsw.writeEndElement(); // end of stacks.
    }

    public void fromXML(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        fromXMLNEW(xer, XMLSerializationState);
    }

    public void fromXMLOLD(XMLEventReader xer, XMLSerializationState XMLSerializationState) throws XMLStreamException {
        // points to stacks tag
        XMLEvent xe = xer.nextEvent(); // moves off the stacks tag.
        getStack().clear(); // Needed or there will be an extra empty stack after this call.
        // no attributes or such with the stacks tag.
        while (xer.hasNext()) {
            xe = xer.peek();
            switch (xe.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    String localPart = xe.asStartElement().getName().getLocalPart();
                    if (localPart.equals(getXMLTableTag())) {
                        XTable xTable = newTableInstance();
                        xTable.fromXML(xer, XMLSerializationState);
                        push(xTable);
                    }

                    break;
                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(getXMLStackTag())) {
                        return;
                    }
                    break;
            }
            xe = xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + FUNCTION_TABLE_STACK_TAG);

    }

    abstract public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;

    abstract public String getXMLStackTag();

    /**
     * @deprecated 
     * @return
     */
    abstract public String getXMLTableTag();

    /**
     * Returns the <b><i>unique</i></b> set of keys over the tables.
     *
     * @return
     */
    public Set<XKey> keySet() {
        HashSet<XKey> uniqueKeys = new HashSet<>();
        for (XTable<? extends XKey, ? extends XThing> table : getStack()) {
            uniqueKeys.addAll(table.keySet());
        }
        return uniqueKeys;
    }

    /**
     * Returns the a list of keys (including redundancies) for this stack.
     *
     * @return
     */
    public List<XKey> allKeys() {
        ArrayList arrayList = new ArrayList();
        for (XTable<? extends XKey, ? extends XThing> table : getStack()) {
            arrayList.addAll(table.keySet());
        }
        return arrayList;
    }

    public JSONArray toJSON(XMLSerializationState xmlSerializationState) {
        JSONArray array = new JSONArray();
        for (XTable xTable : getStack()) {
            JSONArray jsonArray = new JSONArray();
            for (Object key : xTable.keySet()) {
                XThing xThing = (XThing) xTable.get(key);
                jsonArray.add(xTable.toJSONEntry(xThing, xmlSerializationState));
            }
            array.add(jsonArray);
        }
        return array;
    }

    /**
     * This sets the stack corresponding to this class from the state with the given stack.
     * If the stack is not of the correct type, a class cast exception will result. <br/><br/>
     * We <i>could</i> have tried this with some type of dynamic casting, but that is messy and fragile in Java
     *
     * @param state
     * @param xStack
     */
    public abstract void setStateStack(State state, XStack xStack);

    /**
     * This gets the stack corresponding to this class from the state..
     *
     * @param state
     * @return
     */

    public abstract XStack getStateStack(State state);

    public void fromJSON(JSONArray array, XMLSerializationState xmlSerializationState) {
        // To recreate the various states, we still need to use the parser and essentially
        // create local state repeatedly. The aim is to be as faithful as possible to
        // recreating the serialized stack.
        getStack().clear();
        XStack scratch = newInstance();
        State state = new State();
        setStateStack(state, scratch);

        QDLInterpreter qi = new QDLInterpreter(state);

        for (int i = 0; i < array.size(); i++) {
            // these were put in in reverse order, so have to pop them out.
            scratch.getStack().clear();
            XTable currentST = newTableInstance();
            scratch.push(currentST);

            JSONArray jsonArray = array.getJSONArray(i);
            for (int k = 0; k < jsonArray.size(); k++) {
                try {
                    qi.execute(currentST.fromJSONEntry(jsonArray.getString(k), xmlSerializationState));
                } catch (Throwable e) {
                    // For now
                    e.printStackTrace();
                }
            }
            // since this is the stack in the interpreter state, this pushes a new table there
            getStack().add(currentST);
        }

        return;
    }

    protected void fromXMLNEW(XMLEventReader xer, XMLSerializationState xmlSerializationState) throws XMLStreamException {
        // points to stacks tag
        JSONArray jsonArray = getJSON(xer);
        fromJSON(jsonArray, xmlSerializationState);
        //XMLEvent xe = xer.nextEvent(); // moves off the stacks tag.
        // no attributes or such with the stacks tag.
        while (xer.hasNext()) {
            XMLEvent xe = xer.peek();
            switch (xe.getEventType()) {

                case XMLEvent.END_ELEMENT:
                    if (xe.asEndElement().getName().getLocalPart().equals(getXMLStackTag())) {
                        if (getStack().isEmpty()) {
                            // Just make sure there is at least one.
                            getStack().add(newTableInstance());
                        }
                        return;
                    }
                    break;
            }
            xer.nextEvent();
        }
        throw new IllegalStateException("Error: XML file corrupt. No end tag for " + getXMLStackTag());
    }

    protected JSONArray getJSON(XMLEventReader xer) throws XMLStreamException {
        JSONArray jsonArray = null;
        XMLEvent xe = xer.nextEvent();
        while (xer.hasNext()) {
            switch (xe.getEventType()) {
                case XMLEvent.CHARACTERS:
                    if (xe.asCharacters().isWhiteSpace()) {
                        break;
                    }
                    jsonArray = JSONArray.fromObject(xe.asCharacters().getData());
                    return jsonArray;
            }
            xe = xer.nextEvent();
        }
        throw new XMLMissingCloseTagException(VARIABLE_STACK);
    }

    protected void toXMLNEW(XMLStreamWriter xsw, XMLSerializationState xmlSerializationState) throws XMLStreamException {
        xsw.writeStartElement(getXMLStackTag());
        xsw.writeCData(toJSON(xmlSerializationState).toString());
        xsw.writeEndElement();
    }

    public static void main(String[] args) throws Throwable {
        // Roundtrip test for JSON serialization. Should populate a stack, print it out, deserialize it, then
        // print out the exact same stack.
        testFStack();
    }

    static void testFStack() {
        FStack fStack = new FStack();
        fStack.put(new FunctionRecord(new FKey("f", 1), Arrays.asList("f(x)->x^2;")));
        fStack.put(new FunctionRecord(new FKey("g", 1), Arrays.asList("g(x)->x^3;")));
        fStack.put(new FunctionRecord(new FKey("h", 2), Arrays.asList("h(x,y)->x*y;")));
        fStack.pushNewTable();
        fStack.localPut(new FunctionRecord(new FKey("f", 1), Arrays.asList("f(x)->x^4;")));
        fStack.localPut(new FunctionRecord(new FKey("h", 1), Arrays.asList("h(x)->x+1;")));
        fStack.pushNewTable();
        JSONArray serialized = fStack.toJSON(null);
        System.out.println(serialized.toString(2));

        FStack fStack1 = new FStack();
        fStack1.fromJSON(serialized, null);
        System.out.println(fStack1.toJSON(null).toString(2));

    }

    static void testVStack() {
        // Roundtrip test for JSON serialization. Should populate a stack, print it out, deserialize it, then
        // print out the exact same stack.
        VStack vStack = new VStack();
        vStack.pushNewTable();
        vStack.put(new VThing(new XKey("a"), Boolean.TRUE));
        vStack.put(new VThing(new XKey("b"), "foo"));
        vStack.put(new VThing(new XKey("x"), new BigDecimal("123.456789")));
        StemVariable s = new StemVariable();
        s.put("a", 5L);
        s.put("foo", "bar");
        vStack.put(new VThing(new XKey("s."), s));
        vStack.pushNewTable();
        vStack.localPut(new VThing(new XKey("a"), Boolean.FALSE));
        vStack.localPut(new VThing(new XKey("x"), new BigDecimal("9.87654321")));
        vStack.pushNewTable();

        // One reason to use VStack here is that the xmlSerializationState can be null, since it is not used
        System.out.println(vStack.toJSON(null).toString(2));
        JSONArray serialized = vStack.toJSON(null);
        VStack vStack1 = new VStack();
        vStack1.fromJSON(serialized, null);
        System.out.println(vStack1.toJSON(null).toString(2));

    }
}
