package edu.uiuc.ncsa.qdl.statements;

import edu.uiuc.ncsa.qdl.state.State;

/**
 * Local block statments.These have completely local state an know nothing of the ambient space.
 * <p>Created by Jeff Gaynor<br>
 * on 4/8/22 at  12:59 PM
 */
public class LocalBlockStatement extends BlockStatement{
    @Override
    public Object evaluate(State state) {
        State state1 = state.newCleanState();
        for(Statement statement : statements){
            statement.evaluate(state1);
        }
        return null;
    }
}
