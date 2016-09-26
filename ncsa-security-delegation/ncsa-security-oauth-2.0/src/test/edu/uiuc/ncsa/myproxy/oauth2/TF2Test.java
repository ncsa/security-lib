package edu.uiuc.ncsa.myproxy.oauth2;

import edu.uiuc.ncsa.security.oauth_2_0.OA2TokenForge;
import edu.uiuc.ncsa.security.util.TestBase;
import org.junit.Test;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 10/3/13 at  4:29 PM
 */
public class TF2Test extends TestBase {
    @Test
    public void testTokens() throws Exception{
        OA2TokenForge tf2 = new OA2TokenForge("foo:test:");
        System.out.println(tf2.getAccessToken());
        System.out.println(tf2.getAuthorizationGrant());
        try{
            tf2.getVerifier();
        }catch(UnsupportedOperationException gx){
            assert true;
        }
        System.out.println(tf2.getRefreshToken());
    }
}
