package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import edu.uiuc.ncsa.security.core.exceptions.MyConfigurationException;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfigUtils;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  3:30 PM
 */
public abstract class MemoryStoreProvider<T extends Store> extends TypedProvider<T> {
    public MemoryStoreProvider(ConfigurationNode config, String target) {
        super(config, StorageConfigurationTags.MEMORY_STORE, target);
    }

    public MemoryStoreProvider() {
    }

    public MemoryStoreProvider(String target) {
        super(StorageConfigurationTags.MEMORY_STORE, target);
    }

    public UpkeepConfiguration getUpkeepConfiguration() {
        return upkeepConfiguration;
    }

    public void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration) {
        this.upkeepConfiguration = upkeepConfiguration;
    }

    UpkeepConfiguration upkeepConfiguration;

    @Override
    public void setConfig(ConfigurationNode config) {
        super.setConfig(config);
        ConfigurationNode upkeepNode = Configurations.getFirstNode(getConfig(), UpkeepConfigUtils.UPKEEP_TAG);
        if (upkeepNode != null) {
            try {
                upkeepConfiguration = UpkeepConfigUtils.processUpkeep(Configurations.getFirstNode(getConfig(), UpkeepConfigUtils.UPKEEP_TAG));
            } catch (Throwable t) {
                System.err.println("could not load configuration for " + getClass().getSimpleName() + ":" + t.getMessage());
                throw t;
            }
        }
    }
}
