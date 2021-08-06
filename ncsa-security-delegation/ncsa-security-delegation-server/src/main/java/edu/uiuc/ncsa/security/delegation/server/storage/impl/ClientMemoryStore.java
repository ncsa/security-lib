package edu.uiuc.ncsa.security.delegation.server.storage.impl;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.XMLConverter;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientApprovalStore;
import edu.uiuc.ncsa.security.delegation.server.storage.ClientStore;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.storage.impl.ClientConverter;
import edu.uiuc.ncsa.security.storage.MemoryStore;
import edu.uiuc.ncsa.security.storage.data.MapConverter;

import java.util.Date;
import java.util.List;

/**  Abstract class that gets the inheritance and generics right.
 * <p>Created by Jeff Gaynor<br>
 * on 1/18/12 at  11:12 AM
 */
public  class ClientMemoryStore<V extends Client> extends MemoryStore<V> implements ClientStore<V> {
    public ClientMemoryStore(IdentifiableProvider<V> vIdentifiableProvider) {
        super(vIdentifiableProvider);
    }

    @Override
    public XMLConverter<V> getXMLConverter() {
        return getMapConverter();
    }
    public MapConverter<V> getMapConverter() {
        return new ClientConverter(this.identifiableProvider);
    }

/*    @Override
    public IdentifiableProvider getACProvider() {
        return this.identifiableProvider;
    }*/

    @Override
    public void save(V value) {
        value.setLastModifiedTS(new java.sql.Timestamp(new Date().getTime()));
        super.save(value);
    }

    @Override
    public List<Identifier> getByStatus(String status, ClientApprovalStore clientApprovalStore) {
        throw new NotImplementedException();
    }
}
