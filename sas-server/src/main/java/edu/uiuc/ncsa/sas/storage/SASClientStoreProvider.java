package edu.uiuc.ncsa.sas.storage;

import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.configuration.provider.MultiTypeProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:01 PM
 */
public class SASClientStoreProvider<T extends SASClientStore> extends MultiTypeProvider<T> {
    public SASClientStoreProvider() {
    }

    public SASClientStoreProvider(ConfigurationNode config, boolean disableDefaultStore, MyLoggingFacade logger, String type, String target,
                                  IdentifiableProvider<? extends SASClient> clientProvider) {
        super(config, disableDefaultStore, logger, type, target);
        this.clientProvider = clientProvider;
    }

    public SASClientStoreProvider(CFNode config, boolean disableDefaultStore, MyLoggingFacade logger, String type, String target,
                                  IdentifiableProvider<? extends SASClient> clientProvider) {
        super(config, disableDefaultStore, logger, type, target);
        this.clientProvider = clientProvider;
    }


    protected IdentifiableProvider<? extends SASClient> clientProvider;


    @Override
    public T getDefaultStore() {
        return (T) new ClientMemoryStore(clientProvider);
    }
}
