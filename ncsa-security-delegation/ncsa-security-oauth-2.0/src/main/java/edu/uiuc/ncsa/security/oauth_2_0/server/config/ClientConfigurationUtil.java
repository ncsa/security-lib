package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * This will read the configuration. It is meant to be used by the {@link ClientConfigurationFactory}
 * to modularize operations on the JSON.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:37 PM
 */
public class ClientConfigurationUtil {
    public static final String CLAIM_POST_PROCESSING_KEY = "postProcessing";
    public static final String CLAIM_PRE_PROCESSING_KEY = "preProcessing";
    public static final String RUNTIME_KEY = "runtime";

    public static void setRuntime(JSONObject config, JSONObject runtime) {
        config.put(RUNTIME_KEY, runtime);
    }

    public static boolean hasRuntime(JSONObject config) {
        return config.containsKey(RUNTIME_KEY);

    }

    /**
     * Retrieve the processor named by key. Processors are eventually {@link edu.uiuc.ncsa.security.util.functor.LogicBlocks}.
     *
     * @param config
     * @param key
     * @param defaultFunctor
     * @return
     */
    static protected JSONObject getProcessor(JSONObject config, String key, String defaultFunctor) {
        if (config.containsKey(key)) {
            Object obj = config.get(key);

            if (obj instanceof JSONArray) {
                JSONObject json = new JSONObject();
                json.put(defaultFunctor, obj);
                return json;
            }
            if (obj instanceof JSONObject) {
                return (JSONObject) obj;
            }
        }
        return new JSONObject();

    }


    public static JSONObject getRuntime(JSONObject config) {
        return getProcessor(config, RUNTIME_KEY, OR.getValue());
    }

    public static JSONArray getRuntimeArg(JSONObject config) {
        JSONObject processor = getRuntime(config);
        if (processor.containsKey(OR.getValue())) {
            return processor.getJSONArray(OR.getValue());
        }
        if (processor.containsKey(AND.getValue())) {
            return processor.getJSONArray(AND.getValue());
        }
        if (processor.containsKey(XOR.getValue())) {
            return processor.getJSONArray(XOR.getValue());
        }
        return new JSONArray();

    }


}
