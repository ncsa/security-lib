package edu.uiuc.ncsa.security.core;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Nov 9, 2010 at  11:03:07 AM
 * Interface for logging. Things that need to log implement this interface. Note that there is a
 * debug flag. If this is on, then calls to debug will print, otherwise they are
 * disabled. Be warned that the argument to all of these, being strings, are evaluated first.
 * Expensive formatting for debug statements can still slow down your application even if
 * debugging is off!
 */
public interface Logable {
    /**
     * Query if debugging is enabled for this logger.
     * @return
     */
    public boolean isDebugOn();

    /**
     * Enable/disable debugging for this logger
     * @param setOn
     */
    public void setDebugOn(boolean setOn);

    /**
     * Write a debug message to the log.
     * @param x
     */
    public void debug(String x);

    /**
     * Write an informational message to the log
     * @param x
     */
    public void info(String x);

    /**
     * Write a warning to the log. This indicates a severe, but non-fatal condition exists.
     * @param x
     */
    public void warn(String x);

    /**
     * Write an error message to the log. Typically this is invoked as the last command before throwing an exception.
     * @param x
     */
    public void error(String x);
}
