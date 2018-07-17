package edu.uiuc.ncsa.security.util.functor.parser.old;

import edu.uiuc.ncsa.security.util.functor.parser.old.TopHandlerInterface;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  10:40 AM
 */
public interface FunctorHandlerInterface extends TopHandlerInterface {
    void startParenthesis(String name);

    void foundDoubleQuotes(String content);

    void endParenthesis(String name);

}
