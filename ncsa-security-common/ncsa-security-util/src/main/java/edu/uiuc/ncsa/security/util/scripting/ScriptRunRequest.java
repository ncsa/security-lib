package edu.uiuc.ncsa.security.util.scripting;

import java.util.Map;

/**
 * This is a request to the {@link ScriptRuntimeEngine}. The arguments are sent along in a map
 * which is returned with their updated values. An {@link #getAction()} is supplied which tells the runtime
 * engine about script to run. This is agreed on by the implementors and can be a key or perhaps
 * the name of a script.
 * <p>Created by Jeff Gaynor<br>
 * on 2/6/20 at  12:28 PM
 */
public interface ScriptRunRequest {
    /**
     * The arguments for the script. Each name should be turned in to a variable that the
     * script can understand.
     * @return
     */
    public Map<String, Object> getArgs();


    /**
     * An action for the script engine to do. These are set based on the application/implementor and
     * the specific underlying language.
     * @return
     */
    public String getAction();

    /**
     * Return the supplied arguments, using their same names along with any response.
     * @return
     */
     public boolean returnArgs();

    /**
     * The name of the response, if any, to be returned.
     * @return
     */
     public String getResponseArgName();

    /**
     * Whether or not this returns a value. If not, then the response argument name is ignored.
     * This may be the case if the script has no output.
     * @return
     */
    public boolean hasReturnedValue();

}

