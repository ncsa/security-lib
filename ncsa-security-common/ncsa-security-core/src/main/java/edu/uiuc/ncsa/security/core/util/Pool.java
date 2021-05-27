package edu.uiuc.ncsa.security.core.util;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A pool of items, that is to say, a managed list that keeps valid items
 * on it and can create or destroy them as needed. when pushing and popping off a stack.
 * <p>Supremely useful with SQL connections. This does check for validity as a matter
 * of course.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 12, 2010 at  3:48:20 PM
 */
public abstract class Pool<T> {
    public static final int INFINITE = -1;
    int maxSize = INFINITE;
    protected int totalCreated = 0;
    protected int totalDestroyed = 0;
    protected int inUse = 0;
    /**
     * Set true if you want to see a ton of low level debugging for this.
     */
    protected boolean DEEP_DEBUG = false;

    protected void trace(String x) {
        DebugUtil.trace(DEEP_DEBUG, this, x);
    }

    public Queue<T> getStack() {
        return stack;
    }

    protected Queue<T> stack = new LinkedList<T>();

    /**
     * Create a new, ready-to-use object for the pool
     *
     * @return the object
     */
    public abstract T create() throws PoolException;

    /**
     * Destroy an object that is no longer needed.
     *
     * @param object the object
     */
    public abstract void destroy(T object) throws PoolException;

    /**
     * Is this item still good? If not it will be removed from the
     * pool so a new one can be created. Default is to return true.
     */
    public boolean isValid(T object) throws PoolException {
        return true;
    }

    /**
     * Pop an object off the stack if there is one, otherwise, create one.
     *
     * @return the object
     */
    public synchronized T pop() throws PoolException {
        try {

            T item = getStack().poll();
            if (item == null || !isValid(item)) {
                trace("pop: creating new item");
                return doCreate();
            }
            inUse++;
            trace("pop: in use = " + inUse);
            return item;
        } catch (IndexOutOfBoundsException x) { // pool is empty
            return doCreate();
        } catch (NoSuchElementException x) { // pool is empty
            return doCreate();
        }
    }

    public synchronized T doCreate() throws PoolException {
        // this is only called if getStack().size()==0
        if (maxSize == INFINITE || inUse < maxSize) {
            T item = create();
            inUse++;
            return item;
        } else {
            throw new PoolException("pool at capacity: " + inUse + " item(s) checked out. " + getStack().size() + " items in getStack().");
        }
    }

    /**
     * Destroying an item reduces the number of in-use items.
     * Do not call this on an item that hasn't been checked out.
     *
     * @param item the item
     * @throws PoolException
     */
    public synchronized void doDestroy(T item) throws PoolException {
        destroy(item);
        inUse--;
        trace("doDestroy, in use=" + inUse);
    }


    /**
     * Set the maximum number of items.
     *
     * @param c
     */
    public void setMaxSize(int c) {
        maxSize = c;
    }

    /**
     * Get the maximum number of items
     *
     * @return capacity
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Check an object into the pool. If the pool is at capacity,
     * destroy the object.
     *
     * @param object the object
     */
    public synchronized void push(T object) throws PoolException {
        trace("push: ");
        if ((getMaxSize() != INFINITE && getStack().size() >= getMaxSize()) || !isValid(object)) {
            doDestroy(object);
        } else if (getStack().contains(object)) {
            throw new PoolException("can't check in object more than once: " + object);
        } else {
            getStack().add(object);
            inUse--;
        }
    }

    @Override
    public String toString() {
        return "Pool{" +
                "maxSize=" + maxSize +
                ", inUse=" + inUse +
                ", total created=" + totalCreated +
                ", total destroyed=" + totalDestroyed +
                ", size=" + getStack().size() +
                ", stack=" + stack +
                '}';
    }
}

