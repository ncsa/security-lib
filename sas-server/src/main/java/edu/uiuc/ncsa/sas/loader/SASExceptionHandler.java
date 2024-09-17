package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.sas.SASEnvironment;
import edu.uiuc.ncsa.sas.SASServlet;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.servlet.ExceptionHandler;
import edu.uiuc.ncsa.security.servlet.ExceptionHandlerThingie;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  4:02 PM
 */
public class SASExceptionHandler implements ExceptionHandler {
   transient   MyLoggingFacade logger;
    @Override
    public MyLoggingFacade getLogger() {
             return logger;
    }

    public SASExceptionHandler(SASEnvironment sate) {
        this.sate = sate;
        this.logger = sate.getMyLogger();
    }

    SASEnvironment sate;
    @Override
    public void handleException(ExceptionHandlerThingie exceptionHandlerThingie) throws IOException, ServletException {
        SASServlet.SASExceptionHandlerThingie sasXH = (SASServlet.SASExceptionHandlerThingie)exceptionHandlerThingie;
        if(sasXH.hasSessionRecord()){
            sate.getResponseSerializer().serialize(sasXH.throwable, sasXH.response, sasXH.sessionRecord);
        }else{
            exceptionHandlerThingie.throwable.printStackTrace();
        }
    }
}
