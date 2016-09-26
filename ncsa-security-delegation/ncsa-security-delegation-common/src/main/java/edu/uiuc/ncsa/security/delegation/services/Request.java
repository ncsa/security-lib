package edu.uiuc.ncsa.security.delegation.services;

/**
 * General delegation request to a service
 * <p>Created by Jeff Gaynor<br>
 * on Apr 13, 2011 at  3:32:38 PM
 */
public interface Request {
    Response process(Server server);
}
