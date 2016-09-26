package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.delegation.server.request.ATResponse;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.Verifier;
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
 * on May 13, 2011 at  1:18:51 PM
 */
public class ATResponseImpl implements ATResponse {
    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    Map<String, String> parameters;

    AccessToken accessToken;
    Verifier verifier;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public Verifier getVerifier() {
        return verifier;
    }

    public void setVerifier(Verifier verifier) {
        this.verifier = verifier;
    }

    public void write(HttpServletResponse response) {
        response.setContentType(OAuthConstants.FORM_ENCODING);
        try {
            OutputStream out = response.getOutputStream();
            List<OAuth.Parameter> list;
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(OAuth.OAUTH_TOKEN);
            arrayList.add(getAccessToken().getToken());
            arrayList.add(OAuth.OAUTH_TOKEN_SECRET);
            arrayList.add(getAccessToken().getSharedSecret());
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
        } catch (IOException e) {
            throw new GeneralException("Error writing to output stream", e);
        }

    }
}
