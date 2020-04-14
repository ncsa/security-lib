package edu.uiuc.ncsa.security.util.functor;

import java.io.Serializable;

/**
 * This can run some sort of a script/procedure and has a result that is accessible.
 * <p>Created by Jeff Gaynor<br>
 * on 3/11/19 at  10:42 AM
 */
public interface JMetaMetaFunctor extends Serializable {
    Object execute();
    Object getResult();

}
