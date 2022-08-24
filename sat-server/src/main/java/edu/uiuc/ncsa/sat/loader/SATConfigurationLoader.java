package edu.uiuc.ncsa.sat.loader;

import edu.uiuc.ncsa.sat.RequestDeserializer;
import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.sat.client.ClientConverter;
import edu.uiuc.ncsa.sat.client.ClientKeys;
import edu.uiuc.ncsa.sat.client.ClientProvider;
import edu.uiuc.ncsa.sat.client.SATClient;
import edu.uiuc.ncsa.sat.storage.ClientMemoryStore;
import edu.uiuc.ncsa.sat.storage.SATClientStore;
import edu.uiuc.ncsa.sat.storage.SATClientStoreProvider;
import edu.uiuc.ncsa.sat.thing.ResponseSerializer;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.storage.DBConfigLoader;
import org.apache.commons.configuration.tree.ConfigurationNode;

import java.util.HashMap;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  1:00 PM
 */
public class SATConfigurationLoader<T extends SATEnvironment> extends DBConfigLoader<T> {

    public SATConfigurationLoader(ConfigurationNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    public SATConfigurationLoader(ConfigurationNode node) {
        super(node);
    }

    @Override
    public T load() {
        T sate = createInstance();
        // lots of stuff may eventually go here.
        return sate;
    }

    @Override
    public T createInstance() {
        return (T) new SATEnvironment(loggerProvider.get(),
                (Store<? extends SATClient>) getCSP().get(),
                new RequestDeserializer(),
                new ResponseSerializer());

    }

    HashMap<String, String> constants = new HashMap<>();

    /**
     * Not needed here
     *
     * @return
     */
    @Override
    public HashMap<String, String> getConstants() {
        return constants;
    }


    @Override
    public String getVersionString() {
        return "SAT Configuration loader version: " + VERSION_NUMBER;
    }

    SATClientStoreProvider csp;

    public ClientProvider getClientProvider() {
        if (clientProvider == null) {
            clientProvider = new ClientProvider();
        }
        return clientProvider;
    }

    ClientProvider clientProvider;

    protected SATClientStoreProvider getCSP() {
        if (csp == null) {
            ClientConverter converter = new ClientConverter(new ClientKeys(), getClientProvider());
            csp = new SATClientStoreProvider(cn, isDefaultStoreDisabled(), loggerProvider.get(), null, null, getClientProvider());

            csp.addListener(new ClientFSStoreProvider(cn, converter, getClientProvider()));
            csp.addListener(new SQLClientStoreProvider(getMySQLConnectionPoolProvider(),
                    "mysql",
                    converter, getClientProvider()));
            csp.addListener(new SQLClientStoreProvider<>(getMariaDBConnectionPoolProvider(),
                    "mariadb",
                    converter, getClientProvider()));
            csp.addListener(new SQLClientStoreProvider<>(getPgConnectionPoolProvider(),
                    "postgres",
                    converter, getClientProvider()));
            csp.addListener(new SQLClientStoreProvider<>(getDerbyConnectionPoolProvider(),
                    "derby",
                    converter, getClientProvider()));
            csp.addListener(new TypedProvider<SATClientStore>(cn, "memory", "clients") {

                @Override
                public Object componentFound(CfgEvent configurationEvent) {
                    if (checkEvent(configurationEvent)) {
                        return get();
                    }
                    return null;
                }

                @Override
                public SATClientStore get() {
                    return new ClientMemoryStore(getClientProvider());
                }
            });
        }
        return csp;
    }
}
