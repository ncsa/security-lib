package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.util.functor.logic.*;
import edu.uiuc.ncsa.security.util.functor.strings.jToLowerCase;
import edu.uiuc.ncsa.security.util.functor.strings.jToUpperCase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * This factory will take JSON and convert it into a set of functors. This supplies basic logic and a few other
 * things, so if you need more, be sure to extends this factory.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:07 AM
 */
public class JFunctorFactory {

    /**
     * This will create a single functor from the object. If you have a full configuration
     * file, use the {@link #createLogicBlock(JSONArray)}
     * method instead.
     *
     * @param jsonObject
     * @return
     */
    public JFunctor create(JSONObject jsonObject) {
        return fromJSON(jsonObject);
    }

    /**
     * This creates a list of logic blocks from a JSONArray. There are a few cases for this. The basic format is
     * assumed to be
     * <pre>
     *     [{"$if":[..],
     *        "$then":[...],
     *        "$else":[...]},
     *      {"$if":[..],
     *        "$then":[...],
     *        "$else":[...]},...
     *     ]
     * </pre>
     * Or a simple list of commands like
     * <pre>
     *     [{"$functor_1":[args]},{"$functor_2":[args]},...]
     * </pre>
     * which is converted to the logical block of
     * <pre>
     *     [{"$if":["$true"],"$then":[commands]}]
     * </pre>
     * I.e it is effectively always evaluated. A Third case that is handled is having these of the form
     * <pre>
     *     [{"$if":...},[COMMANDS]]
     * </pre>
     *
     * @param array
     * @return
     */
    public List<LogicBlock> createLogicBlock(JSONArray array) {
        ArrayList<LogicBlock> bloxx = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            Object currentObj = array.get(i);
            if (currentObj instanceof JSONObject) {
                LogicBlock logicBlock = doLBObject((JSONObject) currentObj);
                if (logicBlock != null) {
                    // only add it if it is recognized as a logic block.
                    bloxx.add(logicBlock);
                }
            }
            if (currentObj instanceof JSONArray) {
                bloxx.add(doLBArray((JSONArray) currentObj));
            }
        }
        if (bloxx.size() == 0) {
            // assume final case, whole thing is a list of commands to be executed at all times
            bloxx.add(doLBArray(array));
        }
        return bloxx;
    }

    protected LogicBlock doLBObject(JSONObject json) {
        if (json.containsKey("$if")) {
            return new LogicBlock(this, json);
        }
        return null;
    }

    protected LogicBlock doLBArray(JSONArray array) {
        JSONObject json = new JSONObject();
        JSONArray array2 = new JSONArray();
        array2.add("$true");
        json.put(FunctorTypeImpl.IF.getValue(), array2);
        json.put(FunctorTypeImpl.THEN.getValue(), array);
        return new LogicBlock(this, json);

    }

    /**
     * This takes an JSONArray of JSONObjects and turns it into a list of {@link JFunctor}s.
     *
     * @param array
     * @return
     */
    public List<JFunctor> create(JSONArray array) {
        ArrayList<JFunctor> bloxx = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (isFunctor(obj)) {
                bloxx.add(fromJSON(obj));
            }
        }

        return bloxx;
    }

    protected boolean hasEnum(JSONObject rawJson, FunctorType type) {
        return rawJson.containsKey(type.getValue());
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
        if (hasEnum(rawJson, AND)) {
            return new jAnd();
        }
        if (hasEnum(rawJson, OR)) {
            return new jOr();
        }
        if (hasEnum(rawJson, NOT)) {
            return new jNot();
        }

        if (hasEnum(rawJson, EXISTS)) {
            return new jExists();
        }
        if (hasEnum(rawJson, MATCH)) {
            return new jMatch();
        }
        if (hasEnum(rawJson, CONTAINS)) {
            return new jContains();
        }

        if (hasEnum(rawJson, ENDS_WITH)) {
            return new jEndsWith();
        }
        if (hasEnum(rawJson, STARTS_WITH)) {
            return new jStartsWith();
        }

        if (hasEnum(rawJson, IF)) {
            return new jIf();
        }

        if (hasEnum(rawJson, THEN)) {
            return new jThen();
        }
        if (hasEnum(rawJson, TRUE)) {
            return new jTrue();
        }
        if (hasEnum(rawJson, FALSE)) {
            return new jFalse();
        }
        if (hasEnum(rawJson, ELSE)) {
            return new jElse();
        }
        if (hasEnum(rawJson, TO_LOWER_CASE)) {
            return new jToLowerCase();
        }
        if (hasEnum(rawJson, TO_UPPER_CASE)) {
            return new jToUpperCase();
        }
        return null;

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
                boolean isDone = false;
                // Special case short hand for logical constants
                if ((obj instanceof String) && (obj).equals("$true")) {
                    ff.addArg(new jTrue());
                    isDone = true;
                }
                if ((obj instanceof String) && (obj).equals("$false")) {
                    ff.addArg(new jFalse());
                    isDone = true;
                }
                if (!isDone) {
                    // so if this argument is not $true or $false, then it's jsut a string and add it.
                    ff.addArg(preprocess(obj.toString()));
                }
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
