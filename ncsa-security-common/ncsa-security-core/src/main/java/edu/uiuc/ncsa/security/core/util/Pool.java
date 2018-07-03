package edu.uiuc.ncsa.security.core.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

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
    protected  int inUse = 0;
    List<T> stack = new LinkedList<T>();

    /**
     * Create a new, ready-to-use object for the pool
     * @return the object
     */
    public abstract T create() throws PoolException;

    /**
     * Destroy an object that is no longer needed.
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
     * Pop an object off the stack if ther eis one, otherwise, create one.
     * @return the object
     */
    public synchronized T pop() throws PoolException {
        try {
            T item = stack.remove(0);
            if(!isValid(item)){
                return doCreate();
            }
            inUse++;
            return item;
        } catch(IndexOutOfBoundsException x) { // pool is empty
            return doCreate();
        } catch(NoSuchElementException x) { // pool is empty
            return doCreate();
        }
    }

    public synchronized T doCreate() throws PoolException {
	// this is only called if stack.size()==0
	if(maxSize == INFINITE || inUse < maxSize) {
	    T item = create();
	    inUse++;
	    return item;
	} else {
	    throw new PoolException("pool at capacity: "+inUse+" item(s) checked out. " + stack.size() + " items in stack.");
	}
    }

    /**
     * Destroying an item reduces the number of in-use items.
     * Do not call this on an item that hasn't been checked out.
     * @param item the item
     * @throws PoolException
     */
    public synchronized void doDestroy(T item) throws PoolException {
	destroy(item);
	inUse--;
    }

    /**
     * Set the maximum number of items.
     * @param c
     */
    public void setMaxSize(int c) {
        maxSize = c;
    }
    /**
     * Get the maximum number of items
     * @return capacity
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Check an object into the pool. If the pool is at capacity,
     * destroy the object.
     * @param object the object
     */
    public synchronized void push(T object) throws PoolException {
        if((maxSize != INFINITE && stack.size() >= maxSize) || !isValid(object)) {
            doDestroy(object);
        } else if(stack.contains(object)) {
            throw new PoolException("can't check in object more than once: " + object);
        } else {
            stack.add(0, object);
            inUse--;
        }
    }

    public synchronized boolean destroyAll() {
        boolean success = true;
        Iterator<T> it = stack.iterator();
        while(it.hasNext()) {
            try {
                doDestroy(it.next());
            } catch(PoolException x) {
                success = false;
            }
            it.remove();
        }
        return success;
    }
}

