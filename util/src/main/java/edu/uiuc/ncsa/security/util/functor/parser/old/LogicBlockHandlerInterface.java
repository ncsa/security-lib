package edu.uiuc.ncsa.security.util.functor.parser.old;

import edu.uiuc.ncsa.security.util.functor.parser.old.TopHandlerInterface;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  10:40 AM
 */
public interface LogicBlockHandlerInterface extends TopHandlerInterface {
    void startBracket(String name);

      void endBracket(String name);

}
