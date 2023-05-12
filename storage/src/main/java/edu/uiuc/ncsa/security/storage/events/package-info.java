/**
 * This package has events for tracking last access times of objects. There is a
 * thread that can be run. The thread is registered as a listener and the store
 * fires events.  These are in security-lib since they have to be visible for various
 * Java packages in OA4MP. I.e., they are here because of Java.
 * <p>Created by Jeff Gaynor<br>
 * on 3/28/23 at  4:23 PM
 */
package edu.uiuc.ncsa.security.storage.events;