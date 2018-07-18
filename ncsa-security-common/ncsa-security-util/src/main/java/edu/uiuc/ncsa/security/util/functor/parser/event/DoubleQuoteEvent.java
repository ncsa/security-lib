package edu.uiuc.ncsa.security.util.functor.parser.event;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:37 AM
 */
public class DoubleQuoteEvent extends ParserEvent {
    public String getCharacters() {
        return characters;
    }

    protected String characters;

    public DoubleQuoteEvent(EventDrivenParser source, String characters) {
        super(source);
        this.characters = characters;
    }

}
