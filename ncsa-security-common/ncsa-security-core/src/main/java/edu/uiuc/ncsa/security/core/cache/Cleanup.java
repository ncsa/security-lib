package edu.uiuc.ncsa.security.core.cache;


import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * General utility for applying retention policies to maps (which covers all caching as well as stores).
 * This may be used either directly by calling {@link #age()} or by running this as a thread.
 * <h3>Use</h3>
 * Create an instance of this either <br/>
 * <ul>
 *     <li>as &lt;{@link edu.uiuc.ncsa.security.core.Identifiable}, {@link CachedObject}&gt; for caches</li>
 *     <li>as &lt;key, value&gt; for a map or store.</li>
 * </ul>
 * You must then set some {@link RetentionPolicy} so that the cleanup knows when to expunge entries (there
 * are several possible policies and you can have as many as you want, though it is up to you to ensure your
 * policies make sense.) Start this in its own thread and it will quietly clean up ever so often. To stop the
 * thread, call the {@link #setStopThread(boolean)} to true and the thread will halt as soon as it wakes up.
 * YOu can also kill the thread directly but generally that can do bad things if the thread is in the middle
 * of, say, updating you store. Most web servers will wait until all threads exit of their own accord before finishing
 * a shutdown, incidentally.
 * <p>Created by Jeff Gaynor<br>
 * on 7/12/11 at  11:39 AM
 */
public class Cleanup<K, V> extends Thread {
    public Cleanup(MyLoggingFacade logger, String name) {
        super(name);
        this.logger = logger;
    }

    // only enable this if there is a very serious issue since the amount of output will skyrocket.
    boolean deepDebug = true;

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    boolean testMode = false;

    /**
     * Clean out old entries by aging the elements, i.e., apply the retention policies.
     * Returns a list of the entries removed
     */
    public List<V> age() {
        LinkedList<V> linkedList = new LinkedList<V>();
        if (getMap().size() == 0) {
            debug("empty map for " + getName());
            return linkedList;
        }
        debug("map has  " + getMap().size() + " elements for " + getName());


        // copy the object's sorted list or we will get a concurrent modification exception
        for (K key : getSortedKeys()) {
            V co = getMap().get(key);
            for (RetentionPolicy rp : getRetentionPolicies()) {
                // see if we should bother in the first place...
                if (rp.applies()) {
                    if (!rp.retain(key, co)) {
                        if(!isTestMode()){
                            debug("removing " + key);
                            getMap().remove(key);
                        }
                        linkedList.add(co);
                    }
                }
            }
        }

        return linkedList;
    }


    /**
     * Return all the keys for the map, sorted into a some order, usually by timestamp.
     * The default assumes that the keys are comparable and orders them. Override as needed.
     *
     * @return
     */
    public Set<K> getSortedKeys() {
        TreeSet<K> sortedList = new TreeSet<K>();
        sortedList.addAll(getMap().keySet());
        return sortedList;
    }


    MyLoggingFacade logger;
    public long cleanupInterval = 60000L;

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

    Collection<LocalTime> alarms = null;

    public Map<K, V> getMap() {
        return map;
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    Map<K, V> map;

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

    volatile boolean stopThread = false;


    public void addRetentionPolicy(RetentionPolicy retentionPolicy) {
        getRetentionPolicies().add(retentionPolicy);
    }

    public void removeRetentionPolicy(RetentionPolicy retentionPolicy) {
        getRetentionPolicies().remove(retentionPolicy);
    }

    public List<RetentionPolicy> getRetentionPolicies() {
        if (retentionPolicies == null) {
            retentionPolicies = new LinkedList<RetentionPolicy>();
        }
        return retentionPolicies;
    }

    public void setRetentionPolicies(LinkedList<RetentionPolicy> retentionPolicies) {
        this.retentionPolicies = retentionPolicies;
    }

    LinkedList<RetentionPolicy> retentionPolicies;

    protected void info(String x) {
        if(logger == null){
            DebugUtil.info(this, x);
            return;
        }
        logger.info(x);
    }


    protected void warn(String x) {
        if(logger == null){
            DebugUtil.warn(this, x);
            return;
        }
        logger.warn(x);
    }

    /**
     * Log the results of this. Override this if you need more detailed output.
     * It takes the list of removed entries. Actual writing to the log is done
     * with the call {@link #info(String)}.
     *
     * @param removed
     */

    public void info(List<V> removed) {
        info("removed:" + removed.size() + ", remaining:" + getMap().size());
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
            if(0 < nextInterval) {
                if(nextAlarm < 0){
                      nextAlarm = nextInterval;
                }else{
                    // don't check ones in the past
                    nextAlarm = Math.min(nextAlarm, nextInterval);
                }
            }
        }
        if(nextAlarm <= 0L){ // nothing was found
            //So look in the next day
            LocalDate tomorrow = today.plusDays(1);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(tomorrow, getAlarms().iterator().next(), ZoneId.systemDefault());
            nextAlarm = zonedDateTime.toEpochSecond()*1000 - System.currentTimeMillis();
        }
        return nextAlarm;
    }

    @Override
    public void run() {
        info("starting cleanup thread for " + getMap());
        while (!isStopThread()) {
            try {
                Date nextRun = new Date();
                long nextCleanup = getNextSleepInterval();
                nextRun.setTime(nextRun.getTime() + nextCleanup);
                info("next cleanup for " + getName() + " scheduled for " + nextRun);
                sleep(nextCleanup);


                if (getMap() == null) {
                    info("cleanup for " + getName() + " no entries, skipped at " + (new Date()));
                }else{
                    info("cleanup for " + getName() + " starting at " + (new Date()));
                    try {
                        List<V> removed = age();
                        if (!removed.isEmpty()) {
                            info(removed);
                        }
                        info("cleanup removed " + removed.size() + " entries for " + getName());
                    } catch (Throwable throwable) {
                        debug("error in cleanup:" + throwable.getMessage(), throwable);
                        // nix to do, really if this fails.
                        // mostly just print out something someplace so there is a record of the failure.
                        int sz = -1;
                        try {
                            sz = getMap().size();
                            if (0 < sz) {
                                String msg = throwable.getMessage() == null ? "(no message available)" : throwable.getMessage();
                                warn("Error in cleanup:\"" + msg + "\" Processing will continue on " + sz + " elements.");
                            }

                        } catch (Throwable x) {
                            // since the size() call can fail (e.g. database + network flakiness) catch anything.
                            String msg = x.getMessage() == null ? "(no message available)" : x.getMessage();
                            warn("Error in cleanup:\"" + msg + "\" Processing will continue.");
                        }
                        return;
                    }
                    // sleep after cleanup so startup gets a cleanup.
                }
            } catch (InterruptedException e) {
                setStopThread(true); // just in case.
                warn("Cleanup for " + getName() + " interrupted, stopping thread...");
            }
        }
    }
}
