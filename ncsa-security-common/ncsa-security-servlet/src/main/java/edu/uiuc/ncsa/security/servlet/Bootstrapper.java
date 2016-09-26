package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class controls the loading of the correct boot strapper, which in turn is
 * charged with translating the configuration into usable objects. This should be
 * set as a context listener in your web.xml. A Typical entry looks like this:
 * <br><br></br></br>
 *  &lt;listener&gt;<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&lt;listener-class&gt;full.pack.age.name.to.MyBootstrapper&lt;/listener-class&gt;<br>
 *   &lt;/listener&gt;<br>
 * <br><br>
 * Simply supply an instance of
 * the your bootstrapper, returned by {@link #getConfigurationLoader(javax.servlet.ServletContext)}
 * and this will in turn stick it into your servlets (by putting it in the
 * environemtn for the top-level {@link AbstractServlet} which everything should inherit from.
 * Note that this
 * is designed for a single use in a web application. The environment will be static
 * and shared by all the servlets in a single web application.
 * <p>Created by Jeff Gaynor<br>
 * on 3/21/12 at  10:59 AM
 */
public abstract class Bootstrapper implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
      // no op. Override if needed.
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            AbstractServlet.setConfigurationLoader(getConfigurationLoader(event.getServletContext()));
            AbstractServlet.setInitialization(getInitialization());
        } catch (Exception e) {
            // one of the few places to actually print the stack trace, since if it bombs here it will be well before
            // Tomcat has loaded much of anything and no or limited logging will be available. Make sure
            // someone can actually find a message if there is a problem.
            e.printStackTrace();
            throw new MyConfigurationException("Error: could not load configuration", e);
        }
    }


    public abstract ConfigurationLoader getConfigurationLoader(ServletContext servletContext) throws Exception;

    public abstract ConfigurationLoader getConfigurationLoader(ConfigurationNode node) throws MyConfigurationException;

     public abstract Initialization getInitialization();
}
