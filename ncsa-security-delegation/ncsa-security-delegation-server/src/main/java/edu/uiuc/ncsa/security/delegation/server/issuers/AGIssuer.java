package edu.uiuc.ncsa.security.delegation.server.issuers;

import edu.uiuc.ncsa.security.delegation.server.request.AGRequest;
import edu.uiuc.ncsa.security.delegation.server.request.IssuerResponse;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/13 at  3:34 PM
 */
public interface AGIssuer extends DoubleDispatchServer {
    public IssuerResponse processAGRequest(AGRequest authorizationGrantRequest);
}
