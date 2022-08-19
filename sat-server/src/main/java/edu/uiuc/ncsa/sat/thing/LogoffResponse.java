package edu.uiuc.ncsa.sat.thing;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  7:25 AM
 */
public class LogoffResponse extends Response{
    public LogoffResponse(String message) {
        this.message = message;
    }

    public LogoffResponse() {
    }

    // nothing really.
    public String message;
}
