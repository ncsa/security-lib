package edu.uiuc.ncsa.security.delegation.server.issuers;

import edu.uiuc.ncsa.security.delegation.server.request.CBRequest;
import edu.uiuc.ncsa.security.delegation.server.request.CBResponse;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/13 at  3:35 PM
 */
public interface CBIssuer extends DoubleDispatchServer {
    public abstract CBResponse processCallbackRequest(CBRequest CBRequest);
}
