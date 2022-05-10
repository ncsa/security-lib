package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;
import edu.uiuc.ncsa.qdl.state.XThing;
import edu.uiuc.ncsa.qdl.variables.VThing;

import java.util.List;

/**
 * Local block statments.These have completely local state an know nothing of the ambient space.
 * <p>Created by Jeff Gaynor<br>
 * on 4/8/22 at  12:59 PM
 */
public class LocalBlockStatement extends BlockStatement{
    @Override
    public Object evaluate(State state) {
        State state1 = state.newCleanState();
        if(hasFunctionParameters()){
            for(XThing xThing: getFunctionParameters()){
                if(xThing instanceof VThing){
                    state1.getVStack().localPut(xThing);
                }else{
                    state1.getFTStack().localPut(xThing);
                }
            }
        }
        for(Statement statement : statements){
            statement.evaluate(state1);
        }
        return null;
    }
    protected boolean hasFunctionParameters(){
        return functionParameters != null;
    }

    /**
     * If this is called in a function.e.g., f(x)->local[...] then this is the set of arguments.
     * These should be added to the state.
     * @return
     */
    public List<XThing> getFunctionParameters() {
        return functionParameters;
    }

    public void setFunctionParameters(List<XThing> functionParameters) {
        this.functionParameters = functionParameters;
    }

    List<XThing> functionParameters;
}
