package edu.uiuc.ncsa.security.core.util;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Very useful debugging class.  This is much like logging with various levels of
 * debugging available, but this prints to std err, not std out. The idea is
 * that you can stick debug notices everywhere and turn it off and on as needed.</p>
 * <p>What is the advantage to logging? In many cases code is running in some service
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
    public boolean isPrintTS() {
        return printTS;
    }

    public void setPrintTS(boolean printTS) {
        this.printTS = printTS;
    }

    boolean printTS = false;


    protected  String toLabel(int level) {
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
    protected  boolean checkLevelAndLabel(String targetLabel, String givenLabel) {
        return targetLabel.toLowerCase().equals(givenLabel.toLowerCase());
    }

    /**
     * This is used to set the debugging level from a label.
     *
     * @param label
     */
    public  void setDebugLevel(String label) {
        setDebugLevel(toLevel(label));
    }

    protected  int toLevel(String label) {
        if (checkLevelAndLabel(DEBUG_LEVEL_OFF_LABEL, label)) return DEBUG_LEVEL_OFF;
        if (checkLevelAndLabel(DEBUG_LEVEL_INFO_LABEL, label)) return DEBUG_LEVEL_INFO;
        if (checkLevelAndLabel(DEBUG_LEVEL_WARN_LABEL, label)) return DEBUG_LEVEL_WARN;
        if (checkLevelAndLabel(DEBUG_LEVEL_ERROR_LABEL, label)) return DEBUG_LEVEL_ERROR;
        if (checkLevelAndLabel(DEBUG_LEVEL_SEVERE_LABEL, label)) return DEBUG_LEVEL_SEVERE;
        if (checkLevelAndLabel(DEBUG_LEVEL_TRACE_LABEL, label)) return DEBUG_LEVEL_TRACE;

        throw new NFWException("INTERNAL ERROR: Unknown debugging level encountered:" + label);

    }

    public  int getDebugLevel() {
        return debugLevel;
    }

    public  void setDebugLevel(int newDebugLevel) {
        debugLevel = newDebugLevel;
    }

    protected  int debugLevel = DEBUG_LEVEL_OFF; // default or everything gets printed on startup

    public  boolean isEnabled() {
        return getDebugLevel() != DEBUG_LEVEL_OFF;
    }

    public  void setIsEnabled(boolean isEnabled) {
        if (isEnabled) {
            setDebugLevel(DEBUG_LEVEL_WARN); 
        } else {
            setDebugLevel(DEBUG_LEVEL_OFF);
        }
    }


    public  void printIt(int level, Class callingClass, String message) {
        // Standard logging format is date host service: message
        if (level <= getDebugLevel()) {
            if(host == null || host.isEmpty()) {
                printIt((isPrintTS()?Iso8601.date2String(new Date()):"") + " " + callingClass.getSimpleName() + " " + toLabel(level) + ": " + message);
            }else{
                printIt((isPrintTS()?Iso8601.date2String(new Date()):"") + " " + host + " " + callingClass.getSimpleName() + " " + toLabel(level) + ": " + message);
            }
        }
    }
    protected  void printIt(String message) {
        System.err.println(message);
    }

    /** This only prints if the requested level is at least error AND the current utility supports it.
     * Note that if there is no exception, then that is noted in the log as well.
     *
     * @param level
     * @param callingClass
     * @param message
     * @param throwable
     */
    public  void printIt(int level, Class callingClass, String message, Throwable throwable) {

        if (DEBUG_LEVEL_ERROR <= level && level <= getDebugLevel()) {
            if (throwable == null) {
                printIt("     =====>> (NO STACKTRACE AVAILABLE)");
            } else {
                throwable.printStackTrace();
            }
        }
        printIt(level, callingClass, message);
    }


    public  void info(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_INFO, callingClass, message);
    }

    public  void info(Object obj, String message) {
        info(obj.getClass(), message);
    }

    public  void warn(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_WARN, callingClass, message);
    }

    public  void warn(Object obj, String message) {
        warn(obj.getClass(), message);
    }

    public  void error(Object obj, String message, Throwable t) {
        error(obj.getClass(), message, t);
    }

    public  void error(Class callingClass, String message, Throwable t) {
        printIt(DEBUG_LEVEL_ERROR, callingClass, message, t);
    }

    public  void error(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_ERROR, callingClass, message);
    }

    public  void error(Object obj, String message) {
        error(obj.getClass(), message);
    }

    public  void severe(Object obj, String message, Throwable t) {
        severe(obj.getClass(), message, t);
    }

    public  void severe(Class callingClass, String message, Throwable t) {
        printIt(DEBUG_LEVEL_SEVERE, callingClass, message, t);
    }

    public  void severe(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_SEVERE, callingClass, message);
    }

    public  void severe(Object obj, String message) {
        severe(obj.getClass(), message);
    }

    public  void trace(Object obj, String message, Throwable t) {
        trace(obj.getClass(), message, t);
    }

    public  void trace(Class callingClass, String message, Throwable t) {
        printIt(DEBUG_LEVEL_TRACE, callingClass, message, t);
    }

    public  void trace(Class callingClass, String message) {
        printIt(DEBUG_LEVEL_TRACE, callingClass, message);
    }

    public  void trace(Object obj, String message) {
        trace(obj.getClass(), message);
    }


    public  String host;

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
    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("level", getDebugLevel());
        jsonObject.put("print_ts", isPrintTS());
        jsonObject.put("enabled", isEnabled());
        return jsonObject;
    }

    public void fromJSON(JSONObject json){
        setDebugLevel(json.getInt("level"));
        setPrintTS(json.getBoolean("print_ts"));
        setIsEnabled(json.getBoolean("enabled"));
    }
}
