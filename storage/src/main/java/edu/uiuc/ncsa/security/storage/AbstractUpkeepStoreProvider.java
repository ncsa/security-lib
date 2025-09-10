package edu.uiuc.ncsa.security.storage;

import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfigUtils;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * Centralizes the upkeep facilities for a store,
 */
public abstract class AbstractUpkeepStoreProvider<T extends Store> extends TypedProvider<T> {
    public AbstractUpkeepStoreProvider(ConfigurationNode config, String type, String target) {
        super(config, type, target);
    }

    public AbstractUpkeepStoreProvider(CFNode cfNode, String type, String target) {
        super(cfNode, type, target);
    }

    public AbstractUpkeepStoreProvider() {
    }

    public AbstractUpkeepStoreProvider(String type, String target) {
        super(type, target);
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
            }catch (Throwable t){
                System.err.println("could not load configuration for " + getClass().getSimpleName() + ":" + t.getMessage());
                throw t;
            }
        }
    }

    @Override
    public void setCFNode(CFNode cfNode) {
        super.setCFNode(cfNode);
        CFNode upkeepNode = getCFNode().getFirstNode(UpkeepConfigUtils.UPKEEP_TAG);
        if (upkeepNode != null) {
            try {
                upkeepConfiguration = UpkeepConfigUtils.processUpkeep(getCFNode().getFirstNode( UpkeepConfigUtils.UPKEEP_TAG));
            }catch (Throwable t){
                System.err.println("could not load configuration for " + getClass().getSimpleName() + ":" + t.getMessage());
                throw t;
            }
        }
    }

}
