package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.delegation.server.request.AGResponse;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AuthorizationGrant;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;
import net.oauth.OAuth;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  1:13:53 PM
 */
public class AGResponseImpl implements AGResponse {
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    Client client;
    public AuthorizationGrant getGrant() {
        return grant;
    }

    public void setGrant(AuthorizationGrant grant) {
        this.grant = grant;
    }

    AuthorizationGrant grant;

    public void write(HttpServletResponse response) throws IOException {
        response.setContentType(OAuthConstants.FORM_ENCODING);
        OutputStream out = response.getOutputStream();
        List<OAuth.Parameter> list;
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add(OAuth.OAUTH_TOKEN);
        arrayList.add(getGrant().getToken());
        arrayList.add(OAuth.OAUTH_TOKEN_SECRET);
        arrayList.add(getGrant().getSharedSecret());
        for (String k : getParameters().keySet()) {
            if (!k.startsWith("oauth_")) {
                arrayList.add(k);
                arrayList.add(getParameters().get(k));
            }
        }
        list = OAuth.newList(arrayList.toArray(new String[arrayList.size()]));
        OAuth.formEncode(list, out);
        out.flush();
        out.close();
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    Map<String, String> parameters;
}
