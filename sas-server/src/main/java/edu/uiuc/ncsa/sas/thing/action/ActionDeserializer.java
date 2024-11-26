package edu.uiuc.ncsa.sas.thing.action;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.SessionRecord;
import edu.uiuc.ncsa.sas.exceptions.EncryptionException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
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
import java.util.List;

/**
 * Convert the request payload into objects
 * <p>Created by Jeff Gaynor<br>
 * on 8/15/22 at  8:42 AM
 */
public class ActionDeserializer implements SASConstants {
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
        DebugUtil.trace(ActionDeserializer.class, "line=" + line);
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

            return toAction(jsonObject.getJSONObject(SASConstants.KEYS_SAS));
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
     *     {"sas":{simple action}}
     * </pre>
     * <b>or</b>
     * <pre>
     *     {"sas":[{action0}, {action1},...]}
     * </pre>
     * This method always returns a {@link List}.
     *
     * @param jsonObject
     * @return
     */
    public List<Action> toActions(JSONObject jsonObject) {
        JSON json = jsonObject.getJSONObject(KEYS_SAS);
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
                  action = new NewKeyAction();
                  break;
              case ACTION_EXECUTE:
                  action = new ExecuteAction();
                  break;
              case ACTION_INVOKE:
                  action = new InvokeAction();
                 break;
              case ACTION_LOGOFF:
                  action = new LogoffAction();
                  break;
              default:
                  throw new IllegalArgumentException("unknown action \"" + a + "\".");
          }
          action.deserialize(jsonObject);
          //setStateAndID(action, jsonObject);
          return action;
      }



    /**
     * Returns the action to be done.
     *
     * @param json
     * @return
     */
    public String getAction(JSONObject json) {
       // JSONObject api = json.getJSONObject(KEYS_SAT);
        return json.getString(KEYS_ACTION);
    }
}
