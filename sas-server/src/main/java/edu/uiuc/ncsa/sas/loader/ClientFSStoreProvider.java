package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.sas.storage.FSClientStore;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.storage.FSProvider;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import org.apache.commons.configuration.tree.ConfigurationNode;

import javax.inject.Provider;
import java.io.File;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:25 PM
 */
public class ClientFSStoreProvider<T extends FileStore> extends FSProvider<T> {
    public ClientFSStoreProvider(ConfigurationNode config,  MapConverter converter, Provider<? extends SASClient> clientProvider) {
        super(config, StorageConfigurationTags.FILE_STORE, "clients", converter);
        this.clientProvider= clientProvider;

    }

    Provider<? extends SASClient> clientProvider;

    @Override
    protected T produce(File dataPath, File indexPath, boolean removeEmptyFiles) {
        FSClientStore fsClientStore = new FSClientStore(dataPath, indexPath, (IdentifiableProvider) clientProvider, converter, removeEmptyFiles);
        return (T) fsClientStore;
    }
}
