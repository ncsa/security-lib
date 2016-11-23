package edu.uiuc.ncsa.security.core.util;

import java.util.Date;

/**
 * Utilities for centralizing some common debugging commands
 * <p>Created by Jeff Gaynor<br>
 * on 7/27/16 at  2:55 PM
 */
public class DebugUtil {
    static boolean isEnabled = true; // Disable all debugging out put if tru

    /**
     * This will print out a message from a class that includes the class name and current timestamp.
     *
     * @param callingObject
     * @param message
     */
    public static void dbg(Object callingObject, String message) {
        dbg(callingObject.getClass(), message);
    }

    public static void dbg(Class callingClass, String message) {
        if (isEnabled) {
            System.err.println(callingClass.getSimpleName() + " (" + (new Date()) + "): " + message);
        }
    }
}