package edu.uiuc.ncsa.security.core.util;

/**
 * For loading a configuration into an environment. .
 * <p>Created by Jeff Gaynor<br>
 * on 3/19/12 at  4:39 PM
 */
public interface ConfigurationLoader<T extends AbstractEnvironment> {

    /**
     * load the configuration. Normally the specifics of the configuration (e.g. a property
     * file or an XML node) are loaded in the constructor first before this call. This is
     * called automatically by the bootstrapper.
     * @return
     * @throws Exception
     */
    public T load();


    /**
     * Internal call to Create the service environment from the initialized state of this boot strapper.
     * @return
     * @throws Exception
     */
    public T createInstance();
}
