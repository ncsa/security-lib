package edu.uiuc.ncsa.security.core.ipc;


import edu.uiuc.ncsa.security.core.Identifiable;

/**
 * An bean for Inter-Process Communication. This lets the various threads and servlets track information so that
 * exceptions can be propagated between them, This tracks pending transactions that are
 * initiated by the authorized servlet. This waits for a transaction to
 * be completed or to intercept any errors that might occur during execution.
 * <p>Created by Jeff Gaynor<br>
 * on 8/18/11 at  6:47 PM
 */
public abstract class IPCBean implements Runnable, IPCEventListener {
    public static final int STATUS_NOT_STARTED = 0;
    public static final int STATUS_DONE = 1;
    public static final int STATUS_EXCEPTION = -1;
    public static final int STATUS_NOT_COMPLETED = -2;
    public static final long THREAD_SLEEP_INTERVAL = 50L;


    boolean stopThread = false;

    public boolean isStopThread() {
        return stopThread;
    }

    /**
     * Sets this thread to stop the next time it wakes up.
     *
     * @param stopThread
     */
    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }


    public long getSleepInterval() {
        return sleepInterval;
    }

    public void setSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    long sleepInterval = THREAD_SLEEP_INTERVAL;

    /**
     * Runs this thread. The bean stores the state and listens for events.
     */
    public void run() {
        while (!isStopThread()) {
            try {
                Thread.sleep(getSleepInterval());
            } catch (Throwable e) {
                setStopThread(true);
                setThrowable(e);
                setStatus(STATUS_EXCEPTION);
            }
        } //end while
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    Throwable throwable;

    public void fireEventHappened(IPCEvent ipcEvent) {
        if (ipcEvent.getExitStatus() == STATUS_DONE || ipcEvent.getExitStatus() == STATUS_EXCEPTION) {
            // So if the process has completed correctly or there is an exception, hop out.
            setStopThread(true);
        }
        setStatus(ipcEvent.getExitStatus());
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    int status = STATUS_NOT_STARTED;

    /**
     * This should never be cloned in practice.
     * @return
     */
    @Override
    public Identifiable clone() {
        return null;
    }
}
