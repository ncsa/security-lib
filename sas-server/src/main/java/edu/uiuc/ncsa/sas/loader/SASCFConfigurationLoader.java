package edu.uiuc.ncsa.sas.loader;

import edu.uiuc.ncsa.sas.SASEnvironment;
import edu.uiuc.ncsa.sas.client.ClientConverter;
import edu.uiuc.ncsa.sas.client.ClientKeys;
import edu.uiuc.ncsa.sas.client.ClientProvider;
import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.sas.storage.ClientMemoryStore;
import edu.uiuc.ncsa.sas.storage.SASClientStore;
import edu.uiuc.ncsa.sas.storage.SASClientStoreProvider;
import edu.uiuc.ncsa.sas.thing.action.ActionDeserializer;
import edu.uiuc.ncsa.sas.thing.response.ResponseSerializer;
import edu.uiuc.ncsa.security.core.Store;
import edu.uiuc.ncsa.security.core.cf.CFNode;
import edu.uiuc.ncsa.security.core.configuration.provider.CfgEvent;
import edu.uiuc.ncsa.security.core.configuration.provider.TypedProvider;
import edu.uiuc.ncsa.security.core.util.MyLoggingFacade;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.storage.CFDBConfigLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  1:00 PM
 */
public class SASCFConfigurationLoader<T extends SASEnvironment> extends CFDBConfigLoader<T> {

    public SASCFConfigurationLoader(CFNode node, MyLoggingFacade logger) {
        super(node, logger);
    }

    public SASCFConfigurationLoader(CFNode node) {
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
        T t = (T) new SASEnvironment(loggerProvider.get(),
                (Store<? extends SASClient>) getCSP().get(),
                new ActionDeserializer(),
                new ResponseSerializer(),
                getAccessList());
        return t;
    }

    List<String> accessList = null;
    protected List<String> getAccessList(){
        if(accessList == null){
                    accessList = new ArrayList<>();
            String raw = cn.getFirstAttribute("accessList");
            if(!StringUtils.isTrivial(raw)){
                StringTokenizer stringTokenizer = new StringTokenizer(raw, ",");
                while(stringTokenizer.hasMoreTokens()){
                    accessList.add(stringTokenizer.nextToken().trim());
                }
            }
        }
        return accessList;
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
        return "SAS Configuration loader version: " + VERSION_NUMBER;
    }

    SASClientStoreProvider csp;

    public ClientProvider getClientProvider() {
        if (clientProvider == null) {
            clientProvider = new ClientProvider();
        }
        return clientProvider;
    }

    ClientProvider clientProvider;

    protected SASClientStoreProvider getCSP() {
        if (csp == null) {
            ClientConverter converter = new ClientConverter(new ClientKeys(), getClientProvider());
            csp = new SASClientStoreProvider(cn, isDefaultStoreDisabled(), loggerProvider.get(), null, null, getClientProvider());

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
            csp.addListener(new TypedProvider<SASClientStore>(cn, "memory", "clients") {

                @Override
                public Object componentFound(CfgEvent configurationEvent) {
                    if (checkEvent(configurationEvent)) {
                        return get();
                    }
                    return null;
                }

                @Override
                public SASClientStore get() {
                    return new ClientMemoryStore(getClientProvider());
                }
            });
        }
        return csp;
    }

}
