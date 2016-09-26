package edu.uiuc.ncsa.security.core.util;


import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A pool of items, that is to say, a managed list that keeps valid items
 * on it and can create or destroy them as needed. when pushing and popping off a stack.
 * <p>Supremely useful with SQL connections.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  3:48:20 PM
 */
public abstract class Pool<T> {
    int INFINITY = -1;
    int MAX_SIZE = 1000;
    List<T> stack = new LinkedList<T>();
    static int inUse = 0;

    /**
     * Create a new one.
     *
     * @return
     */
    public abstract T create();

    /**
     * Destroy the given element.
     *
     * @param t
     */
    public abstract void destroy(T t);

    public Pool(int maximumSize) {
        this.MAX_SIZE = maximumSize;
    }

    public Pool() {
    }

    static int maxStackSize = 0;

    protected synchronized int maxStackSize() {
        if (maxStackSize < stack.size()) {
            maxStackSize = stack.size();
        }
        return maxStackSize;
    }

    static int stopValue = 0;

    public synchronized T pop() {
        T t = null;
        try {
            if (!stack.isEmpty()) {
                t = stack.remove(0);
            }
        } catch (IndexOutOfBoundsException | NoSuchElementException ix) {
            // do nothing.
        }
        if (t == null) {
            t = realCreate();
        }
        inUse++;
        return t;
    }

    protected synchronized T realCreate() {
        if (MAX_SIZE == INFINITY || inUse < MAX_SIZE) {
            T t = create();
            maxStackSize();
            return t;
        }
        throw new PoolException("Error: Maximum capacity of " + MAX_SIZE + " elements has been exceeded");
    }

    public synchronized void realDestroy(T t) {
        destroy(t);
        inUse--;

    }

    public synchronized void push(T t) {
        if (MAX_SIZE <= stack.size()) {
            realDestroy(t);
            return;
        }
        if (stack.contains(t)) {
            throw new PoolException("Error: This element has already been checked into the pool");
        }
        stack.add(0, t);
        inUse--;

    }


    /**
     * Remove and destroy all elements on stack.
     */
    public synchronized boolean clear() {
        boolean ok = true;
        while (!stack.isEmpty()) {
            try {
                destroy(pop());
                inUse--;
            } catch (Exception x) {
                ok = false;
                // rock on.
            }
        }
        return ok;
    }
}
