package edu.uiuc.ncsa.security.delegation.server.request;

import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.delegation.services.Request;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.services.Server;
import edu.uiuc.ncsa.security.delegation.storage.Client;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  11:57:57 AM
 */
public class IssuerRequest implements Request {
    public IssuerRequest(Client client) {
        this.client = client;
    }

    public IssuerRequest(HttpServletRequest servletRequest, Client client) {
        this.client = client;
        this.servletRequest = servletRequest;
    }

    public Response process(Server server) {
        throw new NotImplementedException();
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }


    HttpServletRequest servletRequest;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    Client client;
}
