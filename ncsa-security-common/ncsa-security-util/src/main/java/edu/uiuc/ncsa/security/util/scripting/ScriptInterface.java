package edu.uiuc.ncsa.security.util.scripting;

import edu.uiuc.ncsa.security.core.configuration.XProperties;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/4/20 at  11:14 AM
 */
public interface ScriptInterface {
    /**
     * Run this script with the given state.
     * @param state
     */
    void execute(StateInterface state);

    /**
     * Properties the system <i>may</i> need about this script. These are not set here but are
     * determined by the needs of the {@link ScriptRuntimeEngine} and the language. At the least,
     * they should included a language and language version, plus some identifier.
     * @return
     */
    XProperties getProperties();
}
