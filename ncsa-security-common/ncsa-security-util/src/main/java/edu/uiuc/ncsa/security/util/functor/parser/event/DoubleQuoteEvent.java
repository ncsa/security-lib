package edu.uiuc.ncsa.security.util.functor.parser.event;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 7/16/18 at  11:37 AM
 */
public class DoubleQuoteEvent extends ParserEvent {
    public String getCharaceters() {
        return characeters;
    }

    protected String characeters;

    public DoubleQuoteEvent(EventDrivenParser source, String characeters) {
        super(source);
        this.characeters = characeters;
    }

}
