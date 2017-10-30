package edu.uiuc.ncsa.security.oauth_2_0.sciTokens;

import edu.uiuc.ncsa.security.oauth_2_0.JWTUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKey;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeyUtil;
import edu.uiuc.ncsa.security.util.jwk.JSONWebKeys;
import edu.uiuc.ncsa.security.util.pkcs.KeyUtil;
import net.sf.json.JSONObject;

import java.io.File;

/**
 * Utility for creating SciTokens
 * <p>Created by Jeff Gaynor<br>
 * on 8/31/17 at  12:04 PM
 */
public class SciTokenUtil extends JWTUtil {
    public static void main(String[] args) {
        try {
            // firstTest();
            //   firstTestB();
            //    otherTest();
            test1();
            //  testSigningDirectly();
            //testJWT_IO();
            // printKeys();
            // generateAndSign();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void test1() throws Throwable {
        String h = "{\"typ\":\"JWT\"," +
                "\"kid\":\"9k0HPG3moXENne\"," +
                "\"alg\":\"RS256\"}";
        String p = "{" +
                "\"sub\":\"https://scitokens.org/vo/1234567\"," +
                "\"nbf\":\"1504208792\"," +
                "\"exp\":1535744788," +
                "\"iss\":\"https://ashigaru.ncsa.uiuc.edu:9443\"," +
                "\"aud\":\"https://scitokens.org/14649e2f468450dac0c1834811dbd4c7\"}";
        JSONObject header = JSONObject.fromObject(h);
        System.out.println("header=" + header);
        JSONObject payload = JSONObject.fromObject(p);
        System.out.println("payload=" + payload);
        System.out.println("base 64=" + concat(header, payload));
        //String keyID = "9k0HPG3moXENne";
        String keyID = "244B235F6B28E34108D101EAC7362C4E";
        JSONWebKeys keys = JSONWebKeyUtil.fromJSON(new File("/home/ncsa/dev/csd/config/polo-keys.jwk"));

        String idTokken = createJWT(payload, keys.get(keyID));
        System.out.println(idTokken);
        JSONObject claims = verifyAndReadJWT(idTokken, keys);
        System.out.println("claims = " + claims);
        JSONWebKey webKey = keys.get(keyID);
        System.out.println(KeyUtil.toX509PEM(webKey.publicKey));
    }

}
