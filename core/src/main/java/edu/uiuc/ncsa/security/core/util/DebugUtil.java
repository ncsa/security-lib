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
public class DebugUtil implements DebugConstants {

    static MetaDebugUtil debugUtil = null;

    public static MetaDebugUtil getInstance() {
        if (debugUtil == null) {
            debugUtil = new MetaDebugUtil();
        }
        return debugUtil;
    }

    public static void setInstance(MetaDebugUtil newDebugUtil) {
        debugUtil = newDebugUtil;
    }

    /**
     * If an instance has been set. Note that calling {@link #getInstance()}  will always return
     * an instance.
     *
     * @return
     */
    public static boolean hasInstance() {
        return debugUtil != null;
    }

    public static void setEnabled(boolean enabled) {
        getInstance().setIsEnabled(enabled);
    }

    public static boolean isPrintTS() {
        return getInstance().isPrintTS();
    }

    public static void setPrintTS(boolean printTS) {
        getInstance().setPrintTS(printTS);
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

    public static void printStackTrace(Throwable t) {
        if (isEnabled()) {
            t.printStackTrace();
        }
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


    public static void info(String message) {
        getInstance().info(message);
    }

    public static void info(String title, String message) {
        getInstance().info(title, message);
    }

    public static void info(Class callingClass, String message) {
        getInstance().info(callingClass, message);
    }

    public static void info(Object obj, String message) {
        getInstance().info(obj.getClass(), message);
    }

    public static void warn(String message) {
        getInstance().warn(message);
    }

    public static void warn(String title, String message) {
        getInstance().warn(title, message);
    }

    public static void warn(Class callingClass, String message) {
        getInstance().warn(callingClass, message);
    }

    public static void warn(Object obj, String message) {
        getInstance().warn(obj, message);
    }

    public static void error(String message, Throwable t) {
        getInstance().error(message, t);
    }

    public static void error(String title, String message, Throwable t) {
        getInstance().error(title, message, t);
    }

    public static void error(Object obj, String message, Throwable t) {
        getInstance().error(obj, message, t);
    }

    public static void error(Class callingClass, String message, Throwable t) {
        getInstance().error(callingClass, message, t);
    }

    public static void error(Class callingClass, String message) {
        getInstance().error(callingClass, message);
    }

    public static void error(Object obj, String message) {
        getInstance().error(obj, message);
    }

    public static void severe(String message, Throwable t) {
        getInstance().severe(message, t);
    }

    public static void severe(String title, String message, Throwable t) {
        getInstance().severe(title, message, t);
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

    public static void trace(String message) {
        getInstance().trace(message);
    }

    public static void trace(String message, Throwable t) {
        getInstance().trace(message, t);
    }

    public static void trace(String title, String message, Throwable t) {
        getInstance().trace(title, message, t);
    }

    public static void trace(Object obj, String message, Throwable t) {
        getInstance().trace(obj, message, t);
    }

    public static void trace(Class callingClass, String message, Throwable t) {
        getInstance().trace(callingClass, message, t);
    }

    public static void trace(Class callingClass, String message) {
        getInstance().trace(callingClass, message);
    }

    public static void trace(Object obj, String message) {
        getInstance().trace(obj, message);
    }

    /*
    do local means to only print out the debug message if there is a local flag for a component. This allows
    you to turn on or off component wise debugging.
     */
    public static void trace(boolean doLocal, Object obj, String message, Throwable t) {
        if (doLocal) {
            getInstance().trace(obj, message, t);
        }
    }

    public static void trace(boolean doLocal, Class callingClass, String message, Throwable t) {
        if (doLocal) {
            getInstance().trace(callingClass, message, t);
        }
    }

    public static void trace(boolean doLocal, Class callingClass, String message) {
        if (doLocal) {
            getInstance().trace(callingClass, message);
        }
    }

    public static void trace(boolean doLocal, Object obj, String message) {
        if (doLocal) {
            getInstance().trace(obj, message);
        }
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

    public static void setHost(String host) {
        getInstance().host = host;
    }

    static String devPath = null;
    static public String NCSA_DEV_ROOT = "NCSA_DEV_ROOT";
    static public String DEFAULT_DEV_ROOT = "/home/ncsa/dev/ncsa-git"; // Jeff's system...

    /**
     * This is used to supply all the paths in tests. Set the environment variable NCSA_DEV_PATH
     * and it will be used to resolve resources etc.
     * @return
     */
    static public String getDevPath() {
        if (devPath == null) {
            devPath = System.getenv(NCSA_DEV_ROOT);
            if (StringUtils.isTrivial(devPath)) {
                devPath = DEFAULT_DEV_ROOT;
            }
        }
        return devPath;
    }

    static String configPath = null;
    static public String NCSA_CONFIG_ROOT = "NCSA_CONFIG_ROOT";

    static public String DEFAULT_CONFIG_ROOT = "/home/ncsa/dev/csd/config"; // Jeff's system...
    static public String getConfigPath() {
        if (configPath == null) {
            configPath = System.getenv(NCSA_CONFIG_ROOT);
            if (StringUtils.isTrivial(configPath)) {
                configPath = DEFAULT_CONFIG_ROOT;
            }
        }
        return configPath;
    }
}
