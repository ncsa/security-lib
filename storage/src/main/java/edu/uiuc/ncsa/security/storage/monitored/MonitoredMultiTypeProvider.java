package edu.uiuc.ncsa.security.storage.monitored;

import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEventListener;
import edu.uiuc.ncsa.security.core.configuration.provider.MultiTypeProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.MonitoredStoreInterface;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfigUtils;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/16/24 at  6:35 PM
 */
/*
    Several classes in OA4MP extend this, but it is not used in the security library directly.
 */
public abstract class MonitoredMultiTypeProvider<T> extends MultiTypeProvider<T> {
    public MonitoredMultiTypeProvider() {
    }

    public MonitoredMultiTypeProvider(ConfigurationNode config, boolean disableDefaultStore, MyLoggingFacade logger, String type, String target) {
        super(config, disableDefaultStore, logger, type, target);
    }

    public MonitoredMultiTypeProvider(CFNode config, boolean disableDefaultStore, MyLoggingFacade logger, String type, String target) {
        super(config, disableDefaultStore, logger, type, target);
    }

    public MonitoredMultiTypeProvider(MyLoggingFacade logger, String type, String target) {
        super(logger, type, target);
    }

    public UpkeepConfiguration getUpkeepConfiguration() {

        return upkeepConfiguration;
    }

    public void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration) {
        this.upkeepConfiguration = upkeepConfiguration;
    }

    UpkeepConfiguration upkeepConfiguration;

    @Override
    public void addListener(CfgEventListener c) {
        super.addListener(c);
    }

    /**
     * Solves the bootstrapping issue with upkeep nodes, viz.,
     * <ol>
     *     <li>Listeners for store types are added</li>
     *     <li>The configuration is traversed</li>
     *     <li>Events are fired to listeners as components are found (common XML traversal scheme)</li>
     *     <li>The correct configuration node is finally found in {@link edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider#fireComponentFound(CfgEvent)}
     *     and injected in the provider (this class)</li>
     *     <li>Only then can we determine if there is an upkeep configuration.</li>
     *     <li>Inject it into the other providers since they are not aware of XML nodes.</li>
     * </ol>
     */
    protected void injectUpkeep() {
        if (upkeepConfiguration == null) {
            if(hasCFNode()){
                cfInjectUpkeep();
            }else {
                cnInjectUpkeep();
            }
        }
    }

    private void cfInjectUpkeep() {
        CFNode upkeepNode = getCFNode().getFirstNode( UpkeepConfigUtils.UPKEEP_TAG);
        if (upkeepNode != null) {
            try{
                upkeepConfiguration = UpkeepConfigUtils.processUpkeep(getCFNode().getFirstNode( UpkeepConfigUtils.UPKEEP_TAG));
                for (CfgEventListener c : getListeners()) {
                    if (c instanceof MonitoredStoreInterface) {
                        ((MonitoredStoreInterface) c).setUpkeepConfiguration(getUpkeepConfiguration());
                    }
                }
            }catch(Throwable t){
                System.err.println("could not load up keep configuration for " + getClass().getSimpleName() + ":" + t.getMessage());
                throw t;
            }
        }
    }


    private void cnInjectUpkeep() {
        ConfigurationNode upkeepNode = Configurations.getFirstNode(getConfig(), UpkeepConfigUtils.UPKEEP_TAG);
        if (upkeepNode != null) {
            try{
                upkeepConfiguration = UpkeepConfigUtils.processUpkeep(Configurations.getFirstNode(getConfig(), UpkeepConfigUtils.UPKEEP_TAG));
                for (CfgEventListener c : getListeners()) {
                    if (c instanceof MonitoredStoreInterface) {
                        ((MonitoredStoreInterface) c).setUpkeepConfiguration(getUpkeepConfiguration());
                    }
                }
            }catch(Throwable t){
                System.err.println("could not load up keep configuration for " + getClass().getSimpleName() + ":" + t.getMessage());
                throw t;
            }
        }
    }

    @Override
    public void setConfig(ConfigurationNode config) {
        super.setConfig(config);
        injectUpkeep();
    }
}
