package edu.uiuc.ncsa.security.servlet;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/20/11 at  2:02 PM
 * <H3>A filter for servlets that display something.</H3>
 * <p>Servlets that present something to a user (e.g. those serving up JSP) implement this interface.
 * The contract is then that in the main doGet or doPost methods, prepare is invoked before the start of every
 * call and present at the very end. Generally servlets should not touch the response output stream
 * before handing it off, though be sure to consult any documentation.
 * </p>
 */
public interface Presentable {

    /**
     * This is invoked before the main processing for this servlet. The aim is that all presentation-specific
     * processing will be done here, e.g. setting form values before displaying them.
     *
     *
     * @param state
     * @throws Throwable
     */
    public abstract void prepare(PresentableState state) throws Throwable;

    /**
     * This is invoked after the main processing for the servlet. This contains the code that actually
     * displays the results of processing to the user.
     *
     *
     *
     *
     * @param state
     * @throws Throwable
     */
    public abstract void present(PresentableState state) throws Throwable;

    /**
     * Handle and error. Generally this means show an error page to be displayed when there is a problem.
     * If this is due to an exception,
     * the exception handler will pass along the exception as an attribute named
     * <b>exception</b> in the request.
     * @return
     */
    public abstract void handleError(PresentableState state, Throwable t) throws IOException, ServletException;
}
