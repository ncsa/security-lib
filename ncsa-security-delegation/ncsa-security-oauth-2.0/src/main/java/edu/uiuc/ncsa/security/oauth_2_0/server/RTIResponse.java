package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;
import edu.uiuc.ncsa.security.oauth_2_0.OA2Constants;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/14 at  10:27 AM
 */
public class RTIResponse extends IResponse2 {
    public RTIResponse() {
    }

    public RTIResponse(AccessToken accessToken, RefreshToken refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    AccessToken accessToken;
    RefreshToken refreshToken;

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public void write(HttpServletResponse response) throws IOException {
        OutputStream out = response.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(out);
        Map m = new HashMap();
        m.put(OA2Constants.ACCESS_TOKEN ,getAccessToken().getToken());
        m.put(OA2Constants.TOKEN_TYPE,"Bearer");// as per spec
        m.put(OA2Constants.REFRESH_TOKEN,getRefreshToken().getToken());
        m.put(OA2Constants.EXPIRES_IN,(getRefreshToken().getExpiresIn()/1000));
        JSONObject jsonObject = JSONObject.fromObject(m);

        jsonObject.write(osw);
        osw.flush();
        osw.close();
    }


}
