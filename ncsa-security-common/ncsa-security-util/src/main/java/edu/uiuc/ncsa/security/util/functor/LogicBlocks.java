package edu.uiuc.ncsa.security.util.functor;

import edu.uiuc.ncsa.security.util.functor.logic.FunctorMap;
import edu.uiuc.ncsa.security.util.functor.logic.jThen;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 4/20/18 at  10:26 AM
 */
public class LogicBlocks<V extends LogicBlock> extends LinkedList<V> {
    boolean executed = false;

    public FunctorMap getFunctorMap() {
        return functorMap;
    }

    FunctorMap functorMap = new FunctorMap();

    public boolean execute() {
        if (isExecuted()) {
            return true;
        }
        for (LogicBlock lb : this) {
            lb.execute();
            getFunctorMap().putAll(lb.getConsequent().getFunctorMap());
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
        public boolean isExecuted(){
            return executed;
        }
    public List<jThen> getConsequents(){
        LinkedList<jThen> consequents = new LinkedList<>();
        if(!isExecuted()){
            return consequents;
        }
        for(LogicBlock lb : this){
            consequents.add(lb.getConsequent());
        }
        return consequents;
    }
}
