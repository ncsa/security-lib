package edu.uiuc.ncsa.sas.webclient;

import edu.uiuc.ncsa.sas.SASConstants;
import edu.uiuc.ncsa.sas.exceptions.SASException;
import edu.uiuc.ncsa.sas.thing.response.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 8/25/22 at  7:28 AM
 */
public class ResponseDeserializer implements SASConstants {
    /**
     * Main entry point for class. This delegates to all the other methods
     *
     * @param input
     * @return
     */
    public List<Response> deserialize(String input) {
        List<Response> outList = new ArrayList<>();
        try {

            Response response = deserialize(JSONObject.fromObject(input));
            outList.add(response);
            return outList;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        JSONArray jsonArray;
        try {
            jsonArray = JSONArray.fromObject(input);
        } catch (Throwable t) {
            throw new SASException("unknown response type:" + t.getMessage());
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            outList.add(deserialize(jsonArray.getJSONObject(i)));
        }

        return outList;
    }

    /**
     * Deserialize a single response
     *
     * @param jsonObject
     * @return
     */
    public Response deserialize(JSONObject jsonObject) {
        if (jsonObject.containsKey(RESPONSE_STATUS)) {
            // check for error codes before we get started
            switch (jsonObject.getInt(RESPONSE_STATUS)) {
                case RESPONSE_STATUS_OK:
                    // do nothing
                    break;
                case RESPONSE_STATUS_ERROR:
                    throw new SASException("Error on SAS server:");
            }
        }
        Response response = null;
        switch (jsonObject.getString(RESPONSE_TYPE)) {
            case RESPONSE_TYPE_LOGON:
                response = new LogonResponse();
                break;
            case RESPONSE_TYPE_LOGOFF:
                response = new LogoffResponse();
                break;
            case RESPONSE_TYPE_OUTPUT:
                response = new OutputResponse();
                break;
            case RESPONSE_TYPE_PROMPT:
                response = new PromptResponse();
                break;
            case RESPONSE_TYPE_NEW_KEY:
                response = new NewKeyResponse();
                break;
            default:
                throw new SASException("unknown response type \"" + jsonObject.getString(KEYS_TYPE) + "\"");
        }
        response.deserialize(jsonObject);
        return response;
    }

}
