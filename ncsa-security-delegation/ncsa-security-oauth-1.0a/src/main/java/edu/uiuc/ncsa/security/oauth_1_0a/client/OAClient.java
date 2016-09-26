package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;

import static edu.uiuc.ncsa.security.core.util.BeanUtils.checkEquals;

/**
 * Models an OAuth 1.0a client. really just sets the signature method.
 * <p>Created by Jeff Gaynor<br>
 * on Mar 16, 2011 at  12:25:56 PM
 */

public class OAClient extends Client {

    @Override
    public OAClient clone()  {
        OAClient client = new OAClient(getIdentifier());
        client.setSecret(getSecret());
        client.setProxyLimited(isProxyLimited());
        client.setName(getName());
        client.setHomeUri(getHomeUri());
        client.setErrorUri(getErrorUri());
        client.setEmail(getEmail());
        client.setSignatureMethod(getSignatureMethod());
        client.setCreationTS(getCreationTS());
        return client;
    }

    public OAClient(Identifier identifier) {
        super(identifier);
    }

    public String getSignatureMethod() {
        return signatureMethod;
    }


    String signatureMethod;

    static final long serialVersionUID = 0xcafed00d1L;

    public void setSignatureMethod(String signatureMethod) {
        if (!signatureMethod.equals(OAuthConstants.RSA_SHA1)) {
            throw new GeneralException("Error: unsupported signature method. Only " + OAuthConstants.RSA_SHA1 + " is supported. Found " + signatureMethod);
        }
        this.signatureMethod = signatureMethod;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        OAClient c = (OAClient) obj;
        if (!checkEquals(getSignatureMethod(), c.getSignatureMethod())) return false;
        return true;
    }

    @Override
    public String toString() {
        String x = super.toString();
        x = x.substring(0, x.lastIndexOf("]"));
        x = x + ", sig. method=" + getSignatureMethod() + "]";
        return x;
    }
}
