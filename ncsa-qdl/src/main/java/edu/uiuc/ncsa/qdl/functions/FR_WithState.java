package edu.uiuc.ncsa.qdl.functions;

import edu.uiuc.ncsa.qdl.state.AbstractState;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/26/20 at  7:30 AM
 */
public class FR_WithState {
    public FunctionRecord functionRecord;
    public AbstractState state;
    public boolean isExternalModule = false;
    public boolean isModule = false;

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
