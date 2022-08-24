package edu.uiuc.ncsa.sat.thing;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:28 AM
 */
public class OutputResponse extends Response{
    public OutputResponse(Action action, String content) {
        super(action);
        this.content = content;
    }

    public String content;
}
