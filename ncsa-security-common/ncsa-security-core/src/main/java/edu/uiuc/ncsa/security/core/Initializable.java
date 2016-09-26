package edu.uiuc.ncsa.security.core;

/**
 * An interface for getting clean semantics on creation, initialization and destruction of things. The issue is that
 * things -- like databases and other persistent stores -- must have a separate creation/initialization
 * mechanisms unlike simple Java object instantiation where both occur at once. <br>
 * This interface organizes this. <br><br>
 * <b>NOTE:</b> A failure means that the returned value is false rather than true! In case of bona fide
 * errors applications should throw the specific runtime exception or wrap it in a GeneralException.
 * An example would be if this is implemented against a database and no connection exists.
 * <p/>
 * <h2>Lifecycle</h2>
 * Generally create and destroy are called at most once in the lifecycle, where {@link #init()} can be called
 * whenever the state needs to be reset.
 * <p>Created by Jeff Gaynor<br>
 * on May 10, 2010 at  8:29:51 AM
 */
public interface Initializable {
    /**
     * Destroy the object completely. Returns <code>true</code> if the object existed before destroy was called.
     * Further calls to this object must fail after this invocation.
     */
    public boolean destroy();

    /**
     * Initialize an existing object. This throws an exception if the object does not exist. The state after this
     * call is exactly as if the system were created for the first time. Calls to the object before invocation have
     * no guarantee of success.
     *
     * @return Returns True if the operation succeeds.
     */
    public boolean init();

    /**
     * Creates a completely new instance. Fails if an instance already exists. In that case, call destroy first.
     * For instance, this might create all file system entries or drop then recreate all tables
     * in an SQL database. Compare this with init which might delete any entries in a file store
     * or SQL table.
     *
     * @return
     */
    public boolean createNew();

    /**
     * (Optional) Returns true if the object in question has been created.
     * If this cannot be determined then the call should throw an exception.
     *
     * @return
     */
    public boolean isCreated();

    /**
     * (Optional) Returns true if the object in question has been initialized.
     * If this cannot be determined this call should throw an exception.
     *
     * @return
     */
    public boolean isInitialized();

    /**
     * (Optional) Returns true if the object in question has been destroyed.
     * If this cannot be determined this call should throw an exception.
     *
     * @return
     */
    public boolean isDestroyed();
}
