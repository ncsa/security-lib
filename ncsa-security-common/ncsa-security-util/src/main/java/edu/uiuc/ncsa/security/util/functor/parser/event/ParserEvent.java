package edu.uiuc.ncsa.security.util.functor.parser.event;

import java.util.EventObject;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  12:02 PM
 */
public class ParserEvent extends EventObject {
    public ParserEvent(EventDrivenParser source) {
        super(source);
    }
    EventDrivenParser getParser(){
        return (EventDrivenParser)getSource();
    }
}
