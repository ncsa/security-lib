package edu.uiuc.ncsa.security.util.scripting;

import java.util.Map;

/**
 * The response to a {@link ScriptRunRequest}. This contains the results of running the script
 * with the objects keyed by name. The message is optional and may be the output of an error.
 * The return code is from the engine and may be one of the RC_ values. A negative result denotes
 * there was an error processing the request,
 * <ul>
 *     <li>{@link #RC_INTERNAL_ERROR} means that there was an actual error in the runtime engine itself</li>
 *     <li>{@link #RC_SCRIPT_NOT_FOUND} means that the requested script was not found.</li>
 *     <li> {@link #RC_SCRIPT_ERROR} means that the script reported an error.</li>
 *     <li>{@link #RC_NOT_RUN} means that the request was not handled. E.g. the runtime engine could not start</li>
 *     <li>{@link #RC_OK} means everything ran fine.</li>
 * </ul>
  * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  12:28 PM
 */
public class ScriptRunResponse {
    public static final int RC_INTERNAL_ERROR = -3;
    public static final int RC_SCRIPT_NOT_FOUND = -2;
    public static final int RC_SCRIPT_ERROR = -1;
    public static final int RC_NOT_RUN = 0;
    public static final int RC_OK = 1;
    public static final int RC_OK_NO_SCRIPTS = 2;
    String message = "";
    Map<String, Object> map;
    int rc = RC_NOT_RUN;

    public ScriptRunResponse(String message, Map<String, Object> map, int rc) {
        this.message = message;
        this.map = map;
        this.rc = rc;
    }

    public String getMessage(){return message;}
    public Map<String,Object> getReturnedValues(){return map;}
    public int getReturnCode(){return rc;}
}
