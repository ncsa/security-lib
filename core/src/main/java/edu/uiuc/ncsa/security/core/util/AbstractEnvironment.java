package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Logable;

import java.util.Map;

/**
 * Basic environment. This contains all of the configuration and configured
 * factories that the an application needs. This permits different environments to operate simultaneously if there
 * are, for instance, multiple services running.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 5, 2010 at  10:17:04 AM
 */
public abstract class AbstractEnvironment implements Logable {
    protected AbstractEnvironment(MyLoggingFacade myLogger) {
        this(myLogger, null);
    }

    protected AbstractEnvironment(MyLoggingFacade myLogger, Map<String, String> constants) {
        this.myLogger = myLogger;
        this.constants = constants;
    }

    public boolean isDebugOn() {
        return getMyLogger().isDebugOn();
    }

    protected Map<String, String> constants;

    /**
     * Returns a hash map of constants that are used for this service. There are required constants
     * (such as for the callback url) and others may be added too.
     */

    public Map<String, String> getConstants() {
        return constants;
    }

    public void setDebugOn(boolean setOn) {
        getMyLogger().setDebugOn(setOn);
    }

    public MyLoggingFacade getMyLogger() {
        if (myLogger == null) {
            myLogger = new MyLoggingFacade(getClass().getName(), false);
        }
        return myLogger;
    }

    public void debug(String x) {
        getMyLogger().debug(x);
    }

    MyLoggingFacade myLogger;

    public void error(String x) {
        getMyLogger().error(x);
    }

    public void info(String x) {
        getMyLogger().info(x);
    }

    public void warn(String x) {
        getMyLogger().warn(x);
    }


    protected AbstractEnvironment() {
    }

    boolean pingable = true;
    /**
     * Turn off or on pinging for this OA4MP install. If disabled, all pings will generate a server side
     * exception.
     * @return
     */
    public boolean isPingable(){
        return pingable;
    }

    public void setPingable(boolean pingable) {
        this.pingable = pingable;
    }


}
