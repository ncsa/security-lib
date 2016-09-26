package edu.uiuc.ncsa.security.delegation.client.server;

import edu.uiuc.ncsa.security.delegation.client.request.RTRequest;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;
import edu.uiuc.ncsa.security.delegation.services.Response;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/24/14 at  11:20 AM
 */
public interface  RTServer extends DoubleDispatchServer {
    public abstract Response processRTRequest (RTRequest rtRequest) ;
}
