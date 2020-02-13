package edu.uiuc.ncsa.qdl.extensions;

import edu.uiuc.ncsa.qdl.module.Module;
import edu.uiuc.ncsa.qdl.state.State;

import java.util.StringTokenizer;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 2/12/20 at  4:08 PM
 */
public class ExternalModuleLoader {
    /**
     * Pass in the state and a comma separated list of class names. This will load them into
     * the state and make them available. Make sure they are imported!
     * @param state
     * @param loaderClasses
     */
    public static void loadModule(State state, String loaderClasses) {
        StringTokenizer st = new StringTokenizer(loaderClasses, ",");
        String loaderClass;
        String foundClasses = "";
        boolean isFirst = true;
        while (st.hasMoreTokens()) {
            loaderClass = st.nextToken();
            try {
                Class klasse = state.getClass().forName(loaderClass);
                QDLLoader loader = (QDLLoader) klasse.newInstance();
                setupJavaModule(state, loader);
                if (isFirst) {
                    isFirst = false;
                    foundClasses = loaderClass;
                } else {
                    foundClasses = foundClasses + "," + loaderClass;
                }
            } catch (Throwable t) {
            }
        }
    }

    protected static void setupJavaModule(State state, QDLLoader loader) {
        for (Module m : loader.load()) {
            state.addModule(m); // done!
        }
    }
}
