package edu.uiuc.ncsa.security.util.functor.parser.event;

import java.util.EventListener;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:39 AM
 */
public interface DoubleQuoteListener extends EventListener {
    void gotText(DoubleQuoteEvent dqEvent);
}
