package edu.uiuc.ncsa.security.util.functor.parser.old;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  10:38 AM
 */
public interface TopHandlerInterface {

    /**
     * Reset the state of this handler to before the first call.
     * This allows the handler to be re-used by other components.
     * be sure to get whatever this handler has accumulated before you call this method.
     *
     */
    void reset();

    void foundComma(String name);

}
