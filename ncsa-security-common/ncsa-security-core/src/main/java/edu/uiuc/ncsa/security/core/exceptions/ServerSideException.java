package edu.uiuc.ncsa.security.core.exceptions;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * An exception that wraps a server-side exception. OAuth 1.0a, for example
 * will intercept every response from the server and throw an exception if there
 * is anything other than a status of 200. Therefore, catch such exceptions
 * and throw this in their stead.  it is too easy to lose information the way
 * OAuth does it as every exception is an OAuthProblemException.
 * <H2>A Note on server side exceptions propagated to the client</H2>
 * OAuth 1.0a does support returning some information about server side
 * exceptions. Generally, OA4MP should not do this unless specifically
 * requested to do so by the client. The reason for this is that while
 * a mechanism is given by OAuth, the details (as expected) are lacking.
 * In this case, if a client sets the request parameter "oa4mp_debug=true"
 * in the initial call, then <B>IF</B> there is an exception on the server side
 * the result will be unpacked as follows:
 * <ul>
 * <li>A local ServerSideException will be thrown.</li>
 * <li>If the server supports it, a redirect to a server-side error page may be supplied in the redirect property</li>
 * <li>The server may return any number of key value pairs. Only one of these is guaranteed to exist, the stack trace.
 * This is returned as a string.</li>
 * </ul>
 * It is wholly up to the client on what to do at this point.
 *
 * Note that even if requested, this mechanism will only work if there is an actual exception on the server, otherwise
 * nothing will be returned to the client. The intent is to give developers who encounter a throny problem a way to
 * communicate more effectively with the OA4MP development staff, so this feature is of limited interest, at best.
 * <p>Created by Jeff Gaynor<br>
 * on 9/4/12 at  7:16 PM
 */
public class ServerSideException extends GeneralException {
    public ServerSideException() {
    }

    public ServerSideException(Throwable cause) {
        super(cause);
    }

    public ServerSideException(String message) {
        super(message);
    }

    public ServerSideException(String message, Throwable cause) {
        super(message, cause);
    }

    URI redirect;

    public URI getRedirect() {
        return redirect;
    }

    public void setRedirect(URI redirect) {
        this.redirect = redirect;
    }

    /**
     * If there is a redirect, any parameters passes as part of it will be parsed and
     * put in this map as key value pairs. This should let clients print out any other information
     * they may want/need about the error.
     *
     * @return
     */
    public Map<String, String> getQueryParameters() {
        if (queryParameters == null) {
            queryParameters = new HashMap<String, String>();
        }
        return queryParameters;
    }

    public void setQueryParameters(Map<String, String> queryParameters) {
        this.queryParameters = queryParameters;
    }

    Map<String, String> queryParameters;

    public boolean isTrivial(){
        return redirect == null && (queryParameters == null || queryParameters.isEmpty());
    }
}
