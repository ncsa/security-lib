package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.server.request.IssuerResponse;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.oauth_2_0.UserInfo;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * User info issuer response
 * <p>Created by Jeff Gaynor<br>
 * on 10/7/13 at  2:38 PM
 */
public class UIIResponse2 implements IssuerResponse {
    AccessToken accessToken;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    // UserInfo field to manage information about user
    private UserInfo userInfo;

    /**
     * Write JSON User Info response to output stream
     *
     * @param response
     * @throws IOException
     */
    @Override
    public void write(HttpServletResponse response) throws IOException {
        Writer writer = response.getWriter();
        createJSON().write(writer);
        writer.flush();
        writer.close();
    }

    /**
     * Override this if needed. The default behavior is to serialize everything in the
     * {@link UserInfo} object.
     * @return
     */
    protected JSONObject createJSON() {
        JSONObject json = new JSONObject();
        if (userInfo != null) {
            json.putAll(userInfo.getMap());
        }
       // System.out.println(getClass().getSimpleName() + ".write: json=" + json);
        return json;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    Map<String, String> parameters;

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }


    /**
     * Getter for UserInfo
     *
     * @return UserInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * Setter for UserInfo
     *
     * @param userInfo
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
