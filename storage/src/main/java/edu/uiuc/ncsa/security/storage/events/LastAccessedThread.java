package edu.uiuc.ncsa.security.storage.events;

import edu.uiuc.ncsa.security.core.cache.MyThread;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.ListeningStoreInterface;

import java.util.*;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/28/23 at  4:45 PM
 */
public class LastAccessedThread extends MyThread {
    public LastAccessedThread(String name, MyLoggingFacade logger, LastAccessedEventListener lastAccessedEventListener) {
        super(name, logger);
        this.lael = lastAccessedEventListener;
    }

    LastAccessedEventListener lael;
    public static String LOCK_ID = "monitor:lock";

    public boolean isDebugOn() {
        return debugOn;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    boolean debugOn = false;

    protected void updateStore() {
        Map<UUID, IDMap> map = lael.getIdsByStoreMap();
        Map<UUID, ListeningStoreInterface> stores = lael.getStoreMap();
        lael.clear(); // so we don't get concurrent mod exceptions
        for (UUID uuid : map.keySet()) {
            IDMap idMap = map.get(uuid);
            if (!map.containsKey(uuid)) {
                throw new NFWException("error: the store with id \"" + uuid + "\" is not recognized.");
            }

            // Every event in this map has the same store stashed in it.
            stores.get(uuid).lastAccessUpdate(idMap);
        }
    }

    public boolean isStopThread() {
        return stopThread;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    boolean stopThread = false;

    @Override
    public void run() {

        info("starting cleanup thread for " + getName());
        while (!isStopThread()) {
            try {
                Date nextRun = new Date();
                long nextCleanup = getNextSleepInterval();
                if (nextCleanup <= 0) {
                    // this disables the thread.
                    warn("Thread disabled for " + getName() + ". Exiting...");
                    setStopThread(true); //just in case
                    return;
                }
                nextRun.setTime(nextRun.getTime() + nextCleanup);
                info("next iteration for " + getName() + " scheduled for " + nextRun);
                sleep(nextCleanup);

                if (lael.getIdsByStoreMap().isEmpty()) {
                    info("thread for " + getName() + " no entries, skipped at " + (new Date()));
                } else {
                    info("thread for " + getName() + " starting at " + (new Date()));
                    updateStore();
                    // now cleanup so the state is ready for more.
                    lael.clear();
                }
            } catch (InterruptedException e) {
                setStopThread(true); // just in case.
                warn("Cleanup for " + getName() + " interrupted, stopping thread...");
            }
        }
    }

    @Override
    protected void info(String x) {
        if(isDebugOn()){
            System.err.println(getClass().getSimpleName() + " INFO:" + x);
            return;
        }
        super.info(x);
    }

    @Override
    protected void warn(String x) {
        if(isDebugOn()){
            System.err.println(getClass().getSimpleName() + " WARN:" + x);
        }
        super.warn(x);
    }
}

