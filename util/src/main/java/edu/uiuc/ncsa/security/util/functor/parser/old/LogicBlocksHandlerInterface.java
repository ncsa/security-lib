package edu.uiuc.ncsa.security.util.functor.parser.old;

import edu.uiuc.ncsa.security.util.functor.parser.old.TopHandlerInterface;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  10:39 AM
 */
public interface LogicBlocksHandlerInterface extends TopHandlerInterface {
    void startBrace(String name);

    void endBrace(String name);
}
