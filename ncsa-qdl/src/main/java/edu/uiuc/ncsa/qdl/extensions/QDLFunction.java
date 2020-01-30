package edu.uiuc.ncsa.qdl.extensions;

import java.io.Serializable;

/**
 * A wrapper for a single Java method that can be invoked from QDL. It may indeed be a single utility, or it may
 * front an entire class and expose the methods in it (so you {@link #getInstance()}  call just hands this class
 * back. You could, for instance, have a single Java object with seceral methods, each of which holds a reference to the object
 * and invokes a method on it. There are may possibilities.
 * <p>Created by Jeff Gaynor<br>
 * on 1/27/20 at  12:02 PM
 */
public interface QDLFunction extends Serializable {
    /**
     * The name of this function as you want it invoked in QDL.
     * @return
     */
    public String getName();

    /**
     * The contract is that when QDL invokes this method, it will faithfully give all of the arguments
     * as an array of Objects. Overloading is not possible in QDL (it is weakly typed)  except by argument count, so if this is
     * called "foo" and you have versions with 3 and 4 arguments, then f(a,b,c) would be executed with the
     * arguments passed.
     *
     * @return
     */
    public int getArgCount();

    /**
     * The method that is invoked by QDL. It will have the arguments evaluated and put in to the
     * array of objects. It is up to you to do any checking you see fit.
     * @param objects
     * @return
     */
    public Object evaluate(Object[] objects);

    /**
     * This passes along the instance of this that you want to evaluate. This may mean just creating a new instance
     * and handing it back, or it may just pass a reference to this class. The former pattern is used if there is
     * to be no state preserved between calls. The latter is used if this class needs to preserve some sort of
     * state, in which case it is up to you to communicate to any users that this is stateful.
     * @return
     */
    public QDLFunction getInstance();
}
