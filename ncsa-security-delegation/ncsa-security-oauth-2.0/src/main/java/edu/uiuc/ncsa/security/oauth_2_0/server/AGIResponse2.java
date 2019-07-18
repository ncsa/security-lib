package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.ServiceTransaction;
import edu.uiuc.ncsa.security.delegation.server.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Constants;
import edu.uiuc.ncsa.security.servlet.ServletDebugUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;


/**
 * Authorization grant response from authorization
 * endpoint on server
 * <p>Created by Jeff Gaynor<br>
 * on 6/4/13 at  5:06 PM
 */
public class AGIResponse2 extends IResponse2 implements AGResponse {
    public AGIResponse2(boolean isOIDC) {
        super(isOIDC);
    }

    public Client getClient() {
        return getServiceTransaction().getClient();
    }


    ServiceTransaction serviceTransaction;

    public ServiceTransaction getServiceTransaction() {
        return serviceTransaction;
    }

    public void setServiceTransaction(ServiceTransaction serviceTransaction) {
        this.serviceTransaction = serviceTransaction;
    }

    /**
     * Getter for grant
     *
     * @return Authorization grant object
     */
    public AuthorizationGrant getGrant() {
        return grant;
    }

    AuthorizationGrant grant;

    /**
     * Setter for grant
     *
     * @param grant Authorization grant object
     */
    public void setGrant(AuthorizationGrant grant) {
        this.grant = grant;
    }

    /**
     * Setter for grant
     *
     * @param parameters Map of parameters
     */
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    /**
     * Write the appropriate auth response
     *
     * @param response Response object to write (using OutputStream)
     */
    public void write(HttpServletResponse response) throws IOException {
        JSONObject m = new JSONObject();
        m.put(OA2Constants.AUTHORIZATION_CODE, grant.getToken());
        if (parameters.get(OA2Constants.STATE) != null && 0 < parameters.get(OA2Constants.STATE).length()) {
            m.put(OA2Constants.STATE, parameters.get(OA2Constants.STATE));
        }
        OA2ClientScopes clientScopes = (OA2ClientScopes) getClient();  // can't be null
        OA2TransactionScopes transactionScopes = (OA2TransactionScopes) getServiceTransaction(); // can be null, since the scope parameter is optional.
        if(clientScopes.getScopes() == null || clientScopes.getScopes().isEmpty()){
            ServletDebugUtil.trace(this, "Client scopes null or empty:" + clientScopes);
        }
         if(transactionScopes.getScopes() == null || transactionScopes.getScopes().isEmpty()){

             ServletDebugUtil.trace(this, "Transaction scopes null or empty = " + transactionScopes);
         }else {
             // CIL-493 followup. If a subset of scopes is requested, the spec says we return a list of the ones we granted.
             if (clientScopes.getScopes().size() != transactionScopes.getScopes().size()) {
                 ServletDebugUtil.trace(this, "returning reduced set of scopes. Stored =" + clientScopes.getScopes() + ", returned =" + transactionScopes.getScopes());
                 // we have to add a scopes parameter to the reponse.
                 JSONArray scopeArray = new JSONArray();
                 scopeArray.addAll(transactionScopes.getScopes());
                 m.put(OA2Constants.SCOPE, scopeArray);
                 ServletDebugUtil.trace(this, "returned scopes = " + scopeArray);
             } else {
                 ServletDebugUtil.trace(this, "Full set of requested scopes requested.");
             }
         }
       // JSONObject jsonObject = JSONObject.fromObject(m);
        Writer osw = response.getWriter();
        ServletDebugUtil.trace(this, "Returning JSON object " + m.toString(2));
        m.write(osw);
        osw.flush();
        osw.close();
    }

}
