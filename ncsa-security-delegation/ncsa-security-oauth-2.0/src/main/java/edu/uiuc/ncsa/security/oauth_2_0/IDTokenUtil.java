package edu.uiuc.ncsa.security.oauth_2_0;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

/**
 * Creates JWT tokens. This is for <b>unsigned</b> and <b>/b>unverified</b> tokens which
 * can be sent over a secure connection. The format is to have a header that describes the
 * content, including algorithm (fixed at "none" here) and a payload of claims. Both of these
 * are in JSON. The token then consists of based64 encoding both of these and <br/><br>
 *     encoded header + "."   + encoded payload + "."<br/><br>
 * The last period is manadatory and must end this. Normally if there is signing or verification
 * used, this last field would contain information pertaining to that. It must be omitted because we
 * do neither of these.
 * <p>Created by Jeff Gaynor<br>
 * on 2/9/15 at  10:45 AM
 */

// Fixes OAUTH-164, adding id_token support.
public class IDTokenUtil {
    public static String TYPE = "typ";
    public static String ALGORITHM = "alg";

    public static String createIDToken(JSONObject payload){
        JSONObject header = new JSONObject();
             header.put(TYPE, "JWT");
             header.put(ALGORITHM, "none");

             return Base64.encodeBase64URLSafeString(header.toString().getBytes()) + "." +
                     Base64.encodeBase64URLSafeString(payload.toString().getBytes()) + ".";
    }

    public static JSONObject readIDToken(String idToken){
        if(!idToken.endsWith(".")) throw new GeneralException("Error: only unsigned/unverified id tokens are supported");
        idToken = idToken.substring(0,idToken.length()-1); //drop that last period
        int firstPeriod = idToken.indexOf(".");
        String header = idToken.substring(0, firstPeriod);
        String payload = idToken.substring(firstPeriod + 1);
        JSONObject h = JSONObject.fromObject(new String(Base64.decodeBase64(header)));
        JSONObject p = JSONObject.fromObject(new String(Base64.decodeBase64(payload)));

        if(!h.get(TYPE).equals("JWT")) throw new GeneralException("Unsupported token type.");
        if(!h.get(ALGORITHM).equals("none")) throw new GeneralException("Unsupported algorithm");

        return p;
    }
}
