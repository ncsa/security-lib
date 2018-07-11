package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.core.exceptions.NFWException;
import edu.uiuc.ncsa.security.core.util.DebugUtil;
import edu.uiuc.ncsa.security.util.functor.logic.FunctorMap;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * A collection of {@link LogicBlock} objects. A logic block is an if-then-else construct. Executing these
 * will result in a collection of results. There are various sublcasses of this that do various things:
 * {@link XORLogicBlocks}, {@link ANDLogicBlocks} and {@link ORLogicBlocks}.
 * <p>Created by Jeff Gaynor<br>
 * on 4/20/18 at  10:26 AM
 */
public abstract class LogicBlocks<V extends LogicBlock> extends LinkedList<V> implements JMetaFunctor {
    public static final int XOR = 0;
    public static final int OR = 1;
    public static final int AND = 2;
    public static final int UNKNOWN = -1;

    protected int connector = UNKNOWN;

    protected boolean result = false;


    @Override
    public Object getResult() {
        return result;
    }

    boolean executed = false;


    public FunctorMap getFunctorMap() {
        return functorMap;
    }

    FunctorMap functorMap = new FunctorMap();


    public Object execute() {
        DebugUtil.dbg(this, "starting to execute logic blocks, type #=" + connector);
        if (isExecuted()) {
            return true;
        }
        result = false;
        boolean breakLoop = false;
       myLoop:  for (LogicBlock lb : this) {
            //lb.execute();
            boolean rc = false;
            switch (connector) {
                case XOR:
                    rc = doXORCase(lb);
                    breakLoop = rc;
                    break;
                case OR:
                    rc = doORCase(lb);
                    break;
                case AND:
                    rc = doANDCase(lb);
                    break;
                case UNKNOWN:
                    throw new NFWException("Error: there is no connector for this set of logic blocks.");
            }
            if (breakLoop) {
                break myLoop; // be sure to break the right thing!
            }
        }
        executed = true;
        return result;
    }

    /**
     * The logical connector is excluive or. This means that the processing ends as soon as the first block is true.
     * else -- the else result will be added to the functor map, BUT execution will stop.
     * Invoking the XOR connector means to stop processing in this case!!!
     *
     * @param lb
     * @return
     */
    protected boolean doXORCase(LogicBlock lb) {
        lb.execute();
        boolean rc = false; // up to this point, no block has worked, hence this is a valid assumption.
        if (lb.getIfBlock().getBooleanResult()) {
            // this is true, so keep processing
            result = true;
            rc = true;
        } else {
            // this failed, so execution stops.
            result = false;
            rc = false;
        }
        updateFunctormap(lb);
        return rc;
    }

    protected void updateFunctormap(LogicBlock lb) {
        // It is possible to have a null consequent, e.g. in the case that the conditional
        // is false and there is no else clause. Only do something if something happened.

        if (lb.getConsequent() != null) {
            DebugUtil.dbg(this, "Got consequent, adding results to functor map:" + lb.getConsequent().getFunctorMap());
            getFunctorMap().putAll(lb.getConsequent().getFunctorMap());
        }
    }

    /**
     * This will execute every logic block and take the logical OR of all the results. Each consequent will be
     * added
     *
     * @param lb
     * @return
     */
    protected boolean doORCase(LogicBlock lb) {
        lb.execute();
        result = result || lb.getIfBlock().getBooleanResult();
        // It is possible to have a null consequent, e.g. in the case that the conditional
        // is false and there is no else clause. Only do something if something happened.

        updateFunctormap(lb);
        return true; // keep processing
    }

    /**
     * This will execute every logic block and take logicla AND of all the results. Processing continues for all of
     * these. Note that only in the case of XOR is processing interrupted.
     *
     * @param lb
     * @return
     */
    protected boolean doANDCase(LogicBlock lb) {
        lb.execute();
        result = result && lb.getIfBlock().getBooleanResult();
        // It is possible to have a null consequent, e.g. in the case that the conditional
        // is false and there is no else clause. Only do something if something happened.
        updateFunctormap(lb);
        return true; // keep processing
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
