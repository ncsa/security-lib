package edu.uiuc.ncsa.security.storage.monitored;

import edu.uiuc.ncsa.security.core.Identifiable;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.AbstractEnvironment;
import edu.uiuc.ncsa.security.storage.MonitoredStoreDelegate;
import edu.uiuc.ncsa.security.storage.FileStore;
import edu.uiuc.ncsa.security.storage.MonitoredStoreInterface;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.events.IDMap;
import edu.uiuc.ncsa.security.storage.events.LastAccessedEventListener;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepResponse;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/29/23 at  10:00 AM
 */
public abstract class MonitoredFileStore<V extends Identifiable> extends FileStore<V> implements MonitoredStoreInterface<V> {
    public MonitoredFileStore(File storeDirectory,
                              File indexDirectory,
                              IdentifiableProvider<V> identifiableProvider,
                              MapConverter<V> converter,
                              boolean removeEmptyFiles,
                              boolean removeFailedFiles) {
        super(storeDirectory, indexDirectory, identifiableProvider, converter, removeEmptyFiles, removeFailedFiles);
    }

    public MonitoredFileStore(File directory, IdentifiableProvider<V> idp, MapConverter<V> cp, boolean removeEmptyFiles,
                              boolean removeFailedFiles) {
        super(directory, idp, cp, removeEmptyFiles, removeFailedFiles);
    }


    MonitoredStoreDelegate<V> monitoredStoreDelegate = new MonitoredStoreDelegate<>();

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }

    @Override
    public List<LastAccessedEventListener> getLastAccessedEventListeners() {
        return monitoredStoreDelegate.getLastAccessedEventListeners();
    }

    @Override
    public UUID getUuid() {
        return monitoredStoreDelegate.getUuid();
    }

    @Override
    public void addLastAccessedEventListener(LastAccessedEventListener lastAccessedEventListener) {
        monitoredStoreDelegate.addLastAccessedEventListener(lastAccessedEventListener);
    }

    @Override
    public void fireLastAccessedEvent(MonitoredStoreInterface store, Identifier identifier) {
        monitoredStoreDelegate.fireLastAccessedEvent(store, identifier);
    }

    @Override
    public boolean isMonitorEnabled() {
        return monitoredStoreDelegate.isMonitorEnabled();
    }

    @Override

    public void setMonitorEnabled(boolean x) {
        monitoredStoreDelegate.setMonitorEnabled(x);
    }

    @Override
    public void lastAccessUpdate(IDMap idMap) {
        for (Identifier id : idMap.keySet()) {
            long lastAccessed = idMap.get(id);
            V v = super.get(id); // use super or a last accessed time event gets fired.
            Monitored monitored = (Monitored) v;
            if (monitored.getLastAccessed().getTime() < lastAccessed) {
                ((Monitored) v).setLastAccessed(new Date(lastAccessed));
                save(v);
            }
        }
    }

    @Override
    public V get(Object key) {
        V v = super.get(key);
        monitoredStoreDelegate.fireLastAccessedEvent(this, (Identifier) key);
        return v;
    }


    @Override
    public void setUpkeepConfiguration(UpkeepConfiguration upkeepConfiguration) {
        monitoredStoreDelegate.setUpkeepConfiguration(upkeepConfiguration);
    }

    @Override
    public UpkeepConfiguration getUpkeepConfiguration() {
        return monitoredStoreDelegate.getUpkeepConfiguration();
    }

    @Override
    public UpkeepResponse doUpkeep(AbstractEnvironment environment) {
        return doUpkeep(getUpkeepConfiguration(), environment);
    }

    @Override
    public UpkeepResponse doUpkeep(UpkeepConfiguration upkeepConfiguration, AbstractEnvironment environment) {
        return null;
    }

    @Override
    public long updateHook(String action, AbstractEnvironment environment, List<Identifier> identifiers) {
        return 0L;
    }

    @Override
    public boolean hasUpkeepConfiguration() {
        return monitoredStoreDelegate.hasUpkeepConfiguration();
    }
}
