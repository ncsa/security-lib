package edu.uiuc.ncsa.security.delegation.storage.impl;

import edu.uiuc.ncsa.security.core.IdentifiableProvider;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.TokenForge;
import edu.uiuc.ncsa.security.delegation.token.Verifier;
import edu.uiuc.ncsa.security.storage.data.ConversionMap;
import edu.uiuc.ncsa.security.storage.data.MapConverter;
import edu.uiuc.ncsa.security.storage.data.SerializationKeys;

/**
 * A map converter bridging the gap between the interface and the backing store.
 * <p>Created by Jeff Gaynor<br>
 * on 4/13/12 at  3:08 PM
 */
public class BasicTransactionConverter<V extends BasicTransaction> extends MapConverter<V> {
    public TokenForge getTokenForge() {
        return tokenForge;
    }

    protected TokenForge tokenForge;

    protected BasicTransactionKeys getBTKeys() {
        return (BasicTransactionKeys) keys;
    }

    public BasicTransactionConverter(IdentifiableProvider<V> identifiableProvider, TokenForge tokenForge) {
        this(new BasicTransactionKeys(), identifiableProvider, tokenForge);

    }

    public BasicTransactionConverter(SerializationKeys keys, IdentifiableProvider<V> identifiableProvider, TokenForge tokenForge) {
        super(keys, identifiableProvider);
        this.tokenForge = tokenForge;
    }

    @Override
    public V fromMap(ConversionMap<String, Object> data, V v) {

        BasicTransaction b = super.fromMap(data, v); // this sets the temp token
        Object token = data.get(getBTKeys().tempCred());
        if (token != null) {
            if (token instanceof AuthorizationGrant) {
                b.setAuthorizationGrant((AuthorizationGrant) token);
            } else {
                b.setAuthorizationGrant(tokenForge.getAuthorizationGrant(token.toString()));
            }
        }

        token = data.get(getBTKeys().accessToken());
        if (token != null) {
            if (token instanceof AccessToken) {
                b.setAccessToken((AccessToken) token);
            } else {
                b.setAccessToken(tokenForge.getAccessToken(token.toString()));
            }
        }



        token = data.get(getBTKeys().verifier());
        if (token != null) {
            if (token instanceof Verifier) {
                b.setVerifier((Verifier) token);
            } else {
                b.setVerifier(tokenForge.getVerifier(token.toString()));
            }
        }


        return (V) b;
    }

    @Override
    public void toMap(V value, ConversionMap<String, Object> data) {
        super.toMap(value, data);
        if (value.hasAuthorizationGrant()) {
            data.put(getBTKeys().tempCred(), value.getAuthorizationGrant().getToken());
        }
        if (value.hasAccessToken()) {
            data.put(getBTKeys().accessToken(), value.getAccessToken().getToken());
        }
        if (value.hasVerifier()) {
            data.put(getBTKeys().verifier(), value.getVerifier().getToken());
        }
    }
}
