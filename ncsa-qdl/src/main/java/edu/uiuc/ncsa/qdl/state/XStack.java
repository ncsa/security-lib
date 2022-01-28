package edu.uiuc.ncsa.qdl.state;

import edu.uiuc.ncsa.qdl.parsing.QDLInterpreter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.List;

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
     * Take an XStack and add all of the tables in this stack in the correct order.
     * This is needed when, e.g., creating new local state for function reference resolution
     *
     * @param xStack
     */
    public void addTables(XStack xStack) {
        // add backwards
        for (int i = xStack.getStack().size() - 1; 0 <= i; i--) {
            push((XTable) xStack.getStack().get(i));
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

    public XTable<? extends XKey, ? extends XThing> peek() {
        return getStack().get(0);
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
     * any comments, invoke this, then the end tag. See {@link edu.uiuc.ncsa.qdl.functions.FStack#toXML(XMLStreamWriter)}
     * for a canonical example.
     * @param xsw
     * @throws XMLStreamException
     */
     public void toXML(XMLStreamWriter xsw) throws XMLStreamException{
         // lay these in in reverse order so we just have to read them in the fromXML method
         // and push them on the stack
         for (int i = getStack().size() - 1; 0 <= i; i--) {
             getStack().get(i).toXML(xsw);
         }
     }

    abstract public void fromXML(XMLEventReader xer, QDLInterpreter qi) throws XMLStreamException;

}
