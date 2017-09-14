package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.delegation.storage.BaseClient;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.oauth_2_0.server.config.LDAPConfiguration;

import java.util.Collection;
import java.util.LinkedList;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkEquals;

/**
 * OAuth2 Open ID connect protocol requires that sites register callback uris and that incoming requests
 * must include a callback that matches one of the registered ones.
 * <p>Created by Jeff Gaynor<br>
 * on 3/14/14 at  11:04 AM
 */
public class OA2Client extends Client {
    @Override
    public OA2Client clone() {
        OA2Client client = new OA2Client(getIdentifier());
        populateClone(client);
        return client;
    }

    @Override
    protected void populateClone(BaseClient c) {
        OA2Client client = (OA2Client) c;
        super.populateClone(client);
        client.setRtLifetime(getRtLifetime());
        client.setCallbackURIs(getCallbackURIs());
        client.setScopes(getScopes());
        client.setLdaps(getLdaps());
        client.setIssuer(getIssuer());
        client.setSignTokens(isSignTokens());
    }
    public boolean isPublicClient(){
       return publicClient;
    }
    public void setPublicClient(boolean publicClient){
        this.publicClient = publicClient;
    }
    protected boolean publicClient = false;

    public boolean isSignTokens() {
        return signTokens;
    }

    public void setSignTokens(boolean signTokens) {
        this.signTokens = signTokens;
    }

    boolean signTokens = true; // new default as of version 3.4. Fixes CIL-405
    String issuer = null;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public OA2Client(Identifier identifier) {
        super(identifier);
    }

    public Collection<String> getCallbackURIs() {
        return callbackURIs;
    }

    public void setCallbackURIs(Collection<String> callbackURIs) {
        this.callbackURIs = callbackURIs;
    }

    Collection<String> callbackURIs = new LinkedList<>();

    long rtLifetime = 0L;

    public long getRtLifetime() {
        return rtLifetime;
    }

    public void setRtLifetime(long rtLifetime) {
        this.rtLifetime = rtLifetime;
    }

    public boolean isRTLifetimeEnabled() {
        return 0 < rtLifetime ;
    }

    public Collection<String> getScopes() {
        return scopes;
    }

    public void setScopes(Collection<String> scopes) {
        this.scopes = scopes;
    }

    Collection<String> scopes = new LinkedList<>();

    public Collection<LDAPConfiguration> getLdaps() {
        return ldaps;
    }

    public void setLdaps(Collection<LDAPConfiguration> ldaps) {
        this.ldaps = ldaps;
    }

    Collection<LDAPConfiguration> ldaps;

    @Override
    public String toString() {
        String x = super.toString();
        x = x.substring(0, x.lastIndexOf("]"));
        x = x + "scopes=" + ((getScopes() == null) ? "[]" : getScopes().toString()) + ",";
        x = x + "callbacks=" + (getCallbackURIs() == null ? "[]" : getCallbackURIs().toString()) + ",";
        x = x + "refresh token lifetime=" + getRtLifetime();
        x = x + "sign ID tokens?=" + isSignTokens();
        return x + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof OA2Client)) return false;
        OA2Client c = (OA2Client)obj;
        if (getRtLifetime() != c.getRtLifetime()) return false;
        if (!checkEquals(getIssuer(), c.getIssuer())) return false;

        if (getScopes().size() != c.getScopes().size()) return false;
        for(String x : getScopes()){
            if(!c.getScopes().contains(x)) return false;
        }

        if(getCallbackURIs().size() != c.getCallbackURIs().size()) return false;
        for(String x : getCallbackURIs()){
            if(!c.getCallbackURIs().contains(x)) return false;
        }
        if(isSignTokens() != c.isSignTokens()) return false;
        return super.equals(obj);
    }
}
