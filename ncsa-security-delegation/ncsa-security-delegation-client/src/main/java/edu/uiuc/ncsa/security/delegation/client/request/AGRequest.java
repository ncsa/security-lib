package edu.uiuc.ncsa.security.delegation.client.request;

import edu.uiuc.ncsa.security.delegation.client.server.AGServer;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Apr 13, 2011 at  3:37:26 PM
 */
public class AGRequest extends BasicRequest {
    public Response process(Server server) {
        if (server instanceof AGServer) {
            return ((AGServer) server).processAGRequest(this);
        }
        return super.process(server);
    }
}
