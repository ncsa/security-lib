package edu.uiuc.ncsa.security.core.exceptions;

/**
 * For cases where a server throws an exception internally but returns a redirect. This can be created to
 * throw an more controlled exception so a client can recover the control flow. Most often used with
 * bad OAuth 1.0a exception behavior, for instance.
 * <p>Created by Jeff Gaynor<br>
 * on 6/27/13 at  4:12 PM
 */
public class ServerRedirectException extends GeneralException {
    public ServerRedirectException() {
    }

    public ServerRedirectException(Throwable cause) {
        super(cause);
    }

    public ServerRedirectException(String message) {
        super(message);
    }

    public ServerRedirectException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getWebpage() {
        return webpage;
    }

    public void setWebpage(String webpage) {
        this.webpage = webpage;
    }

    String webpage;
}
