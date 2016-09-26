package edu.uiuc.ncsa.security.delegation;

import junit.framework.TestCase;
import org.junit.Test;

/**  Proposed test to show direct interaction (so no going through a webapp)
 * of client and sever works. This might just be too large to
 * actually bother with.
 * <p>Created by Jeff Gaynor<br>
 * on 10/23/12 at  9:47 AM
 */
public class EndToEndTest extends TestCase {

    @Test
    public void testInitialize() throws Exception{
        // need key pair for RSA-SHA1
       /* Client client = new Client(BasicIdentifier.newID("urn:test:01"));
        KeyPair keyPair = KeyUtil.generateKeyPair();
        client.setSecret(KeyUtil.toX509PEM(keyPair.getPublic()));
        AuthorizationServer authorizationServer = new AuthorizationServerImpl(URI.create("https://localhost"));

        AGRequest req = new AGRequest();
        HashMap<String,String> extraParameters = new HashMap<String, String>();
        req.setParameters(extraParameters);
        req.setClient(client);
        AGResponse resp = (AGResponse) authorizationServer.process(req);*/

    }
}
