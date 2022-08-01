package edu.uiuc.ncsa.security.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The current state for the presentable interface. This passes the current request and response,
 * in addition this has only one required property and that is an
 * integer that gives the current implementor-defined state. Implement this to pass along anything else needed
 * to manage the state for your application.
 * <p>Created by Jeff Gaynor<br>
 * on 10/25/11 at  10:35 AM
 */
public interface PresentableState {
    int getState();
    HttpServletRequest getRequest();
    HttpServletResponse getResponse();
}
