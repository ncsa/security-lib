package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  3:30 PM
 */
public abstract class MemoryStoreProvider<T extends Store> extends AbstractUpkeepStoreProvider<T> {
    public MemoryStoreProvider(ConfigurationNode config, String target) {
        super(config, StorageConfigurationTags.MEMORY_STORE, target);
    }

    public MemoryStoreProvider() {
    }

    public MemoryStoreProvider(String target) {
        super(StorageConfigurationTags.MEMORY_STORE, target);
    }
}
