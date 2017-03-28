package edu.uiuc.ncsa.security.servlet;

import edu.uiuc.ncsa.security.core.Logable;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.core.util.ConfigurationLoader;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Very straightforward servlet wrapper. This sets up logging and debug. All posts and gets
 * are intercepted and routed to a single doIt method.<br><br></br></br>
 * 3/23/2012: Added simple bootstrapping mechanism. This requires you set a context
 * listener in your web.xml deployment descriptor. See the documentation in
 * {@link Bootstrapper} for more details.
 * <p>Created by Jeff Gaynor<br>
 * on May 3, 2010 at  11:35:16 AM
 */
public abstract class  AbstractServlet extends HttpServlet implements Logable {
    public static final String PING_PARAMETER = "ping";

    static ConfigurationLoader<? extends AbstractEnvironment> configurationLoader;

    public static Initialization getInitialization() {
        return initialization;
    }

    public static void setInitialization(Initialization initialization) {
        AbstractServlet.initialization = initialization;
    }

    protected static Initialization initialization;

    public static ConfigurationLoader<? extends AbstractEnvironment> getConfigurationLoader() {
        return configurationLoader;
    }

    public static void setConfigurationLoader(ConfigurationLoader<? extends AbstractEnvironment> b) {
        configurationLoader = b;
    }


    public static AbstractEnvironment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(AbstractEnvironment env) {
        environment = env;
    }

    protected static AbstractEnvironment environment;


    /**
     * Loads the current environment. This is *not* called automatically. Usually a user will
     * create a custom environment and have a getter for that which checks if the environment has
     * been set and if not, load it or if so, cast it and return the result.
     *
     * @throws IOException
     */
    public abstract void loadEnvironment() throws IOException;

    public boolean isDebugOn() {
        return getMyLogger().isDebugOn();
    }

    public void setDebugOn(boolean setOn) {
        getMyLogger().setDebugOn(setOn);
    }

    protected MyLoggingFacade getMyLogger() {
        if (environment != null) {
            return environment.getMyLogger();
        }
        // always return one so even if things blow up some record remains...
        return new MyLoggingFacade("oa4mp");
    }

    public void debug(String x) {
      getMyLogger().debug(x);
    }

    public void error(String x) {
        getMyLogger().error(x);
    }

    public void info(String x) {
        getMyLogger().info(x);
    }

    public void warn(String x) {
        getMyLogger().warn(x);
    }

    MyLoggingFacade myLogger;

    /**
     * One stop shopping for exception handling. All thrown exceptions are intercepted and run through this.
     * Depending on their type they are wrapped or passed along. You can change this behavior if you need to.
     * <p>Note that all runtime exceptions, IOExceptions and ServletExceptions are not modified,
     * so if you over-ride this and throw one of those exceptions you will not get extra cruft.
     * <p>Also, a response is passed along. This may be used in over-rides, but is not used in the
     * basic implementation. If it is null, it should be ignored.</p>
     *
     * @param t
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    protected void handleException(Throwable t, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        error("INTERNAL ERROR: " + (t.getMessage() == null ? "(no message)" : t.getMessage())); // log it appropriately
        // ok, if it is a strange error, print a stack if you need to.
        if (isDebugOn()) {
            t.printStackTrace();
        }
        t.printStackTrace();

        getExceptionHandler().handleException(t, request, response);

    }

    public ExceptionHandler getExceptionHandler() {
        if (exceptionHandler == null) {
            warn("Warning: no Exception Handler set, using basic exception handling only!");
            exceptionHandler = new BasicExceptionHandler(getMyLogger());
        }
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    ExceptionHandler exceptionHandler;

    @Override
    public void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        try {
            //   printAllParameters(httpServletRequest);
            if (doPing(httpServletRequest, httpServletResponse)) return;
            /*
            To read the request body for processing use something like this.
               // Read from request
               StringBuilder buffer = new StringBuilder();
               BufferedReader reader = httpServletRequest.getReader();
               String line;
               while ((line = reader.readLine()) != null) {
                   buffer.append(line);
                }
               String data = buffer.toString()
             */
            doIt(httpServletRequest, httpServletResponse);
        } catch (Throwable t) {
            handleException(t, httpServletRequest, httpServletResponse);
        }
    }

    @Override
    public void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        try {
            //   printAllParameters(httpServletRequest);
            if (doPing(httpServletRequest, httpServletResponse)) return;
            doIt(httpServletRequest, httpServletResponse);
        } catch (Throwable t) {
            t.printStackTrace();
            handleException(t, httpServletRequest, httpServletResponse);
        }
    }

    /**
     * This returns true or false for
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     */
    protected boolean doPing(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        if (req.getParameterMap().containsKey(PING_PARAMETER)) {
            // Check if ping is enabled. Set in AbstractEnvironment??
            if (!getEnvironment().isPingable()) {
                warn("Ping attempted on server. Ping not enabled.");
                // Throw a servlet exception so it is unclear if the server is down or not.
                // This means it is un-pingable.
                throw new ServletException("Internal error");
            }
            info("ping ok");
            resp.setStatus(HttpStatus.SC_NO_CONTENT);
            return true;
        }
        // Since every request runs through this, logging that no ping was done would just fill up the logs.
        return false;
    }

    protected abstract void doIt(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Throwable;

    @Override
    public void init() throws ServletException {
        super.init();
        if (environment == null) {
            info("loading environment...");
            try {
                loadEnvironment();
            } catch (IOException e) {
                warn("failed to load environment.");
                throw new GeneralException("Error loading environment", e);
            }
            info("done loading environment.");
        }
        if (getInitialization() == null) {
            info("Caution: no initializer set for " + getClass().getSimpleName() + ". Skipping...");
        } else {
            getInitialization().setEnvironment(getEnvironment());
            getInitialization().setServlet(this);
            getInitialization().init();
            setExceptionHandler(getInitialization().getExceptionHandler());
        }
    }

    /**
     * This nulls out the environment which should force a reload of it at the next call. Put anything
     * else you need nulled out here. This allows you to have, e.g., an updated configuration reloaded
     * on the fly without a tomcat restart.
     */
    public void resetState() {
        environment = null;
    }


    /**
     * Utility to get constants or code gets really wordy.
     *
     * @param key
     * @return
     */
    protected static String CONST(String key) {
        return getEnvironment().getConstants().get(key);
    }

    /**
     * Convenience to get the first (zero-th index) value of a parameter list from a servlet request.
     *
     * @param request
     * @param key
     * @return
     */
    protected String getFirstParameterValue(HttpServletRequest request, String key) {
        Object obj = request.getParameter(key);

        if (obj == null) return null;
        if (!obj.getClass().isArray()) {
            return obj.toString();
        }
        // If not null and not a singleton, this is an array of strings. Return the zero-th
        String[] values = (String[]) obj;
        if (values.length == 0) return null;
        return values[0];
    }

    /**
     * This will print all parameters to standard err. It is a low-level debugging tool
     * and should never be used in production. It gives in effect a report on what is
     * actually being passed in.
     *
     * @param request
     */
    protected void printAllParameters(HttpServletRequest request) {
        ServletDebugUtil.printAllParameters(this.getClass(), request);
    }
}
