package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.util.functor.logic.jElse;
import edu.uiuc.ncsa.security.util.functor.logic.jIf;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static edu.uiuc.ncsa.security.util.functor.FunctorTypeImpl.*;

/**
 * This class contains a {@link JFunctor} if-then-else block. You supply a JSONObject, this parses it
 * into its correct elements at runtime. You may also use this as a utility to create such blocks by creating the
 * if then else blcks, setting them and invoking the {@link #toJSON()} method.
 * <p>Created by Jeff Gaynor<br>
 * on 2/27/18 at  4:33 PM
 */
public class LogicBlock implements JSONFunctor {
    Boolean result = null;

    @Override
    public Object getResult() {
        return result;
    }

    public LogicBlock(JFunctorFactory factory) {
        this.factory = factory;
        initialized = true; // probably not at this point.
    }

    public LogicBlock(JFunctorFactory factory, jIf ifBlock, jThen thenBlock, jElse elseBlock) {
        this.factory = factory;
        this.ifBlock = ifBlock;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
        initialized = true;
    }

    /**
     * Constructor for case of no else clause.
     *
     * @param ifBlock
     * @param thenBlock
     */
    public LogicBlock(JFunctorFactory factory, jIf ifBlock, jThen thenBlock) {
        this(factory, ifBlock, thenBlock, null);
    }

    jIf ifBlock;

    /**
     * The consequent is either the then or else block, depending on the antecedent (the if block). If this has not
     * executed, then null is returned. Also, if there no else and the if clause is false, it will be empty
     *
     * @return
     */
    public jThen getConsequent() {
        if (!isExecuted()) {
            return null;
        }
        if (isIfTrue()) {
            return thenBlock;
        }
        return elseBlock;
    }

    public boolean hasConsequent(){
        return getConsequent()!=null;
    }
    public jElse getElseBlock() {
        return elseBlock;
    }

    public jThen getThenBlock() {
        return thenBlock;
    }

    public jIf getIfBlock() {
        return ifBlock;
    }

    jElse elseBlock;
    jThen thenBlock;

    JSONObject json;
    JFunctorFactory factory;

    public LogicBlock(JFunctorFactory factory, JSONObject json) {
        this.json = json;
        this.factory = factory;
    }

    /**
     * A jIf functor is a functor with the agreement that the argument in text may consists of a functor (rather than
     * an array with a single functor). This method will enclose the argument in an array if need be.
     *
     * @return
     */
    protected jIf createIfBlock() {
        JSONObject tempJ = new JSONObject();
        Object obj = json.get("$if");
        if (obj instanceof JSONArray) {
            tempJ.put("$if", obj); // convert it to a functor
        }
        if (obj instanceof JSONObject) {
            // float it to an array
            JSONArray array = new JSONArray();
            array.add(obj);
            tempJ.put("$if", array); // convert it to a functor
        }
        jIf jIf = (jIf) factory.fromJSON(tempJ);
        return jIf;
    }

    public boolean isIfTrue() {
        if (ifBlock == null) return false;
        return ifBlock.getBooleanResult();
    }

    protected JFunctor createThenOrElseBlock(FunctorTypeImpl type) {
        JFunctor ff = null;
        switch (type) {
            case ELSE:
                ff = new jElse();

                break;
            case THEN:
                ff = new jThen();
                break;
        }
        if (ff == null) {
            throw new NFWException("Error: Unknown logical type");
        }

        Object obj = json.get(type.getValue());
        JSONArray jsonArray = null;
        if (obj instanceof JSONArray) {
            jsonArray = json.getJSONArray(type.getValue());
        } else {
            jsonArray = new JSONArray();
            jsonArray.add(obj);
        }
        List<JFunctor> functors = factory.create(jsonArray);


        ff.addArg(functors);
        return ff;
    }

    public boolean isExecuted() {
        return executed;
    }

    /**
     * This clears every executed functor in the antecedent and any consequents.
     */
    public void clearState() {
        if (ifBlock != null) {
            ifBlock.clearState();
            if (thenBlock != null) {
                thenBlock.clearState();
            }
            if (elseBlock != null) {
                elseBlock.clearState();
            }
        }
        executed = false;
        results = new ArrayList<>();
    }

    boolean initialized = false;

    protected void initialize() {
        if (initialized) {
            return;
        }
        DebugUtil.trace(this,"initialized, raw JSON = " + json);

        // the assumption is that this object has three elements for if, then and else, plus possibly others.
        if (json.containsKey(IF.getValue())) {
            ifBlock = createIfBlock();
        } else {
            throw new IllegalStateException("Error: cannot find if block. Invalid logic block.");
        }
        if (json.containsKey(THEN.getValue())) {
            thenBlock = (jThen) createThenOrElseBlock(THEN);
        }
        if (json.containsKey(ELSE.getValue())) {
            elseBlock = (jElse) createThenOrElseBlock(ELSE);
        }
        initialized = true;
    }

    protected void addResults(Object obj) {
        if (obj instanceof Collection) {
            results.addAll((Collection<?>) obj);
        } else {
            results.add(obj);
        }

    }

    public ArrayList<Object> getResults() {
        return results;
    }

    ArrayList<Object> results = new ArrayList<>();

    boolean executed = false;

    public Object execute() {
        initialize();

        ifBlock.execute();
        if (ifBlock.getBooleanResult()) {
            thenBlock.execute();
            addResults(thenBlock.getResult());
        } else {
            if (elseBlock != null) {
                elseBlock.execute();
                addResults(elseBlock.getResult());
            }
        }
        executed = true;
        result = ifBlock.getBooleanResult();
        return result;
    }

    @Override
    public String toString() {
        if(json != null) {
            return json.toString();
        }
        return toJSON().toString();
    }

    public void setIfBlock(jIf ifBlock) {
        this.ifBlock = ifBlock;
    }

    public void setThenBlock(jThen thenBlock) {
        this.thenBlock = thenBlock;
    }

    public void setElseBlock(jElse elseBlock) {
        this.elseBlock = elseBlock;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        if (ifBlock == null && json != null) {
            initialize();
        }
        // If it's still null. then there is nothing to do
        if (ifBlock == null) {
            return jsonObject;
        }
        JSONObject tempIf = ifBlock.toJSON();
        jsonObject.put(IF.getValue(), tempIf.getJSONArray(IF.getValue()));
        if (thenBlock != null) {
            JSONObject tempThen = thenBlock.toJSON();
            jsonObject.put(THEN.getValue(), tempThen.getJSONArray(THEN.getValue()));
        }

        if (elseBlock != null) {
            JSONObject tempElse = elseBlock.toJSON();
            jsonObject.put(ELSE.getValue(), tempElse.getJSONArray(ELSE.getValue()));
        }

        return jsonObject;
    }
}
