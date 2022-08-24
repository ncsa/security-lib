package edu.uiuc.ncsa.sat.thing;

import edu.uiuc.ncsa.sat.EncryptionException;
import edu.uiuc.ncsa.sat.SATConstants;
import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.sat.SessionRecord;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:26 AM
 */
public class ResponseSerializer implements SATConstants {
    public SATEnvironment getSATE() {
        return sate;
    }

    public void setSATE(SATEnvironment sate) {
        this.sate = sate;
    }

    SATEnvironment sate;

    /**
     * Symmetric key encode. Returned string is base 64 encoded byte array.
     *
     * @param key
     * @param x
     * @return
     */
    protected String sEncrypt(byte[] key, String x) {
        return DecryptUtils.sEncrypt(key, x);
    }

    protected String rsaEncrypt(PublicKey publicKey, String x) {
        try {
            return DecryptUtils.encryptPublic(publicKey, x);
        } catch (GeneralSecurityException xx) {
            throw new EncryptionException("Unable to encrypt payload:" + xx.getMessage(), xx);
        }
    }

    public JSONObject ok(HttpServletResponse servletResponse) {
        JSONObject json = new JSONObject();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        //pw.println(sEncrypt(sessionRecord.sKey, json.toString(1)));
        return json;
    }

    public JSONObject serialize(OutputResponse outputResponse) {
        JSONObject json = new JSONObject();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_CONTENT, outputResponse.content);
        //pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
        return json;
    }

    public JSONObject serialize(PromptResponse promptResponse) {
        JSONObject json = new JSONObject();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_PROMPT, promptResponse.prompt);
        return json;
        //pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
    }

    /**
     * This is an outlier in the sense that there is only exactly one logon request and it must have
     * its resposne RSA encrypted.
     *
     * @param logonResponse
     * @param servletResponse
     * @param sessionRecord
     * @throws IOException
     */
    public void serialize(LogonResponse logonResponse, HttpServletResponse servletResponse, SessionRecord sessionRecord) throws IOException {
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_SESSION_ID, logonResponse.uuid.toString());
        json.put(RESPONSE_SYMMETRIC_KEY, Base64.encodeBase64URLSafeString(sessionRecord.sKey));
        pw.println(rsaEncrypt(sessionRecord.client.getPublicKey(), json.toString(1)));
    }

    public JSONObject serialize(LogoffResponse logoffResponse) {
        JSONObject json = new JSONObject();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        if (!StringUtils.isTrivial(logoffResponse.message)) {
            json.put(RESPONSE_MESSAGE, logoffResponse.message);
        }
        return json;
        //pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
    }

    public void serialize(Throwable throwable, HttpServletResponse servletResponse, SessionRecord sessionRecord) throws IOException {
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_ERROR);
        if (!StringUtils.isTrivial(throwable.getMessage())) {
            json.put(RESPONSE_MESSAGE, throwable.getMessage());
        } else {
            json.put(RESPONSE_MESSAGE, "(no message)");
        }
        pw.println(sEncrypt(sessionRecord.sKey, json.toString(1)));
    }

    protected JSONObject processResponse(Response response) {
        if (response instanceof LogonResponse) {
            return serialize((LogoffResponse) response);
        }
        if (response instanceof OutputResponse) {
            return serialize((OutputResponse) response);
        }
        if (response instanceof LogoffResponse) {
            return serialize((LogoffResponse) response);
        }

        if (response instanceof PromptResponse) {
            return serialize((PromptResponse) response);
        }
        if(response instanceof NewKeyResponse){
            return serialize((NewKeyResponse) response);
        }
        throw new IllegalArgumentException("unknown response type:" + response.getClass().getCanonicalName());
    }

    protected JSONObject serialize(NewKeyResponse response) {
        JSONObject json = new JSONObject();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_SYMMETRIC_KEY, Base64.encodeBase64URLSafeString(response.getKey()));
        //pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
        return json;
    }

    public void serialize(List<Response> responses, HttpServletResponse servletResponse, SessionRecord sessionRecord) throws IOException {
        // Options are size(response2) == 1, just return response.
        // only return a list of there is a list.
        if (responses.size() == 1) {
            if (responses.get(0) instanceof LogonResponse) {
                serialize((LogonResponse) responses.get(0), servletResponse, sessionRecord);
                return;
            }
        }
        JSONArray array = new JSONArray();
        NewKeyResponse newKeyResponse = null;
        for (Response response : responses) {
            array.add(processResponse(response));
            if( response instanceof NewKeyResponse){
                newKeyResponse = (NewKeyResponse) response;
            }
        }
        PrintWriter pw = servletResponse.getWriter();
        if (responses.size() == 1) {
            pw.println(sEncrypt(sessionRecord.sKey, array.getJSONObject(0).toString(1)));
            return;
        }
        JSONObject json = new JSONObject();
        json.put(SATConstants.KEYS_SAT, array.toString(1));
        pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
        if(newKeyResponse != null){
            sessionRecord.sKey = newKeyResponse.getKey();
        }
    }
}
