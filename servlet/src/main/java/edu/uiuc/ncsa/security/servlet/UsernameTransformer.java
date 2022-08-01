package edu.uiuc.ncsa.security.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/9/14 at  10:14 AM
 */
public interface UsernameTransformer {
    /**
     * This will take the request. The result returned
     * will be a replacement for the username that is passed to MyProxy for authentication.<br/>
     * @param request
     * @return
     */
    public String createMyProxyUsername(HttpServletRequest request);

    /**
     * This will take the request and current myproxy username, The result is returned to the
     * client as the "username" parameter.
     * @param request
     * @param myproxyUsername
     * @return
     */
    public String createReturnedUsername(HttpServletRequest request, String myproxyUsername);

}
