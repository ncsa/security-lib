package edu.uiuc.ncsa.security.util.functor.parser.event;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:25 AM
 */
public class DelimiterEvent extends ParserEvent {
    public DelimiterEvent(EventDrivenParser source, String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    String name;
}
