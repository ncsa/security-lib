package edu.uiuc.ncsa.security.oauth_1_0a.server;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.delegation.server.request.PAResponse;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.MyX509Certificates;
import edu.uiuc.ncsa.security.delegation.token.ProtectedAsset;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Created by Jeff Gaynor<br>
 * on May 13, 2011 at  1:27:50 PM
 */
public class PAResponseImpl implements PAResponse {

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    Map<String, String> parameters;

    public Map<String, String> getAdditionalInformation() {
        if (additionalInformation == null) {
            additionalInformation = new HashMap<String, String>();
        }
        return additionalInformation;
    }

    public void setAdditionalInformation(Map<String, String> additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    Map<String, String> additionalInformation;

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    AccessToken accessToken;

    public void write(HttpServletResponse response) {

        if (protectedAsset == null) {
            throw new GeneralException("Error, no protected asset =");
        }
        if (!(getProtectedAsset() instanceof MyX509Certificates)) {
            throw new NotImplementedException("Error, this implementation can only serialize MyX509Certificates and a protected asset of type \""
                    + getProtectedAsset().getClass().getName() + "\" was found instead");
        }
        try {
            MyX509Certificates certs = (MyX509Certificates) getProtectedAsset();
            if(certs == null || certs.getX509CertificatesPEM() == null){
                throw new GeneralException("Error: No certificate found.");
            }
            response.setContentType(OAuthConstants.FORM_ENCODING);
            OutputStream out = response.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(out);
            for (String key : getAdditionalInformation().keySet()) {
                osw.write(key + "=" + getAdditionalInformation().get(key) + "\n");
            }
            osw.flush(); // make sure its all there.
            out.write(certs.getX509CertificatesPEM().getBytes());
            out.flush();
            out.close();
        } catch (Exception x) {
            throw new GeneralException(x);
        }
    }

    public ProtectedAsset getProtectedAsset() {
        return protectedAsset;
    }

    public void setProtectedAsset(ProtectedAsset protectedAsset) {
        this.protectedAsset = protectedAsset;
    }

    ProtectedAsset protectedAsset;
}
