package edu.uiuc.ncsa.security.delegation.client.server;

import edu.uiuc.ncsa.security.delegation.client.request.RFC7009Request;
import edu.uiuc.ncsa.security.delegation.client.request.RFC7009Response;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 5/19/21 at  6:29 AM
 */
public interface RFC7009Server {
    RFC7009Response processRFC7009Request(RFC7009Request request);
}
