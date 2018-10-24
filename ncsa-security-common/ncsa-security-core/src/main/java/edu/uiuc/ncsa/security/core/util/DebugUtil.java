package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;

import java.util.Date;

/**
 * Utilities for centralizing some common debugging commands. The debug level is set globally for all calls to this.
 * Note that this is not logging. Use the {@link MyLoggingFacade} for that. This is for levels of debugging that may be
 * turned off completely. Logging will be put into the log file. Debugging commands all go to stderr so that they are not part of
 * logging on purpose, but can be collected and viewed separately.
 * <p>Created by Jeff Gaynor<br>
 * on 7/27/16 at  2:55 PM
 */
public class DebugUtil {
    /**
     * Turn of debugging
     */
    public static int DEBUG_LEVEL_OFF = 0;
    /**
     * ONly basic information should be displayed, such as milestones in the control flow
     */
    public static int DEBUG_LEVEL_INFO = 1;
    /**
     * Display warnings about the control flow and possibly harmful things
     */
    public static int DEBUG_LEVEL_WARN = 2;
    /**
     * Show errors or possible branch points of errors, but ones that still allow the control flow to continue
     */
    public static int DEBUG_LEVEL_ERROR = 3;
    /**
     * Show error that stop the control flow that probably lead the application to abort of be unrecoverable.
     */
    public static int DEBUG_LEVEL_SEVERE = 4;
    /**
     * Show detailed information about the execution so that detailed information about the control flow
     * can be seen. Note that this may be extremely verbose.
     */
    public static int DEBUG_LEVEL_TRACE = 5;

    public static String DEBUG_LEVEL_OFF_LABEL = "OFF";
    public static String DEBUG_LEVEL_INFO_LABEL = "INFO";
    public static String DEBUG_LEVEL_WARN_LABEL = "WARN";
    public static String DEBUG_LEVEL_ERROR_LABEL = "ERROR";
    public static String DEBUG_LEVEL_SEVERE_LABEL = "SEVERE";
    public static String DEBUG_LEVEL_TRACE_LABEL = "TRACE";

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
    protected static boolean checkLevelAndLabel(String targetLabel, String givenLabel) {
        return targetLabel.toLowerCase().equals(givenLabel.toLowerCase());
    }

    /**
     * This is used to set the debugging level from a label.
     *
     * @param label
     */
    public static void setDebugLevel(String label) {
        setDebugLevel(toLevel(label));
    }

    protected static int toLevel(String label) {
        if (checkLevelAndLabel(DEBUG_LEVEL_OFF_LABEL, label)) return DEBUG_LEVEL_OFF;
        if (checkLevelAndLabel(DEBUG_LEVEL_INFO_LABEL, label)) return DEBUG_LEVEL_INFO;
        if (checkLevelAndLabel(DEBUG_LEVEL_WARN_LABEL, label)) return DEBUG_LEVEL_WARN;
        if (checkLevelAndLabel(DEBUG_LEVEL_ERROR_LABEL, label)) return DEBUG_LEVEL_ERROR;
        if (checkLevelAndLabel(DEBUG_LEVEL_SEVERE_LABEL, label)) return DEBUG_LEVEL_SEVERE;
        if (checkLevelAndLabel(DEBUG_LEVEL_TRACE_LABEL, label)) return DEBUG_LEVEL_TRACE;

        throw new NFWException("INTERNAL ERROR: Unknown debugging level encountered:" + label);

    }

    public static int getDebugLevel() {
        return debugLevel;
    }

    public static void setDebugLevel(int newDebugLevel) {
        debugLevel = newDebugLevel;
    }

    protected static int debugLevel;

    public static boolean isEnabled() {
        return getDebugLevel() != DEBUG_LEVEL_OFF;
    }

    public static void setIsEnabled(boolean isEnabled) {
        if (isEnabled) {
            setDebugLevel(DEBUG_LEVEL_WARN); //default
        } else {
            setDebugLevel(DEBUG_LEVEL_OFF);
        }
    }

    /**
     * The actual call for this entire class. You can call it directly with the debug level and such, but generally should
     * use the named levels.
     *
     * @param level
     * @param callingClass
     * @param message
     */
    public static void printIt(int level, Class callingClass, String message) {
        if (level <= getDebugLevel()) {
            printIt(callingClass.getSimpleName() + " (" + (new Date()) + ") " + toLabel(level) + ": " + message);
        }
    }

    protected static void printIt(String message) {
        System.err.println(message);
    }

    /** This only prints if the requested level is at least error AND the current utility supports it.
     * Note that if ther eis no exception, then that is noted in the log as well.
     *
     * @param level
     * @param callingClass
     * @param message
     * @param throwable
     */
    public static void printIt(int level, Class callingClass, String message, Throwable throwable) {

        if (DEBUG_LEVEL_ERROR <= level && level <= getDebugLevel()) {
            if (throwable == null) {
                printIt("     =====>> (NO STACKTRACE AVAILABLE)");
            } else {
                throwable.printStackTrace();
            }
        }
        printIt(level, callingClass, message);
    }


    public static void info(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_INFO, callingClass, message);
    }

    public static void info(Object obj, String message) {
        info(obj.getClass(), message);
    }

    public static void warn(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_WARN, callingClass, message);
    }

    public static void warn(Object obj, String message) {
        warn(obj.getClass(), message);
    }

    public static void error(Object obj, String message, Throwable t) {
        error(obj.getClass(), message, t);
    }

    public static void error(Class callingClass, String message, Throwable t) {
        printIt(DEBUG_LEVEL_ERROR, callingClass, message, t);
    }

    public static void error(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_ERROR, callingClass, message);
    }

    public static void error(Object obj, String message) {
        error(obj.getClass(), message);
    }

    public static void severe(Object obj, String message, Throwable t) {
        severe(obj.getClass(), message, t);
    }

    public static void severe(Class callingClass, String message, Throwable t) {
        printIt(DEBUG_LEVEL_SEVERE, callingClass, message, t);
    }

    public static void severe(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_SEVERE, callingClass, message);
    }

    public static void severe(Object obj, String message) {
        severe(obj.getClass(), message);
    }

    public static void trace(Object obj, String message, Throwable t) {
        trace(obj.getClass(), message, t);
    }

    public static void trace(Class callingClass, String message, Throwable t) {
        printIt(DEBUG_LEVEL_TRACE, callingClass, message, t);
    }

    public static void trace(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_TRACE, callingClass, message);
    }

    public static void trace(Object obj, String message) {
        trace(obj.getClass(), message);
    }

    /**
     * This will print out a message from a class that includes the class name and current timestamp.
     *
     * @param callingObject
     * @param message
     * @deprecated use warn instead
     */
    public static void dbg(Object callingObject, String message) {
        //dbg(callingObject.getClass(), message);
        warn(callingObject, message);
    }


    /**
     * @param callingObject
     * @param message
     * @param throwable
     * @deprecated use error instead
     */
    public static void dbg(Object callingObject, String message, Throwable throwable) {
        error(callingObject, message, throwable);
    }


    /**
     * @param callingClass
     * @param message
     * @param throwable
     * @deprecated use error instead
     */
    public static void dbg(Class callingClass, String message, Throwable throwable) {
        /*if (isEnabled()) {
            throwable.printStackTrace();
        }
        dbg(callingClass, message);*/
        error(callingClass, message, throwable);
    }

    /**
     * @param callingClass
     * @param message
     * @deprecated use warn instead
     */
    public static void dbg(Class callingClass, String message) {
/*
        if (isEnabled()) {
            System.err.println(callingClass.getSimpleName() + " (" + (new Date()) + "): " + message);
        }
*/
        warn(callingClass, message);
    }


}
