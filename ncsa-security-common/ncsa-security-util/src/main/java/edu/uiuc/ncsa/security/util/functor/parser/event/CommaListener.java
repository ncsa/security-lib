package edu.uiuc.ncsa.security.util.functor.parser.event;

import java.util.EventListener;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:38 AM
 */
public interface CommaListener extends EventListener {
    public void gotComma(CommaEvent commaEvent);
}
