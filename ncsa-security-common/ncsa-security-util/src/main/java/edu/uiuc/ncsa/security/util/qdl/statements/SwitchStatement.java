package edu.uiuc.ncsa.security.util.qdl.statements;

import java.util.List;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 1/17/20 at  3:42 PM
 */
public class SwitchStatement implements Statement {
    public List<ConditionalStatement> getArguments() {
        return arguments;
    }

    public void setArguments(List<ConditionalStatement> arguments) {
        this.arguments = arguments;
    }

    List<ConditionalStatement> arguments;
    @Override
    public Object evaluate() {
        for(ConditionalStatement c : getArguments()){
            if((Boolean)c.evaluate()){
                return true;
            }
        }
        return false;
    }
}
