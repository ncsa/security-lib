package edu.uiuc.ncsa.security.core.cache;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Top-level thread class for anything that needs to run in OA4MP. Has barebones logging,
 * getting alarms and sleep intervals plus stopThread method to shut it down cleanly.
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  1:10 PM
 */
public class MyThread extends Thread {
    public long cleanupInterval = 60000L;
    // only enable this if there is a very serious issue since the amount of output will skyrocket.
    boolean deepDebug = false;
    boolean testMode = false;
    MyLoggingFacade logger;
    Collection<LocalTime> alarms = null;
    volatile boolean stopThread = false;

    public MyThread(String name, MyLoggingFacade logger) {
        super(name);
        this.logger = logger;
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * The amount of time, in milliseconds, to wait between attempts to age the cache.
     *
     * @return
     */
    public long getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(long cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public Collection<LocalTime> getAlarms() {
        return alarms;
    }

    public void setAlarms(Collection<LocalTime> alarms) {
        this.alarms = alarms;
    }

    /**
     * Is this thread set to stop?
     *
     * @return
     */
    public boolean isStopThread() {
        return stopThread;
    }

    /**
     * Sets the flag to stop this thread. The next time the thread wakes up after this is enabled, the thread will exit.
     * This allows for a clean shutdown of caching.
     *
     * @param stopThread
     */
    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    protected void info(String x) {
        if (logger == null) {
            DebugUtil.info(this, x);
            return;
        }
        logger.info(x);
    }

    protected void warn(String x) {
        if (logger == null) {
            DebugUtil.warn(this, x);
            return;
        }
        logger.warn(x);
    }



    protected void debug(String x) {
        DebugUtil.trace(deepDebug, this, x);
    }

    protected void debug(String x, Throwable t) {
        DebugUtil.trace(deepDebug, this, x, t);
    }

    protected long getNextSleepInterval() {
        /*
        Note that this asssumes that the alarms are in a sorted collection -- such as
        a TreeSet.
         */
        if (getAlarms() == null || getAlarms().isEmpty()) {
            return getCleanupInterval(); // always set
        }

        LocalDate today = LocalDate.now();
        // trick is that there is no canonical ordering to the alarms, they may be repeated
        // or they may not be distinct for other reasons. Our task here is to find the smallest
        // positive one
        long nextAlarm = -1L;
        for (LocalTime lt : getAlarms()) {
            ZonedDateTime zonedDateTime = ZonedDateTime.of(today, lt, ZoneId.systemDefault());
            long nextInterval = zonedDateTime.toEpochSecond() * 1000L - System.currentTimeMillis();
            if (0 < nextInterval) {
                if (nextAlarm < 0) {
                    nextAlarm = nextInterval;
                } else {
                    // don't check ones in the past
                    nextAlarm = Math.min(nextAlarm, nextInterval);
                }
            }
        }
        if (nextAlarm <= 0L) { // nothing was found
            //So look in the next day
            LocalDate tomorrow = today.plusDays(1);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(tomorrow, getAlarms().iterator().next(), ZoneId.systemDefault());
            nextAlarm = zonedDateTime.toEpochSecond() * 1000 - System.currentTimeMillis();
        }
        return nextAlarm;
    }
}
