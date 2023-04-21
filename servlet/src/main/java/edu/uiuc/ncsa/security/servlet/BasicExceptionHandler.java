package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/15 at  4:32 PM
 */
public class BasicExceptionHandler implements ExceptionHandler {
    public BasicExceptionHandler(MyLoggingFacade logger) {
        this.logger = logger;
    }

    MyLoggingFacade logger;

    @Override
    public MyLoggingFacade getLogger() {
        return logger;
    }

    @Override
    public void handleException(ExceptionHandlerThingie exceptionHandlerThingie) throws IOException, ServletException {
        Throwable t = exceptionHandlerThingie.throwable;
        HttpServletRequest request = exceptionHandlerThingie.request;
        HttpServletResponse response = exceptionHandlerThingie.response;
        if (t instanceof NFWException) {
            // Make SURE this gets logged some place else too in case it's hard to find.
            DebugUtil.severe(this, "internal exception encountered", t);
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        if ((t instanceof IOException)) {
            throw (IOException) t;
        }
        if (t instanceof ServletException) {
            throw (ServletException) t;
        }
        // The default case...
        throw new RuntimeException(t);
    }
}
