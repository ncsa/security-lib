package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.Logable;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple front for logging. Mostly this just centralizes various useful idioms in one place.
 * <p>Created by Jeff Gaynor<br>
 * on Nov 9, 2010 at  10:59:40 AM
 */
public class MyLoggingFacade implements Logable {
    // This implements Logable so it is easier to pass implementing classes through to it.

    public MyLoggingFacade(Logger logger) {
        this.logger = logger;
    }

    public MyLoggingFacade(String className, boolean debugOn) {
        this(className);
        setDebugOn(debugOn);
        debug("****** enabling debugging ******");
    }

    public MyLoggingFacade(String className) {
        setClassName(className);
    }


    /*
    This should be set to false when ready to release and true during development. It lets you turn on/off debug logging
    for everything at once.
     */
    boolean debugOn = false;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    String className;

    /**
     * In debug mode, calls to the {@link #debug(String)} method will print out a detailed message. This allows
     * you to turn on or off debug prints very easily. Note though that while we can control what is printed,
     * we cannot control Java's evaluation of the string to be printed. If the string is very expensive to
     * compute then it will still be computed, it just won't get printed.
     *
     * @return
     */

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    java.util.logging.Logger logger;

    public java.util.logging.Logger getLogger() {
        if (logger == null) {
            logger = java.util.logging.Logger.getLogger(getClassName());
        }
        return logger;
    }


    /**
     * If debug is set on, print the string with the classname and date. Otherwise, do not print debug
     * messages. This allows you to switch debug prints on and off throughout your code via configuration.
     *
     * @param x
     */
    public void debug(String x) {
        if (isDebugOn()) {
            String out = getClassName() + "(" + (new Date()) + "): " + x;
            getLogger().info(out);
        }
    }

    public void info(String x) {
        getLogger().info(getClassName() + "(" + (new Date()) + "): " + x);
    }

    public void warn(String x, Throwable t) {
        getLogger().log(Level.WARNING, x, t);
    }

    public void warn(Throwable t) {
        getLogger().log(Level.WARNING, "(no message)", t);

    }

    public void warn(String x) {
        getLogger().warning(getClassName() + "(" + (new Date()) + "): " + x);
    }

    public void error(String x, Throwable t) {
        getLogger().log(Level.SEVERE, x, t);
    }

    public void error(Throwable t) {
        if (t == null) {
            getLogger().log(Level.SEVERE, "(no message)", t);
            return;
        }
        getLogger().log(Level.SEVERE, t.getMessage() == null ? "(no message)" : t.getMessage(), t);

    }

    public void error(String x) {
        getLogger().severe(getClassName() + "(" + (new Date()) + "): " + x);
    }


}
