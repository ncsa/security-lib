package edu.uiuc.ncsa.security.util.functor.parser.event;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:37 AM
 */
public class CommaEvent extends ParserEvent {
    public CommaEvent(EventDrivenParser source, String text) {
        super(source);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    String text;


}
