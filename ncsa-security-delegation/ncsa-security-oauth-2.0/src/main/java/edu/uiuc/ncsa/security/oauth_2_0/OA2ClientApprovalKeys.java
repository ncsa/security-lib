package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.delegation.storage.ClientApprovalKeys;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 3/20/14 at  10:07 AM
 */
public class OA2ClientApprovalKeys extends ClientApprovalKeys {
    public OA2ClientApprovalKeys() {
        super();
        identifier("client_id");
    }
}
