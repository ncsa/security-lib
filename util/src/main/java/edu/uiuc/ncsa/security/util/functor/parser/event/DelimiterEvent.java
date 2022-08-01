package edu.uiuc.ncsa.security.util.functor.parser.event;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:25 AM
 */
public class DelimiterEvent extends ParserEvent {
    public DelimiterEvent(EventDrivenParser source, String name, char delimiter) {
        super(source);
        this.name = name;
        this.delimiter = delimiter;
    }

    public String getName() {
        return name;
    }

    String name;

    public char getDelimiter() {
        return delimiter;
    }

    char delimiter;
}
