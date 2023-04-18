package edu.uiuc.ncsa.sas.storage;

import edu.uiuc.ncsa.sas.client.SASClient;
import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.storage.MemoryStore;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/22/22 at  2:09 PM
 */
public class ClientMemoryStore<V extends SASClient> extends MemoryStore<V> implements SASClientStore<V> {
    public ClientMemoryStore(IdentifiableProvider<V> identifiableProvider) {
        super(identifiableProvider);
    }

    @Override
    public XMLConverter<V> getXMLConverter() {
        return getMapConverter();
    }

    @Override
    public List<V> getMostRecent(int n, List<String> attributes) {
        return null;
    }
}
