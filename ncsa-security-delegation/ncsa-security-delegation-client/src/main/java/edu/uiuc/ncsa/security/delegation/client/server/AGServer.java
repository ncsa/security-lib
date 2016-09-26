package edu.uiuc.ncsa.security.delegation.client.server;

import edu.uiuc.ncsa.security.delegation.client.request.AGRequest;
import edu.uiuc.ncsa.security.delegation.client.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;

/**
 * Interface for servers tasked with issuing authorization grants.
 * <p>Created by Jeff Gaynor<br>
 * on 6/3/13 at  10:44 AM
 */
public interface AGServer extends DoubleDispatchServer {
    public AGResponse processAGRequest(AGRequest acRequest);

}
