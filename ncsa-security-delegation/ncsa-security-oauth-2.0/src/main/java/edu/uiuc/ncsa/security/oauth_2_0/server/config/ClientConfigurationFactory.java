package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import edu.uiuc.ncsa.security.util.functor.JFunctorFactory;
import net.sf.json.JSONObject;

import javax.inject.Provider;
/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/17/18 at  10:55 AM
 */
public class ClientConfigurationFactory<V extends ClientConfiguration> implements Provider<V> {
    public ClientConfigurationFactory(JFunctorFactory functorFactory) {
        this.functorFactory = functorFactory;
    }

    protected JFunctorFactory functorFactory;


    /**
     * Create a new {@link ClientConfiguration}.
     * @param json
     * @return
     */
    public V newInstance(JSONObject json) {
        V cc = get();
        JSONObject j = ClientConfigurationUtil.getRuntime(json);
        cc.setRuntime(functorFactory.createLogicBlock(j));
        return cc;
    }

    @Override
    public V get() {
        return (V) new ClientConfiguration();
    }
}
