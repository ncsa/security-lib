package edu.uiuc.ncsa.sas.thing.response;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.SASEnvironment;
import edu.uiuc.ncsa.sas.SessionRecord;
import edu.uiuc.ncsa.sas.exceptions.EncryptionException;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;

import static edu.uiuc.ncsa.security.core.util.StringUtils.isTrivial;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/16/22 at  6:26 AM
 */
public class ResponseSerializer implements SASConstants {
    public SASEnvironment getSATE() {
        return sate;
    }

    public void setSATE(SASEnvironment sate) {
        this.sate = sate;
    }

    SASEnvironment sate;

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
        JSONObject json = logonResponse.serialize();
        PrintWriter pw = servletResponse.getWriter();
        //     System.out.println(getClass().getCanonicalName() + " json= " + json.toString(1));
        //   System.out.println(getClass().getCanonicalName() + " length is " + json.toString(1).length());
        pw.println(rsaEncrypt(sessionRecord.client.getPublicKey(), json.toString()));
    }


    public void serialize(Throwable throwable, HttpServletResponse servletResponse, SessionRecord sessionRecord) throws IOException {
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_ERROR);
        json.put(KEYS_TYPE, RESPONSE_TYPE_ERROR);
        if (!isTrivial(throwable.getMessage())) {
            json.put(RESPONSE_MESSAGE, throwable.getMessage());
        } else {
            json.put(RESPONSE_MESSAGE, "(no message)");
        }
        pw.println(sEncrypt(sessionRecord.sKey, json.toString(1)));
    }


    protected void setResponseState(Response response, JSONObject jsonObject) {
        jsonObject.put(REQUEST_TYPE, response.getActionType()); // must have
        if (!isTrivial(response.getState())) {
            jsonObject.put(KEYS_STATE, response.getState());
        }
        if (!isTrivial(response.getId())) {
            jsonObject.put(KEYS_INTERNAL_ID, response.getId());
        }
        if (!isTrivial(response.getComment())) {
            jsonObject.put(KEYS_COMMENT, response.getComment());
        }
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
            array.add(response.serialize());
            if (response instanceof NewKeyResponse) {
                newKeyResponse = (NewKeyResponse) response;
            }
        }
        PrintWriter pw = servletResponse.getWriter();
        if (responses.size() == 1) {
            pw.println(sEncrypt(sessionRecord.sKey, array.getJSONObject(0).toString(1)));
        } else {
            JSONObject json = new JSONObject();
            json.put(SASConstants.KEYS_SAT, array.toString(1));
            pw.println(sEncrypt(sessionRecord.sKey, json.toString(1)));
        }
        // Final thing to do -- update the new key if there is one
        if (newKeyResponse != null) {
            sessionRecord.sKey = newKeyResponse.getKey();
        }
    }
}
