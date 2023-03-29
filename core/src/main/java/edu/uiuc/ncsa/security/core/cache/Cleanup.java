package edu.uiuc.ncsa.security.core.cache;


import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;

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
public class Cleanup<K, V> extends MyThread {
    public Cleanup(MyLoggingFacade logger, String name) {
        super(name, logger);
    }

    public boolean isEnabledLocking() {
        return enabledLocking;
    }

    public void setEnabledLocking(boolean enabledLocking) {
        this.enabledLocking = enabledLocking;
    }

    boolean enabledLocking = true;
    public static Identifier lockID = BasicIdentifier.newID("gc:lock");

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
    Store store;


    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
        setMap(store);
    }
    public List<V> age() {
        if(!isEnabledLocking() || isTestMode()){
            return oldAge();
        }
        if(getStore().containsKey(lockID)){
            info("Locked store for " + getName() + " at " + new Date());
            return new ArrayList<>();
        }
        Identifiable lock = getStore().create();
        lock.setIdentifier(lockID);
        getStore().save(lock);
        try {
            return oldAge();
        }finally {
            // finally block executes even after a return.
            info("removing lock for " + getName() + " at " + new Date());
            getStore().remove(lockID);
            info("removed lock for " + getName());
        }
    }
    /**
     * Clean out old entries by aging the elements, i.e., apply the retention policies.
     * Returns a list of the entries removed
     */
    protected List<V> oldAge() {
        LinkedList<V> linkedList = new LinkedList<V>();
        if (getMap().size() == 0) {
            debug("empty map for " + getName());
            return linkedList;
        }
        debug("map has  " + getMap().size() + " elements for " + getName());


        // copy the object's sorted list or we will get a concurrent modification exception
        for (K key : getSortedKeys()) {
            if(key.equals(lockID)){
                continue;
            }
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


    public Map<K, V> getMap() {
        return map;
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    Map<K, V> map;


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


    @Override
    public void run() {
        info("starting cleanup thread for " + getName());
        while (!isStopThread()) {
            try {
                Date nextRun = new Date();
                long nextCleanup = getNextSleepInterval();
                if(nextCleanup <=0){
                    // this disables cleanup.
                    warn("Cleanup disabled for " + getName() + ". Exiting...");
                    setStopThread(true); //just in case
                    return;
                }
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
