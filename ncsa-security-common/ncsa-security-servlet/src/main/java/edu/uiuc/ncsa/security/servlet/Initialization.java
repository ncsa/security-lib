package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;

import javax.servlet.ServletException;

/**
 * The reason this class exists is because of Java single-inheritence. A top-level servlet
 * class has an init() method that is called. However, if there are many subclasses of this
 * it can be very hard to change their initialization without over-riding several classes
 * or doing something very odd, such as extending a single subclass to do nothing but over-ride
 * its parent then putting in machinery to prevent the parent from running its init method. Hardly
 * ideal.This interface decouples initialization from the servlet. The {@link Bootstrapper}
 * is tasked with making the correct instance of this class which is then injected into the
 * servlet statically on boot. This in turn is called in the servlet init method.
 * <br/><br/>This solve issue OAUTH-112.
 * <p>Created by Jeff Gaynor<br>
 * on 4/15/14 at  10:24 AM
 */
public interface Initialization {
    /**
     * This should handle all initialization tasks for a servlet. Set the environment before use.
     * @throws ServletException
     */
    public void init() throws ServletException;

    public void setEnvironment(AbstractEnvironment environment);

    public AbstractEnvironment getEnvironment();

    /**
     * Many initialization tasks can only be done dynamically, after the servlet has been created and
     * its {@link javax.servlet.ServletContext} set. There <b>has</b> to be a back reference to
     * or you cannot completely control servlet startup.
     * @return
     */
    public AbstractServlet getServlet();

    public void setServlet(AbstractServlet servlet);

    public ExceptionHandler getExceptionHandler();
}
