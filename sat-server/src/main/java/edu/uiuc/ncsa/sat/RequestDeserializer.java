package edu.uiuc.ncsa.sat;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.UUID;

/**
 * Convert the request payload into objects
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:42 AM
 */
public class RequestDeserializer implements SATConstants {
    /**
     * Given the request, grab the body of the post
     *
     * @param sessionRecord
     * @param request
     * @return
     * @throws IOException
     */
    public JSONObject rsaDeserialize(SATServlet.SessionRecord sessionRecord, HttpServletRequest request) throws IOException {
        StringBuffer stringBuffer = getStringBuffer(request);
        if (stringBuffer.length() == 0) {
            throw new IllegalArgumentException("Error: There is no content for this request");
        }
        return rsaDeserialize(sessionRecord, stringBuffer.toString());
    }
    public JSONObject sDeserialize(SATServlet.SessionRecord sessionRecord, HttpServletRequest request) throws IOException {
        StringBuffer stringBuffer = getStringBuffer(request);
        if(stringBuffer.length() == 0){
            throw new IllegalArgumentException("Error: There is no content for this request");
        }
        return sDeserialize(sessionRecord, stringBuffer.toString());
    }
    private StringBuffer getStringBuffer(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        StringBuffer stringBuffer = new StringBuffer();
        String line = br.readLine();
        DebugUtil.trace(RequestDeserializer.class, "line=" + line);
        while (line != null) {
            stringBuffer.append(line);
            line = br.readLine();
        }
        br.close();
        return stringBuffer;
    }

    public JSONObject rsaDeserialize(SATServlet.SessionRecord sessionRecord, String payload) {
        try {
            return JSONObject.fromObject(DecryptUtils.decryptPublic(sessionRecord.client.getPublicKey(), payload));
        } catch (GeneralSecurityException | UnsupportedEncodingException gsx) {
            throw new EncryptionException("Unable to decrypt:" + gsx.getMessage(), gsx);
        }
    }

    public JSONObject sDeserialize(SATServlet.SessionRecord sessionRecord, String payload){
        return JSONObject.fromObject(DecryptUtils.sDecrypt(sessionRecord.sKey, payload));

    }
    /**
     * Rerturns the action to be done.
     *
     * @param json
     * @return
     */
    public String getAction(JSONObject json) {
        JSONObject api = json.getJSONObject(KEYS_SAT);
        return api.getString(KEYS_ACTION);
    }

    public String getContent(JSONObject json) {
        JSONObject api = json.getJSONObject(KEYS_SAT);
        String object = api.getString(KEYS_CONTENT);        // always a base64 encoded string
        if (object == null) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(object), "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new NFWException("UTF-8 is not a supported encoding in Java");
        }
    }

    public String getPrompt(JSONObject json) {
        JSONObject api = json.getJSONObject(KEYS_SAT);
        String object = api.getString(KEYS_PROMPT);        // always a base64 encoded string
        if (object == null) {
            return null;
        }
        try {
            return new String(Base64.getDecoder().decode(object), "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new NFWException("UTF-8 is not a supported encoding in Java");
        }
    }

    /**
     * Session {@link Subject} is JSON encoded as
     * <pre>
     *    {"{@link SATConstants#KEYS_SUBJECT}":
     *     {"{@link SATConstants#KEYS_SUBJECT_ID}":"id_string",
     *      "{@link SATConstants#KEYS_SUBJECT_SESSION_ID}":"session_uuid"
     *      }
     *    }
     * </pre>
     * <b>OR</b>
     * <pre>
     *    {"{@link SATConstants#KEYS_SUBJECT}": "id_string"}
     *    }
     * </pre>
     * Alternate
     *
     * @param jsonObject
     * @return
     */
    public Subject getSubject(JSONObject jsonObject) {
        Subject subject = new Subject();
        JSONObject api = jsonObject.getJSONObject(KEYS_SAT);
        Object object = api.get(KEYS_SUBJECT);
        String id = null;
        String uuid = null;
        if (object instanceof JSONObject) {
            JSONObject sub = (JSONObject) object;
            id = sub.getString(KEYS_SUBJECT_ID);
            if (StringUtils.isTrivial(id)) {
                return subject;
            }
            uuid = jsonObject.getString(KEYS_SUBJECT_SESSION_ID);
        } else {
            // try to interpret it as the id
            id = object.toString();
        }
        subject.identifier = BasicIdentifier.newID(id);
        if (uuid != null) {
            subject.sessionID = UUID.fromString(uuid);
        }
        return subject;
    }

    /**
     * The comment is at the top level of the JSONObject. It is generally ignored
     * in processing, but may be, e.g., entered into the logs.
     *
     * @param jsonObject
     * @return
     */
    public String getComment(JSONObject jsonObject) {
        return jsonObject.getString(KEYS_COMMENT);

    }
}
