package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.core.Identifier;
import edu.uiuc.ncsa.security.core.util.IdentifiableProviderImpl;
import edu.uiuc.ncsa.security.core.util.IdentifierProvider;

import java.util.Date;

import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants.RSA_SHA1;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/3/12 at  3:06 PM
 */
public class OAClientProvider extends IdentifiableProviderImpl<OAClient> {
    public OAClientProvider(IdentifierProvider<Identifier> idProvider) {
        super(idProvider);
    }

    @Override
    public OAClient get(boolean createNewIdentifier) {
        OAClient c = new OAClient(createNewId(createNewIdentifier));
        c.setCreationTS(new Date());
        c.setSignatureMethod(RSA_SHA1);
        return c;
    }
}
