package edu.uiuc.ncsa.security.delegation.client.server;

import edu.uiuc.ncsa.security.delegation.client.request.RFC7662Request;
import edu.uiuc.ncsa.security.delegation.client.request.RFC7662Response;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/19/21 at  6:21 AM
 */
public interface RFC7662Server {
    RFC7662Response processRFC7662Request(RFC7662Request request);
}
