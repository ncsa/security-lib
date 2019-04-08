package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.core.exceptions.GeneralException;
import edu.uiuc.ncsa.security.core.exceptions.NotImplementedException;
import edu.uiuc.ncsa.security.util.functor.logic.*;
import edu.uiuc.ncsa.security.util.functor.strings.*;
import edu.uiuc.ncsa.security.util.functor.system.jRaiseError;
import edu.uiuc.ncsa.security.util.functor.system.jgetEnv;
import edu.uiuc.ncsa.security.util.functor.system.jsetEnv;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * This factory will take JSON and convert it into a set of functors. This supplies basic logic and a few other
 * things, so if you need more, be sure to extends this factory.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  9:07 AM
 */
public class JFunctorFactory {

    Map<String,String> environment = new HashMap<>();

    public Map<String, String> getEnvironment() {
        return environment;
    }

    /**
     * Get all the tmeplates for all replacements. This is usually the runtime environment but can be anything.
     * @return
     */
    public Map<String, String> getReplacementTemplates() {
        return getEnvironment();
    }

    public void setEnvironment(Map<String, String> environment) {
        this.environment = environment;
    }

    public JFunctorFactory() {
    }

    boolean verboseOn = false;

    public boolean isVerboseOn() {
        return verboseOn;
    }

    public void setVerboseOn(boolean verboseOn) {
        this.verboseOn = verboseOn;
    }

    public JFunctorFactory(boolean verboseOn) {
        this.verboseOn = verboseOn;
    }

    /**
     * This will create a single functor from the object. If you have a full configuration
     * file, use the {@link #createLogicBlock(JSONObject)}
     * method instead.
     *
     * @param jsonObject
     * @return
     */
    public JFunctor create(JSONObject jsonObject) {
        return fromJSON(jsonObject);
    }

    public JFunctor create(String rawJSON) {
        try {
            JSONObject jj = JSONObject.fromObject(rawJSON);
            return create(jj);
        } catch (Throwable t) {
            // ok, see if it's an array
        }
        return null;
    }

    /**
     * Convenience to create logic blocks from a string. This assumes that the string represents a JSON array.
     * If this fails to resolve, this call returns null;
     *
     * @param rawJSON
     * @return
     */
    public LogicBlocks createLogicBlocks(String rawJSON) {
        try {

            JSONArray array = JSONArray.fromObject(rawJSON);
            JSONObject j = new JSONObject();
            j.put(FunctorTypeImpl.OR.getValue(), array);
            return createLogicBlock(j);
        } catch (Throwable t) {
            // do nix
        }
        try {
            JSONObject j = JSONObject.fromObject(rawJSON);
            return createLogicBlock(j);

        } catch (Throwable t) {
            // do nix
        }
        return null;
    }

    /**
     * This creates a list of logic blocks from a JSONArray. There are a few cases for this. The basic format of the blocks
     * is assumed to be
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
     * Now, the full format is a functor of the form
     * <pre>
     *     {"connector":[array]}
     * </pre>
     * where connector is $or, $xor or $and. In the case of or or and, the entire set of blocks will evaluate
     * and the final result will be available. In the case of xor, evaluation will cease when the first if block is
     * found to be false. If there is simply an array and no connector, logical or is supplied as the default.
     *
     * @param jsonObject
     * @return
     */
    public LogicBlocks<? extends LogicBlock> createLogicBlock(JSONObject jsonObject) {
        LogicBlocks<LogicBlock> bloxx = null;
        if (jsonObject.isEmpty()) {
            return new ORLogicBlocks(); // default
        }
        JSONArray array = null;
        if (jsonObject.containsKey(FunctorTypeImpl.OR.getValue())) {
            bloxx = new ORLogicBlocks();
            array = jsonObject.getJSONArray(FunctorTypeImpl.OR.getValue());
        }
        if (jsonObject.containsKey(FunctorTypeImpl.XOR.getValue())) {
            bloxx = new XORLogicBlocks();
            array = jsonObject.getJSONArray(FunctorTypeImpl.XOR.getValue());
        }
        if (jsonObject.containsKey(FunctorTypeImpl.AND.getValue())) {
            bloxx = new ANDLogicBlocks();
            array = jsonObject.getJSONArray(FunctorTypeImpl.AND.getValue());
        }

        if (bloxx == null) {
            throw new IllegalArgumentException("Error: No recognized functor type associated with this logic block collection");
        }
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

    public JMetaMetaFunctor lookUpFunctor(FunctorType type) {
        return lookUpFunctor(type.getValue());
    }

    /**
     * This does the actual work of looking up the functor and creating a new one.
     *
     * @param name
     * @return
     */
    public JMetaMetaFunctor lookUpFunctor(String name) {

        if (name.equals(AND.getValue())) {
            return new jAnd();
        }
        if (name.equals(OR.getValue())) {
            return new jOr();
        }
        if (name.equals(XOR.getValue())) {
            return new jXOr();
        }
        if (name.equals(NOT.getValue())) {
            return new jNot();
        }
        if (name.equals(REPLACE.getValue())) {
            return new jReplace();
        }

        if (name.equals(CONCAT.getValue())) {
            return new jConcat();
        }

        if (name.equals(EXISTS.getValue())) {
            return new jExists();
        }
        if (name.equals(EQUALS.getValue())) {
            return new jEquals();
        }
        if (name.equals(MATCH.getValue())) {
            return new jMatch();
        }
        if (name.equals(TO_ARRAY.getValue())) {
            return new jToArray();
        }
        if (name.equals(CONTAINS.getValue())) {
            return new jContains();
        }

        if (name.equals(ENDS_WITH.getValue())) {
            return new jEndsWith();
        }
        if (name.equals(STARTS_WITH.getValue())) {
            return new jStartsWith();
        }

        if (name.equals(IF.getValue())) {
            return new jIf();
        }

        if (name.equals(THEN.getValue())) {
            return new jThen();
        }
        if (name.equals(TRUE.getValue())) {
            return new jTrue();
        }
        if (name.equals(FALSE.getValue())) {
            return new jFalse();
        }
        if (name.equals(ELSE.getValue())) {
            return new jElse();
        }
        if (name.equals(ECHO.getValue())) {
            return new jEcho(verboseOn);
        }
        if (name.equals(SET_ENV.getValue())) {
               return new jsetEnv(getEnvironment());
           }
        if (name.equals(GET_ENV.getValue())) {
                return new jgetEnv(getEnvironment());
            }
        if (name.equals(TO_LOWER_CASE.getValue())) {
            return new jToLowerCase();
        }
        if (name.equals(TO_UPPER_CASE.getValue())) {
            return new jToUpperCase();
        }
        if (name.equals(DROP.getValue())) {
            return new jDrop();
        }
        if(name.equals(RAISE_ERROR.getValue())){
            return new jRaiseError();
        }
        return null;

    }


    /**
     * This figures out which functor to create based on the key of the raw JSON object. Override
     * this method to add your own creation code, but be sure to call super at some point to get all
     * of the existing functors that come with this module.
     *
     * @param rawJson
     * @return
     */
    protected JMetaMetaFunctor figureOutFunctor(JSONObject rawJson) {
        Iterator iterator = rawJson.keys();
        String functorKey = (String) iterator.next();
        if (iterator.hasNext()) {
            throw new GeneralException("Error: too many functors in this JSON object. There should be exactly one functor");
        }
        return lookUpFunctor(functorKey);
    }

    public JFunctor fromJSON(JSONObject rawJson) {
        if (!isFunctor(rawJson)) {
            throw new IllegalArgumentException("Error: not a functor");
        }
        JMetaMetaFunctor ff = figureOutFunctor(rawJson);
        if (ff == null) {
            throw new NotImplementedException("Error: \"" + rawJson + "\" is not an implemented functor");
        }
        if(!(ff instanceof JSONFunctor)){
            throw new NotImplementedException("Error: The functor is not a JSONFunctor");

        }
        addArgs((JFunctor)ff, rawJson);
        return (JFunctor)ff;

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

    @Override
    public String toString() {
        return "JFunctorFactory{env=" + getEnvironment() + ", verbose? " + verboseOn + "}";
    }
}
