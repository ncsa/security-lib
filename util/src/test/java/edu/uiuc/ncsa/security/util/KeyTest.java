package edu.uiuc.ncsa.security.util;

import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.util.pkcs.MyKeyUtil;

import java.io.FileReader;
import java.io.Reader;
import java.security.PrivateKey;
import java.security.PublicKey;

import static edu.uiuc.ncsa.security.util.pkcs.MyKeyUtil.fromPKCS1PEM;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/25/22 at  1:57 PM
 */
public class KeyTest extends TestBase{
    /**
     * Tests that a public and private keypair generated in OpenSSL can be ingested and
     * will result in a valid key pair.
     * @throws Exception
     */
    public void testOpenSSL() throws Exception{
        Reader privateFR = new FileReader(DebugUtil.getDevPath() + "/security-lib/util/src/test/resources/openssl-private.pem");
        Reader publicFR = new FileReader(DebugUtil.getDevPath() + "/security-lib/util/src/test/resources/openssl-public.pem");
        PrivateKey privateKey = fromPKCS1PEM(privateFR);
        PublicKey publicKey = MyKeyUtil.fromX509PEM(publicFR);
        assert MyKeyUtil.validateKeyPair(publicKey, privateKey) : "Invalid keypair";
    }

    /**
     * Tests that a pair of keys generated in Java can be ingested and will result in a valid keypair.
     * @throws Exception
     */
    public void testPublicPrivate() throws Exception{
        Reader privateFR = new FileReader(DebugUtil.getDevPath() + "/security-lib/util/src/test/resources/private-key.pem");
        Reader publicFR = new FileReader(DebugUtil.getDevPath() + "/security-lib/util/src/test/resources/public-key.pem");
        PrivateKey privateKey = fromPKCS1PEM(privateFR);
        PublicKey publicKey = MyKeyUtil.fromX509PEM(publicFR);
        assert MyKeyUtil.validateKeyPair(publicKey, privateKey) : "Invalid keypair";
    }
}
