package edu.uiuc.ncsa.sat.storage;

import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.configuration.provider.MultiTypeProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:01 PM
 */
public class SATClientStoreProvider<T extends SATClientStore > extends MultiTypeProvider<T> {
    public SATClientStoreProvider() {
    }

    public SATClientStoreProvider(ConfigurationNode config, boolean disableDefaultStore, MyLoggingFacade logger, String type, String target,
                                  IdentifiableProvider<? extends SATClient> clientProvider) {
        super(config, disableDefaultStore, logger, type, target);
        this.clientProvider = clientProvider;
    }

/*
    public SATClientStoreProvider(MyLoggingFacade logger, String type, String target) {
        super(logger, type, target);
    }
*/

    protected IdentifiableProvider<? extends SATClient> clientProvider;


    @Override
    public T getDefaultStore() {
        return (T) new ClientMemoryStore(clientProvider);
    }
}
