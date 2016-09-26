package edu.uiuc.ncsa.security.core.util;

import java.util.LinkedList;

/**
 * A Queue with a "spare tire". That is to say, the last entry returned by this queue is always stored in
 * the spare. It is assumed that the queue is populated by another thread which might be lagging for
 * expensive operations (such as key pair generation), so this is a queue that always has an element.
 *
 * <h2>Note about this and a Java {@link java.util.Queue}</h2>
 * The reason that this is a separate class from the java Queue is that the semantics are sufficiently different
 * to invalidate its use: Since this queue always has an element, methods that return an exception when the queue
 * is empty do not operate as expected. Moreover, having Queue inherit from Collection means there are many
 * methods which must be over-ridden and do nothing but throw exceptions to make the semantics consistent with
 * the contract of this class. Therefore, this is an independent class that inherits from nothing.
 * <p>Created by Jeff Gaynor<br>
 * on 2/20/12 at  9:56 AM
 */
public class QueueWithSpare<E>  {

    public E getSpare() {
        return spare;
    }

    public void setSpare(E spare) {
        this.spare = spare;
    }

    E spare;

    LinkedList<E> linkedList = new LinkedList<E>();

    public boolean push(E e) {
        return linkedList.add(e);
    }

    public E pop() {
        E x = linkedList.poll();
        if(x == null){
            return spare;
        }
        spare = x;
        return x;
    }

    public E remove() {
        return linkedList.remove();
    }


    public int size() {
        return linkedList.size();
    }

    /**
     * Slightly different than expected: This return true if there are no elements in the
     * queue proper AND the spare is null. Since the spare is not included in the calculation
     * of the number of elements in the queue, use {@link #size()} == 0 to check if there
     * are no elements in this queue.
     * @return
     */
    public boolean isEmpty() {
        return linkedList.isEmpty() && spare == null;
    }

    public void clear(){
        linkedList = new LinkedList<E>();
        spare = null;
    }
}
