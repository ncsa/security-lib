package edu.uiuc.ncsa.security.oauth_2_0.server;

import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.RefreshToken;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/26/14 at  10:27 AM
 */
public class RTIResponse extends IDTokenResponse {


    public RTIResponse(AccessToken accessToken, RefreshToken refreshToken) {
        super(accessToken, refreshToken);
    }


/*
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
        response.setContentType("application/json");

        jsonObject.write(osw);
        osw.flush();
        osw.close();
    }*/


}
