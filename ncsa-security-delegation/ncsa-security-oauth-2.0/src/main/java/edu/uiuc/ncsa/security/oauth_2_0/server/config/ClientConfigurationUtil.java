package edu.uiuc.ncsa.security.oauth_2_0.server.config;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * This will read the configuration. It is meant to be used by the {@link ClientConfigurationFactory}
 * to modularize operations on the JSON. Note that the "thiny" refers to a JSON idiom, viz.,
 * a configuration entry of the form {"topLevelKey":JSON} where JSON is either an array or json object.
 * These may be done at any level of the configuration file, so be sure to send in the right JSON object with the topLevelKey.
 * <p/>
 * <p>Created by Jeff Gaynor<br>
 * on 8/30/17 at  3:37 PM
 */
public class ClientConfigurationUtil {
    public static final String CLAIM_POST_PROCESSING_KEY = "postProcessing";
    public static final String CLAIM_PRE_PROCESSING_KEY = "preProcessing";
    public static final String RUNTIME_KEY = "runtime";
    public static final String SAVED_KEY = "isSaved";
    public static final String COMMENT_KEY = "comment";

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

    /**
     * Return the contents as a JSON array. This also means that if there is a single object, it will
     * be wrapped in a {@link JSONArray}.
     * <pre>
     *  Z=   {"key0":{
     *         "key1':X
     *        }
     *     }
     * </pre>
     * This method returns the JSON array <code>[X]</code>. Another example:
     * <pre>
     *  Z=   {"key0":{
     *         "key1':[X,Y,z]
     *        }
     *     }
     * </pre>
     * This method returns the JSON array <code>[X,y,z]</code>.
     * @param topLevelKey
     * @param config
     * @param key
     * @return
     */

    public static JSONArray getThingies(String topLevelKey, JSONObject config, String key) {
        if (!config.containsKey(topLevelKey)) {
            return new JSONArray();
        }
        JSONObject claims = config.getJSONObject(topLevelKey);
        Object obj = claims.get(key);
        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        }
        JSONArray array = new JSONArray();
        array.add(obj);
        return array;
    }

    /**
     * Return the {@link JSONObject} for the given key. This will fail if there is not a single object there,
     * <p/>
     * <pre>
     *  Z=   {"key0":{
     *         "key1':X
     *        }
     *     }
     * </pre>
     * the call <code>getThingy("key0",Z,"key1")</code> returns X.
     *
     * @param topLevelKey
     * @param config
     * @param key
     * @return
     */
    public static JSONObject getThingy(String topLevelKey, JSONObject config, String key) {
        if (!config.containsKey(topLevelKey)) {
            return new JSONObject();
        }
        return config.getJSONObject(topLevelKey);
    }

    /**
     * Sets a JSON object at the given level.
     * <pre>
     *  Z=   {"key0":{
     *         ... stuff...
     *        }
     *     }
     * </pre>
     * The call <code>setThingy("key0:,Z,"key1",X)</code> results in
     * <pre>
     *  Z=   {"key0":{
     *         "key1':X,
     *          ... stuff ...
     *        }
     *     }
     * </pre>
     * This may involve replacing the value of key1 with X if there is already a value there.
     *
     * @param topLevelKey
     * @param config
     * @param key
     * @param thingy
     */
    public static void setThingy(String topLevelKey, JSONObject config, String key, JSON thingy) {
        JSONObject claims;
        if (config.containsKey(topLevelKey)) {
            claims = config.getJSONObject(topLevelKey);
        } else {
            claims = new JSONObject();
        }
        claims.put(key, thingy);
        config.put(topLevelKey, claims);

    }

    /**
     * Drills down a level to check if this thingy has the given object. So if you have the following
     * <pre>
     *  Z=   {"key0":{
     *         "key1':X
     *        }
     *     }
     * </pre>
     * The call here would be <code>hasThingy("key0",Z,"key1');</code> that would return <code>true</code>
     *
     * @param topLevelKey
     * @param key
     * @param config
     * @return
     */
    public static boolean hasThingy(String topLevelKey, String key, JSONObject config) {
        JSONObject claims;
        if (config.containsKey(topLevelKey)) {
            claims = config.getJSONObject(topLevelKey);
        } else {
            return false;
        }
        return claims.containsKey(key);
    }

    /**
     * One of the few bits of state. Checks if the object has a saved flag.
     *
     * @param config
     * @return
     */
    public static boolean isSaved(JSONObject config) {
        if (config.containsKey(SAVED_KEY)) {
            return config.getBoolean(SAVED_KEY);
        }

        return true;
    }

    public static void setSaved(JSONObject config, boolean value) {
        config.put(SAVED_KEY, value);
    }

    /**
     * Checks if the object has a comment flag.
     *
     * @param config
     * @return
     */
    public static String getComment(JSONObject config) {
        if (config.containsKey(COMMENT_KEY)) {
            return config.getString(COMMENT_KEY);
        }

        return "";
    }

    public static void setComment(JSONObject config, String comment) {
          config.put(COMMENT_KEY, comment);
    }
}
