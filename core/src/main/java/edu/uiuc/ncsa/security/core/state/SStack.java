package edu.uiuc.ncsa.security.core.state;

import edu.uiuc.ncsa.security.core.configuration.EnvEntry;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;

import java.io.Serializable;
import java.util.*;

/**
 * A stateful stack of things, such as functions. This is the method by which local state
 * is preserved. The zero-th element is the current local table. It is used for entries.,
 * hence the prefix of <b>S</b> for it and things related to it.
 * <h3>Usage</h3>
 * <p>Create a subclass as needed for your objects. This involves an {@link SStack},
 * {@link STable}, {@link SKey} and an {@link SThing}.</p>
 * <h3>How state is managed</h3>
 * If we had the following QDL:
 * <pre>
 *     f(x)-&gt;x^2;
 *     block[f(x)-&gt;x^3;...]
 * </pre>
 * Then {@link SStack} subclass for functions inside the block would look like
 * <pre>
 *     table       entry
 *     0           f(x)-&gt;x^3
 *     1           f(x)-&gt;x^2
 * </pre>
 * Calls to  {@link SStack#get(SKey)} would peek at 0 and return f(x)-&gt;x^3
 * inside the block. This is how local state overrides the parent state.
 * Blocks of course can be for loops, functions, conditionals etc. Were there no
 * entry for f(x) in the block, then {@link SStack} would return f(x)-&gt;x^2.
 *
 * <p>Created by Jeff Gaynor<br>
 * on 11/8/21 at  6:27 AM
 */
public abstract class SStack<V extends STable<? extends SKey, ? extends SThing>> implements Serializable {
    /**
     * Clears the entire stack and resets it.
     */
    public void clear() {
        getStack().clear();
        pushNewTable();
    }

    /**
     * Take an SStack and prepend in the correct order
     * to the front of the stack. If SStack is [A,B,C,...] And the existing stack is
     * [P,Q,...] the result is [A,B,C,...,P,Q,...]
     * This is needed when, e.g., creating new local state for function reference resolution
     * <br/><br/>
     * <b>Note:</b> {@link #get(SKey)} starts from index 0, so local overrides are first!
     *
     * @param sStack
     */
    public void addTables(SStack sStack) {
        // add backwards from stack
        for (int i = sStack.getStack().size() - 1; 0 <= i; i--) {
            STable xt = (STable) sStack.getStack().get(i);
            if (addedTables.contains(xt.getID())) {
                continue;
            }
            if (!xt.isEmpty()) {
                push((STable) sStack.getStack().get(i)); // puts at 0th elements each time
                addedTables.add(xt.getID());
            }
        }
    }

    Set<UUID> addedTables = new HashSet<>();

    /**
     * Similar to {@link #addTables(SStack)}, but this appends them to the existing
     * set of tables. If SStack is [A,B,C,...] And the existing stack is
     * [P,Q,...] the result is [P,Q,...,A,B,C,...,]
     * <br/><br/>
     * <b>Note:</b> {@link #get(SKey)} starts from index 0, so local overrides are first!
     *
     * @param sStack
     */
    public void appendTables(SStack sStack) {
        //for (int i = SStack.getStack().size() - 1; 0 <= i; i--) {
        for (int i = 0; i < sStack.getStack().size(); i++) {
            STable xt = (STable) sStack.getStack().get(i);
            if (addedTables.contains(xt.getID())) {
                continue;
            }
            if (!xt.isEmpty()) {
                append((V) sStack.getStack().get(i)); // puts at 0th elements each time
                addedTables.add(xt.getID());
            }
        }
    }

    abstract public SStack newInstance();

    abstract public STable newTableInstance();

    /**
     * Append the table to the end of the stack -- this sets the root for the table.
     *
     * @param v
     */
    public void append(V v) {
        getStack().add(v);
    }

    @Override
    public SStack clone() {
        SStack cloned = newInstance();
        for (STable ft : getStack()) {
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
    public boolean containsKey(SKey key, int startTableIndex) {
        for (int i = startTableIndex; i < getStack().size(); i++) {
            if (getStack().get(i).containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsKey(SKey key) {
        return containsKey(key, 0);
    }

    /**
     * Only returns a non-null element if it is defined in the local (index 0) table.
     *
     * @param key
     * @return
     */
    public SThing localGet(SKey key) {
        if (getStack().isEmpty()) {
            return null;
        }
        return getLocal().get(key);
    }

    /**
     * Get the local table for this stack.
     *
     * @return
     */
    public STable<? extends SKey, ? extends SThing> getLocal() {
        return getStack().get(0);
    }

    public boolean localHas(SKey xkey) {
        if (getStack().isEmpty()) {
            return false;
        }
        return getLocal().get(xkey) != null;

    }

    /**
     * Get the value from someplace in the stack. This returns first found so manages the
     * overrides in the scope. Note that local calls (e.g. {@link #localGet(SKey)} only
     * look in the current scope.
     *
     * @param key
     * @return
     */
    public SThing get(SKey key) {
        for (STable<? extends SKey, ? extends SThing> sTable : getStack()) {
            SThing xThing = sTable.get(key);
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
    public SThing nonlocalGet(SKey key) {
        for (int i = 1; i < getStack().size(); i++) {
            SThing xThing = getStack().get(i).get(key);
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
    public List<? extends SThing> getAll() {
        List<? extends SThing> list = new ArrayList<>();
        for (STable sTable : getStack()) {
            list.addAll(sTable.values());
        }
        return list;
    }

    /**
     * Since all new tables are added at 0, the initial one, called the root, is last. This gets
     * the root {@link STable}.
     *
     * @return
     */
    public STable<? extends SKey, ? extends SThing> getRoot() {
        return getStack().get(getStack().size() - 1);
    }

    public List<STable<? extends SKey, ? extends SThing>> getStack() {
        if (stack == null) {
            stack = new ArrayList();
        }
        return stack;
    }

    public void setStack(List<STable<? extends SKey, ? extends SThing>> stack) {
        this.stack = stack;
    }

    List<STable<? extends SKey, ? extends SThing>> stack = new ArrayList<>();

    public boolean isEmpty() {
        boolean empty = true;
        for (STable<? extends SKey, ? extends SThing> sTable : getStack()) {
            empty = empty && sTable.isEmpty();
        }
        return empty;
    }

    public STable<SKey, SThing> peek() {
        return (STable<SKey, SThing>) getStack().get(0);
    }

    public void push(STable<? extends SKey, ? extends SThing> sTable) {
        getStack().add(0, sTable);
    }

    public void pushNewTable() {
        push(newTableInstance());
    }

    /**
     * Remove the most local table.
     */
    public STable<SKey, SThing> pop() {
        STable<SKey, SThing> table = peek();
        getStack().remove(0);
        return table;
    }

    /**
     * Only add this to the local state.
     *
     * @param value
     * @return
     */
    public SThing localPut(SThing value) {
        SThing oldValue = getStack().get(0).get(value);
        getStack().get(0).put(value);
        return oldValue;
    }

    public SThing put(SKey sKey, SThing xThing) {
        for (STable<? extends SKey, ? extends SThing> sTable : getStack()) {
            if (sTable.containsKey(sKey)) {
                sTable.put(xThing);
                return xThing;
            }
        }
        return peek().put(sKey, xThing);
    }

    public SThing put(SThing value) {
        for (STable<? extends SKey, ? extends SThing> sTable : getStack()) {
            if (sTable.containsKey(value.getKey())) {
                sTable.put(value);
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
    public void localRemove(SKey key) {
        peek().remove(key);
    }

    /**
     * Removes <i>all</i> references from all tables. This includes all overrides
     * so at the end of this operation there are no references any place.
     *
     * @param key
     */
    public void remove(SKey key) {
        for (STable<? extends SKey, ? extends SThing> sTable : getStack()) {
            sTable.remove(key);
            addedTables.remove(sTable.getID());
        }
    }

    public int size() {
        int totalSymbols = 0;
        for (STable sTable : getStack()) {
            totalSymbols += sTable.size();
        }
        return totalSymbols;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean showKeys) {
        String out = "[" + getClass().getSimpleName();
        out = out + ", table#=" + getStack().size();
        int i = 0;
        int totalSymbols = 0;
        boolean isFirst = true;
        String keys = "keys=[";
        out = ", counts=[";
        for (STable sTable : getStack()) {
            if (isFirst) {
                isFirst = false;
                out = out + sTable.size();
                keys = keys + sTable.keySet();
            } else {
                out = out + "," + sTable.size();
                keys = keys + "," + sTable.keySet();
            }
            totalSymbols += sTable.size();
        }
        out = out + "], total=" + totalSymbols;
        keys = keys + "]";
        out = out + "]";
        if (showKeys) {
            out = out + "," + keys;
        }
        return out;
    }


    /**
     * Returns the <b><i>unique</i></b> set of keys over the tables.
     *
     * @return
     */
    public Set<SKey> keySet() {
        HashSet<SKey> uniqueKeys = new HashSet<>();
        for (STable<? extends SKey, ? extends SThing> table : getStack()) {
            uniqueKeys.addAll(table.keySet());
        }
        return uniqueKeys;
    }

    /**
     * Returns the a list of keys (including redundancies) for this stack.
     *
     * @return
     */
    public List<SKey> allKeys() {
        ArrayList arrayList = new ArrayList();
        for (STable<? extends SKey, ? extends SThing> table : getStack()) {
            arrayList.addAll(table.keySet());
        }
        return arrayList;
    }

    /**
     * Get a <b>read only</b> virtualization of this stack as a map.
     *
     * @return
     */
    public Map<String, String> getAsMap() {
        return new ROMap(this);

    }

    /**
     * A read-only map that virtualizes this stack. Most things don't work, just
     * iterators and gets.
     */
    public static class ROMap implements Map<String, String> {
        public ROMap(SStack sStack) {
            this.sStack = sStack;
        }

        SStack sStack;

        @Override
        public int size() {
            return sStack.size();
        }

        @Override
        public boolean isEmpty() {
            return sStack.isEmpty();
        }

        @Override
        public boolean containsKey(Object o) {
            if (o instanceof String) {
                return sStack.containsKey(new SKey((String) o));
            }
            return false;
        }

        @Override
        public boolean containsValue(Object o) {
            throw new NotImplementedException();
        }

        @Override
        public String get(Object o) {
            if (o instanceof String) {
                EnvEntry entry = (EnvEntry) sStack.get(new SKey((String) o));
                if (entry == null) {
                    return null;
                }
                return entry.getValue();
            }
            return null;
        }

        @Override
        public String put(String s, String s2) {
            throw new NotImplementedException();
        }

        @Override
        public String remove(Object o) {
            throw new NotImplementedException();
        }

        @Override
        public void putAll(Map<? extends String, ? extends String> map) {
            throw new NotImplementedException();
        }

        @Override
        public void clear() {
            throw new NotImplementedException();
        }

        HashSet<String> set = null;

        @Override
        public Set<String> keySet() {
            if (set == null) {
                set = new HashSet<>();
                for (Object ooo : sStack.keySet()) {
                    SKey key = (SKey) ooo;
                    set.add(key.getKey());
                }
            }
            return set;
        }

        @Override
        public Collection<String> values() {
            throw new NotImplementedException();
        }

        @Override
        public Set<Entry<String, String>> entrySet() {
            throw new NotImplementedException();
        }
    }
}
