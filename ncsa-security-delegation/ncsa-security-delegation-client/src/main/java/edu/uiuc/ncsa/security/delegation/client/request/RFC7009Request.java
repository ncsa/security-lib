package edu.uiuc.ncsa.security.delegation.client.request;

import edu.uiuc.ncsa.security.delegation.client.server.RFC7009Server;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/19/21 at  6:29 AM
 */
public class RFC7009Request extends RFC7662Request{
    @Override
    public Response process(Server server) {
         if (server instanceof RFC7009Server) {
             return ((RFC7009Server) server).processRFC7009Request(this);
         }
         return super.process(server);
     }
}
