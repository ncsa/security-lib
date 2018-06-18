package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.util.functor.logic.FunctorMap;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/20/18 at  10:26 AM
 */
public class LogicBlocks<V extends LogicBlock> extends LinkedList<V> implements JMetaFunctor {
    @Override
    public Object getResult() {
        return executed;
    }

    boolean executed = false;

    public FunctorMap getFunctorMap() {
        return functorMap;
    }

    FunctorMap functorMap = new FunctorMap();

    public Object execute() {
        if (isExecuted()) {
            return true;
        }
        for (LogicBlock lb : this) {
            lb.execute();
            // It is possible to have a null consequent, e.g. in the case that the conditional
            // is false and there is no else clause. Only do something if something happened.
            if (lb.getConsequent() != null) {
                getFunctorMap().putAll(lb.getConsequent().getFunctorMap());
            }
        }
        executed = true;
        return executed;
    }

    /**
     * Clears each of the execution states of the logic blocks
     */
    public void clearState() {
        for (LogicBlock lb : this) {
            lb.clearState();
        }
        functorMap = new FunctorMap();
        executed = false;
    }

    public boolean isExecuted() {
        return executed;
    }

    public List<jThen> getConsequents() {
        LinkedList<jThen> consequents = new LinkedList<>();
        if (!isExecuted()) {
            return consequents;
        }
        for (LogicBlock lb : this) {
            consequents.add(lb.getConsequent());
        }
        return consequents;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        for (LogicBlock logicBlock : this) {
            array.add(logicBlock.toJSON());
        }
        jsonObject.put("logicBlock", array);
        return jsonObject;
    }
}
