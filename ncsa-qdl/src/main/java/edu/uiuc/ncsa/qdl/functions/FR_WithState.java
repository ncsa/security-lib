package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.extensions.QDLFunctionRecord;
import edu.uiuc.ncsa.qdl.state.AbstractState;
import edu.uiuc.ncsa.qdl.state.XThing;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/26/20 at  7:30 AM
 */
public class FR_WithState implements XThing {
    @Override
    public String getName() {
        return functionRecord.name;
    }

    @Override
    public FKey getKey() {
        return functionRecord.getKey();
    }


    public FR_WithState() {
    }

    public FR_WithState(FunctionRecord functionRecord, AbstractState state, boolean isModule) {
        this(functionRecord, state);
        this.isModule = isModule;
    }

    public FR_WithState(FunctionRecord functionRecord, AbstractState state) {
        this.functionRecord = functionRecord;
        this.state = state;
        isExternalModule = isJavaFunction();
    }
    public boolean hasState(){
        return state!=null;
    }
    public FunctionRecord functionRecord = null;
    public AbstractState state;
    public boolean isExternalModule = false;
    public boolean isModule = false;
    public boolean isJavaFunction(){
        return functionRecord instanceof QDLFunctionRecord;
    }
    @Override
    public String toString() {
        return "FR_WithState{" +
                "functionRecord=" + functionRecord +
                ", state=" + state +
                ", isExternalModule=" + isExternalModule +
                ", isModule=" + isModule +
                '}';
    }
}
