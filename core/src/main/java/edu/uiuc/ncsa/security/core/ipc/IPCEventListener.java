package edu.uiuc.ncsa.security.core.ipc;


import edu.uiuc.ncsa.security.core.Identifiable;

import java.util.EventListener;

/**
 * The listener interface for inter-process communications. Listeners are just threads that wait
 * for  an event signifying either a successful conclusion or failure. This is about the only
 * reasonable way to communicate between java threads that must exchange state.
 * <p>Note that each listener must also implement {@link Identifiable} which means that
 * events should be delivered to the specific listener they are intended for.
 * </p>
 * <h3>Usage</h3>
 * Mostly something that needs to communicate across threads (such as a servlet) will add listeners
 * and fire the appropriate event to the one that requires it. Make sure that the list of such listeners
 * is static within the VM. Mostly you should use the {@link IPCBean} for this or override it if you need to.
 * <p>Created by Jeff Gaynor<br>
 * on 8/19/11 at  9:49 AM
 */
public interface IPCEventListener extends EventListener, Identifiable {
    public void fireEventHappened(IPCEvent ipcEvent);
}
