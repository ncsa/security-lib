package edu.uiuc.ncsa.sat.loader;

import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.sat.SessionRecord;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.servlet.ExceptionHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  4:02 PM
 */
public class SATExceptionHandler implements ExceptionHandler {
    MyLoggingFacade logger;
    @Override
    public MyLoggingFacade getLogger() {
             return logger;
    }

    public SATExceptionHandler(SATEnvironment sate) {
        this.sate = sate;
        this.logger = sate.getMyLogger();
    }

    SATEnvironment sate;
    @Override
    public void handleException(Throwable t, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
           t.printStackTrace();
    }
    public void handleException(Throwable t,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                SessionRecord sessionRecord) throws IOException, ServletException {
        sate.getResponseSerializer().serialize(t, response,sessionRecord);
    }

}
