package edu.uiuc.ncsa.security.oauth_1_0a.client;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.util.MapUtilities;
import edu.uiuc.ncsa.security.delegation.client.request.PARequest;
import edu.uiuc.ncsa.security.delegation.client.request.PAResponse;
import edu.uiuc.ncsa.security.delegation.client.server.PAServer;
import edu.uiuc.ncsa.security.delegation.services.AddressableServer;
import edu.uiuc.ncsa.security.delegation.services.Request;
import edu.uiuc.ncsa.security.delegation.services.Response;
import edu.uiuc.ncsa.security.delegation.storage.Client;
import edu.uiuc.ncsa.security.delegation.token.AccessToken;
import edu.uiuc.ncsa.security.delegation.token.MyX509Certificates;
import edu.uiuc.ncsa.security.delegation.token.impl.AccessTokenImpl;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants;
import edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities;
import edu.uiuc.ncsa.security.util.pkcs.CertUtil;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthConstants.PRIVATE_KEY;
import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities.createOAuthAccessor;
import static edu.uiuc.ncsa.security.oauth_1_0a.OAuthUtilities.newOAuthClient;

/**
 * <p>Created by Jeff Gaynor<br>
 * on Apr 27, 2011 at  12:12:17 PM
 */
public class PAServerImpl implements AddressableServer, PAServer {
    URI address;
    public PAServerImpl(URI address) {
        this.address  = address;
    }

    @Override
    public URI getAddress() {
        return address;
    }

    @Override
    public Response process(Request request) {
        return request.process(this);
    }
    @Override
    public PAResponse processPARequest(PARequest request) {
        return getAsset(request);
    }

    protected PAResponse getAsset(PARequest request) {
        return getAsset(request.getClient(), request.getParameters(), request.getAccessToken());

    }

    protected PAResponse getAsset(Client client, Map props, AccessToken accessToken) {
        MyX509Certificates myX509Certificate = null;
        AccessTokenImpl atImpl = null;
        if (accessToken instanceof AccessTokenImpl) {
            atImpl = (AccessTokenImpl) accessToken;
        } else {
            throw new GeneralException("Error: unsupported access token implementation");
        }
        OAuthAccessor accessor = createOAuthAccessor(this, (OAClient) client);
        accessor.accessToken = atImpl.getToken();
        accessor.tokenSecret = atImpl.getSharedSecret();

        try {
            OAuthClient oaClient = newOAuthClient(getAddress());
            // Have to set it for *both* of these!
            accessor.consumer.setProperty(PRIVATE_KEY, client.getSecret());
            accessor.setProperty(OAuthConstants.PRIVATE_KEY, client.getSecret());
            // Note that in *THIS* case, the second parameter is not the method name, but the URL of the service.
            OAuthMessage message = oaClient.invoke(accessor, getAddress().toString(), MapUtilities.toList(props));
            // we have to look at the body for additional information.
            InputStreamReader isr = new InputStreamReader(message.getBodyAsStream());
            BufferedReader br = new BufferedReader(isr);
            HashMap<String, String> additionalInfo = new HashMap<String, String>();
            String linein = br.readLine();
            while (linein != null && !linein.equals(CertUtil.BEGIN_CERTIFICATE)) {
                int loc = linein.indexOf("=");
                if (loc == -1) {
                    // do something better than just silently skip it?
                } else {
                    additionalInfo.put(linein.substring(0, loc), linein.substring(loc + 1));
                }
                linein = br.readLine();
            }
            // so we have the first line that is the begin certificate line. Glom all that and decode
            StringBuilder certString = new StringBuilder();

            while (linein != null) {
                certString.append(linein);
                linein = br.readLine();
                // only appends a return if this is not the last line. Appending and extra one prevents correct
                // parsing of the final cert.
                if (linein != null) {
                    certString.append("\n");
                }
            }

            myX509Certificate = new MyX509Certificates(CertUtil.fromX509PEM(certString.toString()));
            PAResponse par = new PAResponse(myX509Certificate);
            par.setAdditionalInformation(additionalInfo);
            HashMap m = OAuthUtilities.whittleParameters(message);
            par.setParameters(m);
            return par;
        } catch (Exception e) {
            throw new GeneralException("Error invoking OAuthConstants client", e);
        }
    }
}
