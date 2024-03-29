package edu.uiuc.ncsa.security.storage.sql;

import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.configuration.Configurations;
import edu.uiuc.ncsa.security.core.configuration.StorageConfigurationTags;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfigUtils;
import edu.uiuc.ncsa.security.storage.monitored.upkeep.UpkeepConfiguration;
import edu.uiuc.ncsa.security.storage.sql.derby.DerbyConnectionParameters;
import edu.uiuc.ncsa.security.storage.sql.internals.Table;
import org.apache.commons.configuration.tree.ConfigurationNode;

/**
 * For creating SQL-based stores
 * <p>Created by Jeff Gaynor<br>
 * on 1/10/12 at  2:47 PM
 */
public abstract class SQLStoreProvider<T extends Store> extends TypedProvider<T> {

    public String getSchema() {
        if(getConnectionPool().getConnectionParameters() instanceof DerbyConnectionParameters){
            // Special case since file store and memory store may have this overridden
            return ((DerbyConnectionParameters)getConnectionPool().getConnectionParameters()).getSchema();
        }
    return getTypeAttribute(SCHEMA);
    }

    public String getPrefix() {
        return getTypeAttribute(PREFIX);
    }

    String tablename = null;

    /**
     * Return the configured tablename if there is one, otherwise return the default.
     *
     * @return
     */
    public String getTablename() {
        return getAttribute(TABLENAME, tablename);
    }


    protected MapConverter converter;

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getDatabaseName() {
        return getTypeAttribute(StorageConfigurationTags.SQL_DATABASE);
    }

    public static final String TABLENAME = StorageConfigurationTags.SQL_TABLENAME;
    public static final String PREFIX = StorageConfigurationTags.SQL_PREFIX;
    public static final String SCHEMA = StorageConfigurationTags.SQL_SCHEMA;

    protected SQLStoreProvider(ConfigurationNode config,
                               ConnectionPoolProvider<? extends ConnectionPool> cpp,
                               String type,
                               String target,
                               MapConverter converter) {
        super(config, type, target);
        connectionPoolProvider = cpp;
        this.converter = converter;
    }

    protected SQLStoreProvider(ConfigurationNode config,
                               ConnectionPoolProvider<? extends ConnectionPool> cpp,
                               String type,
                               String target,
                               String tablename,
                               MapConverter converter
    ) {
        super(config, type, target);
        connectionPoolProvider = cpp;
        this.tablename = tablename;
        this.converter = converter;
    }


    protected SQLStoreProvider(ConnectionPoolProvider<? extends ConnectionPool> cpp,
                               String type,
                               String target,
                               MapConverter converter) {
        this(null, cpp, type, target, converter);
    }

    protected SQLStoreProvider(ConnectionPoolProvider<? extends ConnectionPool> cpp,
                               String type, String target,
                               String tablename,
                               MapConverter converter) {
        this(null, cpp, type, target, tablename, converter);
    }

    public SQLStoreProvider() {

    }

    @Override
    public Object componentFound(CfgEvent configurationEvent) {
        if (checkEvent(configurationEvent)) {
            return get();
        }
        return null;
    }

    protected SQLStoreProvider(String type, String target) {
        super(type, target);
    }

    ConnectionPoolProvider<? extends ConnectionPool> connectionPoolProvider;

    protected ConnectionPool getConnectionPool() {
        if (connectionPoolProvider.getConfig() == null) {
            connectionPoolProvider.setConfig(getTypeConfig());
        }
        return connectionPoolProvider.get();
    }

    public abstract T newInstance(Table table);

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
