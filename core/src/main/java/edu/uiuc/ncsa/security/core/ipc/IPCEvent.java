package edu.uiuc.ncsa.security.core.ipc;

import java.util.EventObject;

import static edu.uiuc.ncsa.security.core.ipc.IPCBean.STATUS_NOT_STARTED;

/**
 * An event that is created for inter-process communications.
 * <p>Created by Jeff Gaynor<br>
 * on 8/19/11 at  9:49 AM
 */
public class IPCEvent extends EventObject {
    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    Throwable throwable;

    public int getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(int exitStatus) {
        this.exitStatus = exitStatus;
    }

    int exitStatus = STATUS_NOT_STARTED;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public IPCEvent(Object source) {
        super(source);
    }
}
