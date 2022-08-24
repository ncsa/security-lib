package edu.uiuc.ncsa.sas;

import edu.uiuc.ncsa.sas.thing.*;
import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.BasicIdentifier;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.core.util.StringUtils;
import edu.uiuc.ncsa.security.util.crypto.DecryptUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Convert the request payload into objects
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:42 AM
 */
public class RequestDeserializer implements SASConstants {
    /**
     * Given the request, grab the body of the post
     *
     * @param sessionRecord
     * @param request
     * @return
     * @throws IOException
     */
    public List<Action> rsaDeserialize(SessionRecord sessionRecord, HttpServletRequest request) throws IOException {
        StringBuffer stringBuffer = getStringBuffer(request);
        if (stringBuffer.length() == 0) {
            throw new IllegalArgumentException("Error: There is no content for this request");
        }
        List<Action> actions = new ArrayList<>();
        actions.add(rsaDeserialize(sessionRecord, stringBuffer.toString()));
        return actions;
    }

    public List<Action> sDeserialize(SessionRecord sessionRecord, HttpServletRequest request) throws IOException {
        StringBuffer stringBuffer = getStringBuffer(request);
        if (stringBuffer.length() == 0) {
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

    public Action rsaDeserialize(SessionRecord sessionRecord, String payload) {
        try {
            JSONObject jsonObject = JSONObject.fromObject(DecryptUtils.decryptPublic(sessionRecord.client.getPublicKey(), payload));

            return toAction(jsonObject.getJSONObject(SASConstants.KEYS_SAT));
        } catch (GeneralSecurityException | UnsupportedEncodingException gsx) {
            throw new EncryptionException("Unable to decrypt:" + gsx.getMessage(), gsx);
        }
    }

    public List<Action> sDeserialize(SessionRecord sessionRecord, String payload) {
        return toActions(JSONObject.fromObject(DecryptUtils.sDecrypt(sessionRecord.sKey, payload)));

    }

    /**
     * Options for format are
     * <pre>
     *     {"sat":{simple action}}
     * </pre>
     * <b>or</b>
     * <pre>
     *     {"sat":[{action0}, {action1},...]}
     * </pre>
     * This method always returns a {@link List}.
     *
     * @param jsonObject
     * @return
     */
    public List<Action> toActions(JSONObject jsonObject) {
        JSON json = jsonObject.getJSONObject(KEYS_SAT);
        List<Action> actions = new ArrayList<>();
        if (json.isArray()) {
            JSONArray array = (JSONArray) json;
                for(int i = 0; i < array.size(); i++){
                    actions.add(toAction(array.getJSONObject(i)));
                }
        } else {
             actions.add(toAction((JSONObject) json));
        }
        return actions;
    }

    /**
     * Takes a <i>single</i> known action item and returns the right one
     *
     * @param jsonObject
     * @return
     */
    public Action toAction(JSONObject jsonObject) {
        String a = getAction(jsonObject);
        Action action = null;
        switch (a) {
            case ACTION_LOGON:
                action = new LogonAction();
                break;
            case ACTION_NEW_KEY:
                NewKeyAction newKeyAction = new NewKeyAction();
                newKeyAction.setSize(jsonObject.getInt(KEYS_ARGUMENT));
                action = newKeyAction;
                break;
            case ACTION_EXECUTE:
                ExecuteAction executeAction = new ExecuteAction();
                executeAction.setArg(getArg(jsonObject));
                action = executeAction;
                break;
            case ACTION_INVOKE:
                InvokeAction invokeAction = new InvokeAction();
                invokeAction.setName(getMethod(jsonObject));
                if (jsonObject.get(KEYS_ARGUMENT) instanceof JSONArray) {
                    invokeAction.setArgs(jsonObject.getJSONArray(KEYS_ARGUMENT));
                } else {
                    JSONArray array = new JSONArray();
                    array.add(jsonObject.get(KEYS_ARGUMENT));
                    invokeAction.setArgs(array);
                }
                action = invokeAction;
                break;
            case ACTION_LOGOFF:
                action = new LogoffAction();
                break;
            default:
                throw new IllegalArgumentException("unknown action \"" + a + "\".");
        }
        setStateAndID(action, jsonObject);
        return action;
    }

    /**
     * Rerturns the action to be done.
     *
     * @param json
     * @return
     */
    public String getAction(JSONObject json) {
       // JSONObject api = json.getJSONObject(KEYS_SAT);
        return json.getString(KEYS_ACTION);
    }

    public String getArg(JSONObject json) {
        //JSONObject api = json.getJSONObject(KEYS_SAT);
        String object = json.getString(KEYS_ARGUMENT);        // always a base64 encoded string
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
     * For an invoke action, get the method name
     *
     * @param json
     * @return
     */
    public String getMethod(JSONObject json) {
       // JSONObject api = json.getJSONObject(KEYS_SAT);
        return json.getString(KEYS_METHOD);
    }

    public String getPrompt(JSONObject json) {
        //JSONObject api = json.getJSONObject(KEYS_SAT);
        String object = json.getString(KEYS_PROMPT);        // always a base64 encoded string
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
     *    {"{@link SASConstants#KEYS_SUBJECT}":
     *     {"{@link SASConstants#KEYS_SUBJECT_ID}":"id_string",
     *      "{@link SASConstants#KEYS_SUBJECT_SESSION_ID}":"session_uuid"
     *      }
     *    }
     * </pre>
     * <b>OR</b>
     * <pre>
     *    {"{@link SASConstants#KEYS_SUBJECT}": "id_string"}
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

    protected void setStateAndID(Action action, JSONObject object) {
        if (object.containsKey(KEYS_STATE)) {
            action.setState(object.getString(KEYS_STATE));
        }
        if (object.containsKey(KEYS_INTERNAL_ID)) {
            action.setId(object.getString(KEYS_INTERNAL_ID));
        }
        if (object.containsKey(KEYS_COMMENT)) {
            action.setComment(object.getString(KEYS_COMMENT));
        }
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
