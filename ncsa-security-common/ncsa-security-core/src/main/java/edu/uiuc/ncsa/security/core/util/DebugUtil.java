package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;

/**
 * Utilities for centralizing some common debugging commands. The debug level is set globally for all calls to this.
 * Note that this is not logging. Use the {@link MyLoggingFacade} for that. This is for levels of debugging that may be
 * turned off completely. Logging will be put into the log file. Debugging commands all go to stderr so that they are not part of
 * logging on purpose, but can be collected and viewed separately.
 * <p>Optionally if this is being run on a server, you may specify a host to be printed with each message. If
 * this is not set, that is fine.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 7/27/16 at  2:55 PM
 */
public class DebugUtil implements DebugConstants{

    static MetaDebugUtil debugUtil = null;

    protected static MetaDebugUtil getInstance() {
        if (debugUtil == null) {
            debugUtil = new MetaDebugUtil();
        }
        return debugUtil;
    }


    protected static String toLabel(int level) {
        if (level == DEBUG_LEVEL_OFF) return "";
        if (level == DEBUG_LEVEL_INFO) return DEBUG_LEVEL_INFO_LABEL;
        if (level == DEBUG_LEVEL_WARN) return DEBUG_LEVEL_WARN_LABEL;
        if (level == DEBUG_LEVEL_ERROR) return DEBUG_LEVEL_ERROR_LABEL;
        if (level == DEBUG_LEVEL_SEVERE) return DEBUG_LEVEL_SEVERE_LABEL;
        if (level == DEBUG_LEVEL_TRACE) return DEBUG_LEVEL_TRACE_LABEL;

        throw new NFWException("INTERNAL ERROR: Unknown debugging level encountered: " + level);
    }

    /**
     * Do a case insensitive check for equality of a given label and one of the pre-defined (target) labels.
     *
     * @param targetLabel
     * @param givenLabel
     * @return
     */

    /**
     * This is used to set the debugging level from a label.
     *
     * @param label
     */
    public static void setDebugLevel(String label) {
        setDebugLevel(toLevel(label));
    }

    protected static int toLevel(String label) {
        if (getInstance().checkLevelAndLabel(DEBUG_LEVEL_OFF_LABEL, label)) return DEBUG_LEVEL_OFF;
        if (getInstance().checkLevelAndLabel(DEBUG_LEVEL_INFO_LABEL, label)) return DEBUG_LEVEL_INFO;
        if (getInstance().checkLevelAndLabel(DEBUG_LEVEL_WARN_LABEL, label)) return DEBUG_LEVEL_WARN;
        if (getInstance().checkLevelAndLabel(DEBUG_LEVEL_ERROR_LABEL, label)) return DEBUG_LEVEL_ERROR;
        if (getInstance().checkLevelAndLabel(DEBUG_LEVEL_SEVERE_LABEL, label)) return DEBUG_LEVEL_SEVERE;
        if (getInstance().checkLevelAndLabel(DEBUG_LEVEL_TRACE_LABEL, label)) return DEBUG_LEVEL_TRACE;

        throw new NFWException("INTERNAL ERROR: Unknown debugging level encountered:" + label);

    }

    public static int getDebugLevel() {
        return getInstance().getDebugLevel();
    }

    public static void setDebugLevel(int newDebugLevel) {
        getInstance().setDebugLevel(newDebugLevel);
    }


    public static boolean isEnabled() {
        return getInstance().isEnabled();
    }

    public static void setIsEnabled(boolean isEnabled) {
        getInstance().setIsEnabled(isEnabled);
    }


    public static void printIt(int level, Class callingClass, String message) {
        getInstance().printIt(level, callingClass, message);
    }

    protected static void printIt(String message) {
        getInstance().printIt(message);
    }

    /**
     * This only prints if the requested level is at least error AND the current utility supports it.
     * Note that if there is no exception, then that is noted in the log as well.
     *
     * @param level
     * @param callingClass
     * @param message
     * @param throwable
     */
    public static void printIt(int level, Class callingClass, String message, Throwable throwable) {
        getInstance().printIt(level, callingClass, message, throwable);
    }


    public static void info(Class callingClass, String message) {
        getInstance().info(callingClass, message);
    }

    public static void info(Object obj, String message) {
        getInstance().info(obj.getClass(), message);
    }

    public static void warn(Class callingClass, String message) {
        getInstance().warn(callingClass, message);
    }

    public static void warn(Object obj, String message) {
        getInstance().warn(obj, message);
    }

    public static void error(Object obj, String message, Throwable t) {
        getInstance().error(obj,message,t);
    }

    public static void error(Class callingClass, String message, Throwable t) {
        getInstance().error(callingClass,message,t);
    }

    public static void error(Class callingClass, String message) {
        getInstance().error(callingClass,message);
    }

    public static void error(Object obj, String message) {
        getInstance().error(obj, message);
    }

    public static void severe(Object obj, String message, Throwable t) {
        getInstance().severe(obj, message, t);
    }

    public static void severe(Class callingClass, String message, Throwable t) {
        getInstance().severe(callingClass, message, t);
    }

    public static void severe(Class callingClass, String message) {
        getInstance().severe(callingClass, message);
    }

    public static void severe(Object obj, String message) {
              getInstance().severe(obj, message);
    }

    public static void trace(Object obj, String message, Throwable t) {
        getInstance().trace(obj, message, t);
    }

    public static void trace(Class callingClass, String message, Throwable t) {
        getInstance().trace(callingClass, message, t);
    }

    public static void trace(Class callingClass, String message)
    {
        getInstance().trace(callingClass, message);
    }

    public static void trace(Object obj, String message) {

        getInstance().trace(obj, message);
    }

    /**
     * This will print out a message from a class that includes the class name and current timestamp.
     *
     * @param callingObject
     * @param message
     * @deprecated use warn instead
     */
    public static void dbg(Object callingObject, String message) {
        getInstance().warn(callingObject, message);
    }


    /**
     * @param callingObject
     * @param message
     * @param throwable
     * @deprecated use error instead
     */
    public static void dbg(Object callingObject, String message, Throwable throwable) {
        getInstance().error(callingObject, message, throwable);
    }


    /**
     * @param callingClass
     * @param message
     * @param throwable
     * @deprecated use error instead
     */
    public static void dbg(Class callingClass, String message, Throwable throwable) {
        getInstance().error(callingClass, message, throwable);
    }

    /**
     * @param callingClass
     * @param message
     * @deprecated use warn instead
     */
    public static void dbg(Class callingClass, String message) {
        getInstance().warn(callingClass, message);
    }
    public static void setHost(String host){
        getInstance().host = host;
    }

}
