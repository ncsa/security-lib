package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/15 at  4:29 PM
 */
public interface ExceptionHandler {

    public MyLoggingFacade getLogger();
    /**
     * This has to throw these two exceptions for servlets.
     * @param exceptionHandlerThingie
     * @throws IOException
     * @throws ServletException
     */
    public void handleException(ExceptionHandlerThingie exceptionHandlerThingie) throws IOException, ServletException;
}
