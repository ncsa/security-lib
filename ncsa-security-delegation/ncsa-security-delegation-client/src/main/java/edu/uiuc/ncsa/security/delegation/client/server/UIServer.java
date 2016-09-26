package edu.uiuc.ncsa.security.delegation.client.server;

import edu.uiuc.ncsa.security.delegation.client.request.UIRequest;
import edu.uiuc.ncsa.security.delegation.client.request.UIResponse;
import edu.uiuc.ncsa.security.delegation.services.DoubleDispatchServer;

/**
 * Created with IntelliJ IDEA.
 * User: wedwards
 * Date: 1/30/14
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UIServer extends DoubleDispatchServer {
    public abstract UIResponse processUIRequest (UIRequest uiRequest) ;
}
