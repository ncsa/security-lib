package edu.uiuc.ncsa.sas.client;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.StringUtils;

import java.util.UUID;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  9:39 AM
 */
public class ClientProvider<V extends SATClient> implements IdentifiableProvider<V>, SASConstants {
    @Override
    public V get(boolean createNewIdentifier) {
        Identifier id = null;
        if(createNewIdentifier){
                 id = new BasicIdentifier(CLIENT_ID_HEAD + UUID.randomUUID() + "/" + System.currentTimeMillis());
        }
        return (V) new SATClient(id);
    }

    @Override
    public V get() {
        return get(true);
    }

    public static boolean isClientID(String x){
        if(StringUtils.isTrivial(x)) return false;
        return x.startsWith(CLIENT_ID_HEAD);
    }
}
