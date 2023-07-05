package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Very useful debugging class.  This is much like logging with various levels of
 * debugging available, but this prints to std err, not std out. The idea is
 * that you can stick debug notices everywhere and turn it off and on as needed.
 * Giving a second debug-only system that is not loggged. In many cases you do
 * not want debug information going to a log (such as if the log might be public and
 * there is sensitive information). This lets you stick view it separately.</p>
 * <p>What other advantage to a debug system? In many cases code is running in some service
 * that has logging, say, piped to the system log. (Common approach for Tomcat
 * running under Apache). This means that the log might be simply enormous and
 * processing it looking for your 3 debug statements is hard or very slow.
 * (Think active production server with literally hundreds of entries being generated
 * every second.) These debug statements
 * get sent to standard error, so it is easy to turn it on for a bit, pick them up,
 * then turn debugging back off.</p>
 * <p>Created by Jeff Gaynor<br>
 * on 10/23/20 at  2:54 PM
 */
public class MetaDebugUtil implements DebugConstants, Serializable {
    public MetaDebugUtil() {
    }

    public MetaDebugUtil(String title, int debugLevel, boolean printTS, String host) {
        this.printTS = printTS;
        this.title = title;
        this.debugLevel = debugLevel;
        this.host = host;
    }

    public MetaDebugUtil(String title, int debugLevel, boolean printTS) {
        this(title, debugLevel, printTS, null);
    }

    public boolean isPrintTS() {
        return printTS;
    }

    public void setPrintTS(boolean printTS) {
        this.printTS = printTS;
    }

    boolean printTS = false;

    /**
     * Name of the component that goes in the entry. This may be set and used implcitly
     * or you may explicitly call with the name of the object.
     *
     * @return
     */
    public String getTitle() {
        if (title == null) {
            title = DEFAULT_TITLE;
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String title = null;
    public static String DEFAULT_TITLE = "(un-named)";

    public static String toLabel(int level) {
        switch (level) {
            case DEBUG_LEVEL_OFF:
                return DEBUG_LEVEL_OFF_LABEL;
            case DEBUG_LEVEL_INFO:
                return DEBUG_LEVEL_INFO_LABEL;
            case DEBUG_LEVEL_WARN:
                return DEBUG_LEVEL_WARN_LABEL;
            case DEBUG_LEVEL_ERROR:
                return DEBUG_LEVEL_ERROR_LABEL;
            case DEBUG_LEVEL_SEVERE:
                return DEBUG_LEVEL_SEVERE_LABEL;
            case DEBUG_LEVEL_TRACE:
                return DEBUG_LEVEL_TRACE_LABEL;
        }
        throw new NFWException("INTERNAL ERROR: Unknown debugging level encountered: " + level);
    }

    /**
     * Do a case-insensitive check for equality of a given label and one of the pre-defined (target) labels.
     *
     * @param targetLabel
     * @param givenLabel
     * @return
     */
    protected static boolean checkLevelAndLabel(String targetLabel, String givenLabel) {
        return targetLabel.equalsIgnoreCase(givenLabel);
    }

    /**
     * This is used to set the debugging level from a label.
     *
     * @param label
     */
    public void setDebugLevel(String label) {
        int lll = toLevel(label);
        if (lll == DEBUG_LEVEL_UNKNOWN) {
            throw new IllegalArgumentException("Unknown debugging level for \"" + label + "\"");
        }
        setDebugLevel(toLevel(label));
    }

    public static int toLevel(String label) {
        switch (label.toLowerCase()) {
            case DEBUG_LEVEL_OFF_LABEL:
                return DEBUG_LEVEL_OFF;
            case DEBUG_LEVEL_INFO_LABEL:
                return DEBUG_LEVEL_INFO;
            case DEBUG_LEVEL_WARN_LABEL:
                return DEBUG_LEVEL_WARN;
            case DEBUG_LEVEL_ERROR_LABEL:
                return DEBUG_LEVEL_ERROR;
            case DEBUG_LEVEL_SEVERE_LABEL:
                return DEBUG_LEVEL_SEVERE;
            case DEBUG_LEVEL_TRACE_LABEL:
                return DEBUG_LEVEL_TRACE;
            default:
                return DEBUG_LEVEL_UNKNOWN;
        }
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int newDebugLevel) {
        debugLevel = newDebugLevel;
    }

    protected int debugLevel = DEBUG_LEVEL_OFF; // default or everything gets printed on startup

    /**
     * Checks if the debug level is set to off.
     *
     * @return
     */
    public boolean isEnabled() {
        return getDebugLevel() != DEBUG_LEVEL_OFF;
    }

    /**
     * Setting this to true will set the debug level to warn only. It is better to use
     * the {@link #setDebugLevel(int)} directly. Some very old applications still use this.
     *
     * @param isEnabled
     * @deprecated
     */
    public void setIsEnabled(boolean isEnabled) {
        if (isEnabled) {
            setDebugLevel(DEBUG_LEVEL_WARN);
        } else {
            setDebugLevel(DEBUG_LEVEL_OFF);
        }
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    String delimiter = " | ";

    public void printIt(int level, String title, String message) {
        if (getDebugLevel() <= level) {
            if (host == null || host.isEmpty()) {
                printIt((isPrintTS() ? Iso8601.date2String(new Date()) : "") + getDelimiter() + title + getDelimiter() + toLabel(level) + ": " + message);
            } else {
                printIt((isPrintTS() ? Iso8601.date2String(new Date()) : "") + getDelimiter() + "<" + host + ">" + getDelimiter() + title + getDelimiter() + toLabel(level) + ": " + message);
            }
        }

    }

    public void printIt(int level, Class callingClass, String message) {
        printIt(level, callingClass.getSimpleName(), message);
    }

    protected void printIt(String message) {
        System.err.println(message);
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
    public void printIt(int level, Class callingClass, String message, Throwable throwable) {
        printIt(level, callingClass.getSimpleName(), message, throwable);
    }

    public void printIt(int level, String title, String message, Throwable throwable) {
        if (DEBUG_LEVEL_ERROR == level && level == getDebugLevel()) {
            if (throwable == null) {
                printIt("     =====>> (NO STACKTRACE AVAILABLE)");
            } else {
                throwable.printStackTrace();
            }
        }
        printIt(level, title, message);

    }


    public void info(String message) {
        info(getTitle(), message);
    }

    public void info(String title, String message) {
        printIt(DEBUG_LEVEL_INFO, title, message);
    }

    public void info(Class callingClass, String message) {
        info(callingClass.getSimpleName(), message);
    }

    public void info(Object obj, String message) {
        info(obj.getClass(), message);
    }

    public void warn(String message) {
        warn(getTitle(), message);
    }

    public void warn(String title, String message) {
        printIt(DEBUG_LEVEL_WARN, title, message);

    }


    public void warn(Class callingClass, String message) {
        warn(callingClass.getSimpleName(), message);
    }

    public void warn(Object obj, String message) {
        warn(obj.getClass().getSimpleName(), message);
    }

    public void error(String message, Throwable t) {
        error(getTitle(), message, t);
    }

    public void error(String message) {
        error(getTitle(), message);
    }

    public void error(String title, String message) {
        printIt(DEBUG_LEVEL_ERROR, title, message);
    }

    public void error(String title, String message, Throwable t) {
        printIt(DEBUG_LEVEL_ERROR, title, message, t);
    }

    public void error(Object obj, String message, Throwable t) {
        error(obj.getClass().getSimpleName(), message, t);
    }

    public void error(Class callingClass, String message, Throwable t) {
        severe(callingClass.getSimpleName(), message, t);
        //printIt(DEBUG_LEVEL_ERROR, callingClass, message, t);
    }

    public void error(Class callingClass, String message) {
        error(callingClass.getSimpleName(), message);
        //printIt(DEBUG_LEVEL_ERROR, callingClass, message);
    }

    public void error(Object obj, String message) {
        error(obj.getClass(), message);
    }

    public void severe(String message, Throwable t) {
        severe(getTitle(), message, t);
    }

    public void severe(String title, String message, Throwable t) {
        printIt(DEBUG_LEVEL_SEVERE, title, message, t);

    }

    public void severe(Object obj, String message, Throwable t) {
        severe(obj.getClass().getSimpleName(), message, t);
    }

    public void severe(Class callingClass, String message, Throwable t) {
        severe(callingClass.getSimpleName(), message, t);
    }

    public void severe(String message) {
        severe(getTitle(), message);
    }


    public void severe(String title, String message) {
        printIt(DEBUG_LEVEL_SEVERE, title, message);

    }

    public void severe(Class callingClass, String message) {
        severe(callingClass.getSimpleName(), message);
    }

    public void severe(Object obj, String message) {
        severe(obj.getClass().getSimpleName(), message);
    }

    public void trace(String message) {
        trace(getTitle(), message);
    }

    public void trace(String message, Throwable t) {
        trace(getTitle(), message, t);
    }

    public void trace(String title, String message, Throwable t) {
        printIt(DEBUG_LEVEL_TRACE, title, message, t);
    }

    public void trace(Object obj, String message, Throwable t) {
        trace(obj.getClass().getSimpleName(), message, t);
    }

    public void trace(Class callingClass, String message, Throwable t) {
        trace(callingClass.getSimpleName(), message, t);
    }

    public void trace(Class callingClass, String message) {
        trace(callingClass.getSimpleName(), message);
    }

    public void trace(String title, String message) {
        printIt(DEBUG_LEVEL_TRACE, title, message);

    }

    public void trace(Object obj, String message) {
        trace(obj.getClass().getSimpleName(), message);
        //trace(obj.getClass().getSimpleName(), message); // Don't use -- java turns this into a recursive call for trace(Object, message)
        //printIt(DEBUG_LEVEL_TRACE, (obj instanceof String)?((String)obj):obj.getClass().getSimpleName(), message);
    }

    public boolean hasHost() {
        return host != null;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String host;

    @Override
    public String toString() {
        return "MetaDebugUtil{" +
                "enabled=" + isEnabled() +
                ",printTS=" + printTS +
                ", debugLevel=" + debugLevel +
                ", host='" + host + '\'' +
                ", hash='" + hashCode() + '\'' +
                '}';
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_LEVEL, getDebugLevel());
        jsonObject.put(JSON_TS_ON, isPrintTS());
        if (title != null) {
            jsonObject.put(JSON_TITLE, title);
        }
        jsonObject.put(JSON_ENABLED, isEnabled());
        if(!StringUtils.isTrivial(getHost())) {
            jsonObject.put(JSON_HOST, getHost());
        }
        if(!StringUtils.isTrivial(getDelimiter())){
            jsonObject.put(JSON_DELIMITER, getDelimiter());
        }
        return jsonObject;
    }

    public void fromJSON(JSONObject json) {
        setDebugLevel(json.getInt(JSON_LEVEL));
        setPrintTS(json.getBoolean(JSON_TS_ON));
        setDelimiter(json.getString(JSON_DELIMITER));
        if (json.containsKey(JSON_TITLE) && !StringUtils.isTrivial(json.getString(JSON_TITLE))) {
            setTitle(json.getString(JSON_TITLE));
        }
        setIsEnabled(json.getBoolean(JSON_ENABLED));
        if(json.containsKey(JSON_HOST) && !StringUtils.isTrivial(json.getString(JSON_HOST)))
        setHost(json.getString(JSON_HOST));
    }

    public static final String JSON_LEVEL = "level";
    public static final String JSON_DELIMITER = "delimiter";
    public static final String JSON_TS_ON = "ts_on";
    public static final String JSON_TITLE = "title";
    public static final String JSON_ENABLED = "enabled";
    public static final String JSON_HOST = "host";

}
