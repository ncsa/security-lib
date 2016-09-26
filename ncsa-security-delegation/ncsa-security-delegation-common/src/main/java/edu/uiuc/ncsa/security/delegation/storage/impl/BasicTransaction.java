package edu.uiuc.ncsa.security.delegation.storage.impl;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.cache.Cacheable;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableImpl;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.delegation.token.ProtectedAsset;
import edu.uiuc.ncsa.security.delegation.token.Verifier;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkEquals;

/**
 * A bean holding a transaction.
 * <p>Created by Jeff Gaynor<br>
 * on May 3, 2011 at  3:28:17 PM
 */
public class BasicTransaction extends IdentifiableImpl implements Cacheable {

    public BasicTransaction(Identifier identifier) {
        super(identifier);
    }

    public BasicTransaction(AuthorizationGrant ag) {
        super(BasicIdentifier.newID(ag.getToken()));
        setAuthorizationGrant(ag);
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean hasAuthorizationGrant() {
        return authorizationGrant != null;
    }

    public boolean hasProtectedAsset() {
        return protectedAsset != null;
    }

    public boolean hasVerifier() {
        return verifier != null;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public AuthorizationGrant getAuthorizationGrant() {
        return authorizationGrant;
    }

    public void setAuthorizationGrant(AuthorizationGrant authorizationGrant) {
        this.authorizationGrant = authorizationGrant;
        setIdentifier(BasicIdentifier.newID(authorizationGrant.getToken()));
    }


    public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }

    AuthorizationGrant authorizationGrant;
    AccessToken accessToken;
    Verifier verifier;
    ProtectedAsset protectedAsset;


    public ProtectedAsset getProtectedAsset() {
        return protectedAsset;
    }

    public void setProtectedAsset(ProtectedAsset protectedAsset) {
        this.protectedAsset = protectedAsset;
    }

    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof BasicTransaction)) return false;
        BasicTransaction t = (BasicTransaction) obj;
        if (!checkEquals(getAuthorizationGrant(), t.getAuthorizationGrant())) return false;
        if (!checkEquals(getAccessToken(), t.getAccessToken())) return false;
        if (!checkEquals(getVerifier(), t.getVerifier())) return false;
        return true;
    }


    public String toString() {
        String out = "Transaction[";
        out = out + "id=" + getIdentifierString() + ", auth grant=" + (hasAuthorizationGrant() ? getAuthorizationGrant() : "(none)");
        out = out + ", verifier=" + (!hasVerifier() ? "(none)" : getVerifier());
        out = out + ", access token=" + (hasAccessToken() ? getAccessToken() : "(none)");
        return out + "]";
    }


}
