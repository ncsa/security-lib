package edu.uiuc.ncsa.security.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A class that holds the information for the exception handler
 * <p>Created by Jeff Gaynor<br>
 * on 4/20/23 at  12:58 PM
 */
public class ExceptionHandlerThingie {
    // Fixes https://github.com/ncsa/security-lib/issues/16
    public ExceptionHandlerThingie(Throwable throwable, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.throwable = throwable;
    }

    public HttpServletRequest request;
    public HttpServletResponse response;
    public Throwable throwable;
}
