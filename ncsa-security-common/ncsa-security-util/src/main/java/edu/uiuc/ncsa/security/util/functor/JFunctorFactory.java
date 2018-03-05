package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.util.functor.logic.*;
import edu.uiuc.ncsa.security.util.functor.strings.jToLowerCase;
import edu.uiuc.ncsa.security.util.functor.strings.jToUpperCase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * This factory will take JSON and convert it into a set of functors. This supplies basic logic and a few other
 * things, so if you need more, be sure to extends this factory.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:07 AM
 */
public class JFunctorFactory {
    public static JFunctorFactory getFactory() {
        if (factory == null) {
            factory = new JFunctorFactory();
        }
        return factory;
    }

    public static void setFactory(JFunctorFactory factory) {
        JFunctorFactory.factory = factory;
    }

    protected static JFunctorFactory factory;

    /**
     * Static factory method. This uses whatever the default factory unless that has been set.
     * If you need dynamic functor creation, consider using the {@link #fromJSON(JSONObject)}
     * method instead.
     *
     * @param jsonObject
     * @return
     */
    public static JFunctor create(JSONObject jsonObject) {
        return getFactory().fromJSON(jsonObject);
    }

    /**
     * This figures out which functor to create based on the key of the raw JSON object. Override
     * this method to add your own creation code, but be sure to call super at some point to get all
     * of the existing functors that come with this module.
     *
     * @param rawJson
     * @return
     */
    protected JFunctor figureOutFunctor(JSONObject rawJson) {
        JFunctor ff = null;
        if (rawJson.containsKey("$and")) {
            ff = new jAnd();
        }
        if (rawJson.containsKey("$or")) {
            ff = new jOr();
        }
        if (rawJson.containsKey("$not")) {
            ff = new jNot();
        }

        if (rawJson.containsKey("$exists")) {
            ff = new jExists();
        }
        if (rawJson.containsKey("$match")) {
            ff = new jMatch();
        }
        if (rawJson.containsKey("$contains")) {
            ff = new jContains();
        }

        if (rawJson.containsKey("$endsWith")) {
            ff = new jEndsWith();
        }
        if (rawJson.containsKey("$startWith")) {
            ff = new jStartsWith();
        }

        if (rawJson.containsKey("$if")) {
            ff = new jIf();
        }

        if (rawJson.containsKey("$then")) {
            ff = new jThen();
        }
        if (rawJson.containsKey("$else")) {
            ff = new jElse();
        }
        if (rawJson.containsKey("$toLowerCase")) {
            ff = new jToLowerCase();
        }
        if (rawJson.containsKey("$toUpperCase")) {
            ff = new jToUpperCase();
        }
        return ff;

    }

    public JFunctor fromJSON(JSONObject rawJson) {
        if (!isFunctor(rawJson)) {
            throw new IllegalArgumentException("Error: not a functor");
        }
        JFunctor ff = figureOutFunctor(rawJson);
        if (ff == null) {
            throw new NotImplementedException("Error: not an implemented functor");
        }
        addArgs(ff, rawJson);
        return ff;

    }

    /**
     * This is invoked to allow for preprocessing each argument before it is added. The contract is that only
     * scalar arguments (e.g. strings, so not functors) are pre-processed. This method simply returns the argument
     * unchanged. Override as needed.
     *
     * @param x
     * @return
     */
    protected String preprocess(String x) {
        return x;
    }

    protected void addArgs(JFunctor ff, JSONObject jsonObject) {
        JSONArray jsonArray = getArray(jsonObject);
        for (int i = 0; i < jsonArray.size(); i++) {
            Object obj = jsonArray.get(i);
            if ((obj instanceof JSONObject)) {
                ff.addArg(fromJSON((JSONObject) obj));
            } else {
                ff.addArg(preprocess(obj.toString()));
            }
        }
    }

    protected JSONArray getArray(JSONObject jsonObject) {
        String key = jsonObject.keySet().iterator().next().toString();
        return jsonObject.getJSONArray(key);
    }

    public boolean isFunctor(JSONObject jsonObject) {
        if (jsonObject.size() != 1) {
            return false;
        }
        String key = jsonObject.keySet().iterator().next().toString();
        return jsonObject.get(key) instanceof JSONArray;
    }
}
