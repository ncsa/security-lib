package edu.uiuc.ncsa.security.delegation.server.issuers;

import edu.uiuc.ncsa.security.delegation.server.request.PARequest;
import edu.uiuc.ncsa.security.delegation.server.request.PAResponse;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/13 at  3:35 PM
 */
public interface PAIssuer extends DoubleDispatchServer {
    public PAResponse processProtectedAsset(PARequest paRequest);
}
