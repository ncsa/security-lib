package edu.uiuc.ncsa.security.delegation.server.issuers;

import edu.uiuc.ncsa.security.delegation.server.request.ATRequest;
import edu.uiuc.ncsa.security.delegation.server.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/13 at  3:34 PM
 */
public interface ATIssuer extends DoubleDispatchServer{
    public abstract ATResponse processATRequest(ATRequest accessTokenRequest);

}
