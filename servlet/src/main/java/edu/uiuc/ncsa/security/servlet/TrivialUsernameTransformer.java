package edu.uiuc.ncsa.security.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * This transformation is trivial, <i>i.e.</i> it returns a null
 * myproxy username and the returned username is simply the passed-in.
 * This is the default on the server unless you specifically override
 * this class/implement your own {@link UsernameTransformer} class and set it in the
 * service environment.
 * <p>Created by Jeff Gaynor<br>
 * on 1/9/14 at  10:17 AM
 */
public class TrivialUsernameTransformer implements UsernameTransformer {
    @Override
    public String createMyProxyUsername(HttpServletRequest request) {
        return null;
    }

    @Override
    public String createReturnedUsername(HttpServletRequest request, String myproxyUsername) {
        return myproxyUsername;
    }
}
