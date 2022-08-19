package edu.uiuc.ncsa.sat.thing;

import edu.uiuc.ncsa.sat.EncryptionException;
import edu.uiuc.ncsa.sat.SATConstants;
import edu.uiuc.ncsa.sat.SATEnvironment;
import edu.uiuc.ncsa.sat.SATServlet;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

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
     * @param key
     * @param x
     * @return
     */
    protected String sEncrypt(byte[] key, String x){
        return DecryptUtils.sEncrypt(key, x);
    }

    protected String rsaEncrypt(PublicKey publicKey, String x) {
        try {
            return DecryptUtils.encryptPublic(publicKey, x);
        }catch(GeneralSecurityException xx){
              throw new EncryptionException("Unable to encrypt payload:" + xx.getMessage(), xx);
        }
    }
    public void ok(HttpServletResponse servletResponse, SATServlet.SessionRecord sessionRecord) throws IOException {
        PrintWriter pw = servletResponse.getWriter();
        JSONObject json = new JSONObject();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        pw.println(sEncrypt(sessionRecord.sKey, json.toString(1)));
    }

    public void serialize(OutputResponse outputResponse, HttpServletResponse servletResponse, SATServlet.SessionRecord sessionRecord) throws IOException {
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_CONTENT, outputResponse.content);
        pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));

    }
    public void serialize(PromptResponse promptResponse, HttpServletResponse servletResponse, SATServlet.SessionRecord sessionRecord) throws IOException{
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_PROMPT, promptResponse.prompt);
        pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
    }
    public void serialize(LogonResponse logonResponse, HttpServletResponse servletResponse, SATServlet.SessionRecord sessionRecord) throws IOException{
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        json.put(RESPONSE_SESSION_ID, logonResponse.uuid.toString());
        json.put(RESPONSE_SYMMETRIC_KEY, Base64.encodeBase64URLSafeString(sessionRecord.sKey));
        System.out.println(getClass().getSimpleName() + ": logon response: " + json.toString(1));
        pw.println(rsaEncrypt(sessionRecord.client.getPublicKey(),json.toString(1)));
    }

    public void serialize(LogoffResponse logoffResponse, HttpServletResponse servletResponse, SATServlet.SessionRecord sessionRecord) throws IOException{
        JSONObject json = new JSONObject();
        PrintWriter pw = servletResponse.getWriter();
        json.put(RESPONSE_STATUS, RESPONSE_STATUS_OK);
        if(!StringUtils.isTrivial(logoffResponse.message)){
            json.put(RESPONSE_LOG_OFF_MESSAGE, logoffResponse.message);
        }
        pw.println(sEncrypt(sessionRecord.sKey,json.toString(1)));
    }
}
