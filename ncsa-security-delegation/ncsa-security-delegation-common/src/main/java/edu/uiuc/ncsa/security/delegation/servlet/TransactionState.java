package edu.uiuc.ncsa.security.delegation.servlet;

import edu.uiuc.ncsa.security.delegation.storage.impl.BasicTransaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Used by the delegation servlet, this allows a programmer to intercept and process the HTTP
 * request and response. The {@link #getParameters()} call returns the parsed parameters from
 * the request. The {@link #getTransaction()} returns the current transaction (which will
 * probably have to be cast to an appropriate subclass of {@link BasicTransaction} to be useful.
 * Save any changes to the transaction you make. Generally avoid touching the response's
 * output stream.
 * <p>Created by Jeff Gaynor<br>
 * on 4/23/12 at  4:56 PM
 */
public class TransactionState {
    public TransactionState(HttpServletRequest request,
                            HttpServletResponse response,
                            Map<String, String> parameters,
                            BasicTransaction transaction) {
        this.request = request;
        this.response = response;
        this.parameters = parameters;
        this.transaction = transaction;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public BasicTransaction getTransaction() {
        return transaction;
    }

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected BasicTransaction transaction;
    protected Map<String, String> parameters;

}
